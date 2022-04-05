package com.swe573.socialhub;

import com.swe573.socialhub.domain.Service;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.domain.UserFollowing;
import com.swe573.socialhub.domain.UserServiceApproval;
import com.swe573.socialhub.domain.key.UserServiceApprovalKey;
import com.swe573.socialhub.dto.UserDto;
import com.swe573.socialhub.enums.ApprovalStatus;
import com.swe573.socialhub.enums.ServiceStatus;
import com.swe573.socialhub.enums.UserType;
import com.swe573.socialhub.repository.*;
import com.swe573.socialhub.service.NotificationService;
import com.swe573.socialhub.service.RatingService;
import com.swe573.socialhub.service.UserService;
import com.swe573.socialhub.config.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.security.auth.Subject;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserServiceUnitTests {

    @TestConfiguration
    static class ServiceServiceUnitTestsConfiguration {
        @Bean
        UserService service() {
            return new UserService();
        }
    }

    @Autowired
    private UserService service;

    @MockBean
    private UserRepository repository;

    @MockBean
    private TagRepository tagRepository;

    @MockBean
    private ServiceRepository serviceRepository;

    @MockBean
    private UserServiceApprovalRepository userServiceApprovalRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenUtil jwtTokenUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private RatingService ratingService;

    @MockBean
    private UserFollowingRepository userFollowingRepository;

    static class MockPrincipal implements Principal {

        public MockPrincipal(String name) {
            this.name = name;
        }

        final String name;

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean implies(Subject subject) {
            return Principal.super.implies(subject);
        }
    }

    @Test
    public void contextLoads() throws Exception {
        assertNotNull(service);
    }

    @Test
    public void Service_GetBalance_ShouldReturn_BalanceToBe() {
        var testUser = new User();
        testUser.setBalance(18);
        testUser.setId(1L);
        testUser.setUsername("test user");

        var testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setUsername("test user 2");


        var testService1 = new Service();
        testService1.setCredit(ThreadLocalRandom.current().nextInt(1, 10));
        testService1.setId(new Random().nextLong());

        var testService2 = new Service();
        testService2.setCredit(ThreadLocalRandom.current().nextInt(1, 10));
        testService2.setId(new Random().nextLong());

        var testApproval1Key = new UserServiceApprovalKey(testUser.getId(), testService2.getId());
        var testApproval = new UserServiceApproval();
        testApproval.setId(testApproval1Key);
        testApproval.setService(testService2);
        testApproval.setUser(testUser);
        testApproval.setApprovalStatus(ApprovalStatus.PENDING);

        var testServices = new ArrayList<Service>() {{
            add(testService2);
        }};

        var testApprovals = new ArrayList<UserServiceApproval>() {{
            add(testApproval);
        }};


        Mockito.when(userServiceApprovalRepository.findUserServiceApprovalByUserAndApprovalStatus(testUser, ApprovalStatus.PENDING)).thenReturn(testApprovals);
        Mockito.when(serviceRepository.findServiceByCreatedUserAndStatus(testUser, ServiceStatus.ONGOING)).thenReturn(testServices);

        var expectedResult = testUser.getBalance() + testService2.getCredit() + testApproval.getService().getCredit();

        var result = service.getBalanceToBe(testUser);
        assertEquals(expectedResult, result);

    }

    @Test
    public void FollowUser_ShouldThrowError_IfAlreadyFollowing() {
        var testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setUsername("test user 2");

        var testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test user");
        var following = new UserFollowing(testUser, testUser2);

        testUser.setFollowingUsers(new HashSet<>() {{
            add(following);
        }});

        var mockUser = new MockPrincipal(testUser.getUsername());

        Mockito.when(repository.findUserByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        Mockito.when(repository.findById(testUser2.getId())).thenReturn(Optional.of(testUser2));
        Mockito.when(repository.findUserByUsername(testUser2.getUsername())).thenReturn(Optional.of(testUser2));
        Mockito.when(userFollowingRepository.findUserFollowingByFollowingUserAndFollowedUser(Mockito.any(User.class), Mockito.any(User.class))).thenReturn(Optional.of(following));

        assertThrows(IllegalArgumentException.class, () -> service.follow(mockUser, testUser2.getId()));

    }

    @Test
    public void FollowUser_ShouldReturnEntity() {
        var testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setUsername("test user 2");

        var testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test user");


        var mockUser = new MockPrincipal(testUser.getUsername());

        Mockito.when(repository.findUserByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        Mockito.when(repository.findById(testUser2.getId())).thenReturn(Optional.of(testUser2));
        Mockito.when(repository.findUserByUsername(testUser2.getUsername())).thenReturn(Optional.of(testUser2));
        Mockito.when(userFollowingRepository.findUserFollowingByFollowingUserAndFollowedUser(testUser, testUser2)).thenReturn(Optional.empty());

        var userFollowing = new UserFollowing(testUser, testUser2);
        userFollowing.setId(1L);
        Mockito.when(userFollowingRepository.save(Mockito.any(UserFollowing.class))).thenReturn(userFollowing);


        var result = service.follow(mockUser, testUser2.getId());
        assertEquals(testUser, result.getFollowingUser());
        assertEquals(testUser2, result.getFollowedUser());

    }

    @Test
    public void Register_ShouldThrowError_WhenDataIsInvalid() {
        var testUser = new UserDto(null, "test", "test", "test", 0, null, 0, "", "", "", null, null,null, null, UserType.USER, 0);
        assertThrows(IllegalArgumentException.class, () -> service.register(testUser));
        testUser.setPassword("123456");
        testUser.setUsername("");
        assertThrows(IllegalArgumentException.class, () -> service.register(testUser));
        testUser.setUsername("test");
        testUser.setEmail("");
        assertThrows(IllegalArgumentException.class, () -> service.register(testUser));
        testUser.setEmail("test");
        testUser.setBio("");
        assertThrows(IllegalArgumentException.class, () -> service.register(testUser));
    }

    @Test
    public void Register_ShouldReturnEntity() {
        var testUser = new UserDto(null, "test", "test", "test", 0, null, 0, "", "", "", null, null,null, null,  UserType.USER, 0);
        testUser.setPassword("123456");
        Mockito.when(passwordEncoder.encode(testUser.getPassword())).thenReturn("testHash");
        var user = new User(null,testUser.getUsername(),testUser.getEmail(),testUser.getBio(),null,0,"","","", UserType.USER);
        Mockito.when(repository.save(Mockito.any(User.class))).thenReturn(user);
        assertEquals(testUser.getUsername(), user.getUsername());
        assertEquals(testUser.getBio(), user.getBio());
        assertEquals(testUser.getEmail(), user.getEmail());
    }

    @Test
    public void Register_ShouldReturnUserType() {

        var testUser = new UserDto(null, "test", "test", "test", 0, null, 0, "", "", "", null, null,null, null,  UserType.USER, 0);
        testUser.setPassword("123456");
        Mockito.when(passwordEncoder.encode(testUser.getPassword())).thenReturn("testHash");
        var user = new User(null,testUser.getUsername(),testUser.getEmail(),testUser.getBio(),null,0,"","","", UserType.USER);
        Mockito.when(repository.save(Mockito.any(User.class))).thenReturn(user);
        assertEquals(testUser.getUserType(), UserType.USER);
    }

    @Test
    public void MapToDto_ShouldReturnSameFields()
    {
        var user = new User(new Random().nextLong(),"testUsername","testMail","testBio",null,new Random().nextInt(15),"testLatitude","tetstLongitude","testAddress", UserType.USER);
        user.setFollowingUsers(new HashSet<>());
        user.setFollowedBy(new HashSet<>());


        Mockito.when(userServiceApprovalRepository.findUserServiceApprovalByUserAndApprovalStatus(user, ApprovalStatus.PENDING)).thenReturn(new ArrayList<>());
        var dto = service.mapUserToDTO(user);

        assertEquals(user.getUsername(),dto.getUsername());
        assertEquals(user.getId(),dto.getId());
        assertEquals(user.getEmail(),dto.getEmail());
        assertEquals(user.getBio(),dto.getBio());
        assertEquals(user.getBalance(),dto.getBalance());
        assertEquals(user.getLatitude(),dto.getLatitude());
        assertEquals(user.getLongitude(),dto.getLongitude());
        assertEquals(user.getFormattedAddress(),dto.getFormattedAddress());
        assertEquals(user.getUserType(),dto.getUserType());
    }
}
