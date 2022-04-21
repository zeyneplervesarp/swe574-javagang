package com.swe573.socialhub;

import com.swe573.socialhub.domain.Flag;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.dto.ServiceDto;
import com.swe573.socialhub.enums.FlagStatus;
import com.swe573.socialhub.enums.FlagType;
import com.swe573.socialhub.enums.LocationType;
import com.swe573.socialhub.enums.ServiceStatus;
import com.swe573.socialhub.repository.FlagRepository;
import com.swe573.socialhub.repository.UserRepository;
import com.swe573.socialhub.service.ServiceService;
import com.swe573.socialhub.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.security.auth.Subject;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
public class FlagTest {
    @Autowired
    private UserService userService;

    @Autowired
    private ServiceService serviceService;

    @MockBean
    private UserRepository repository;

    @MockBean
    private FlagRepository flagRepository;

    class MockPrincipal implements Principal {

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
        assertNotNull(userService);
    }
/*
    @Test
    public void FlagUser_shouldThrowErrorIfAlreadyFlagged() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test user");

        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setUsername("test user 2");


        var mockUser = new MockPrincipal(testUser.getUsername());
        Mockito.when(repository.findUserByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        Mockito.when(repository.findById(testUser2.getId())).thenReturn(Optional.of(testUser2));
        Mockito.when(repository.findUserByUsername(testUser2.getUsername())).thenReturn(Optional.of(testUser2));
        Mockito.when(flagRepository.findFlagByFlaggingUserAndFlaggedEntityAndType(testUser.getId(), testUser2.getId(), FlagType.user)).thenReturn(Optional.empty());

        Flag flag = new Flag(FlagType.user, testUser.getId(), testUser2.getId());
        flag.setId(1L);
        Mockito.when(flagRepository.save(Mockito.any(Flag.class))).thenReturn(flag);

        service.flagUser(mockUser, testUser2.getId());

        assertThrows(IllegalArgumentException.class, () -> service.flagUser(mockUser, testUser2.getId()));
    }
*/
    @Test
    public void FlagUser_shouldReturnFlag() {
        User testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test user");

        User testUser2 = new User();
        testUser2.setId(2L);
        testUser2.setUsername("test user 2");

        var mockUser = new MockPrincipal(testUser.getUsername());
        Mockito.when(repository.findUserByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        Mockito.when(repository.findById(testUser2.getId())).thenReturn(Optional.of(testUser2));
        Mockito.when(repository.findUserByUsername(testUser2.getUsername())).thenReturn(Optional.of(testUser2));
        Mockito.when(flagRepository.findFlagByFlaggingUserAndFlaggedEntityAndType(testUser.getId(), testUser2.getId(), FlagType.user)).thenReturn(Optional.empty());

        Flag flag = new Flag(FlagType.user, testUser.getId(), testUser2.getId(), FlagStatus.active);
        flag.setId(1L);
        Mockito.when(flagRepository.save(Mockito.any(Flag.class))).thenReturn(flag);

        Flag result = userService.flagUser(mockUser, testUser2.getId());
        assertEquals(testUser.getId(), result.getFlaggingUser());
        assertEquals(testUser2.getId(), result.getFlaggedEntity());
    }

    @Test
    public void FlagService_shouldReturnFlag() {
        var testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("test user");

        ServiceDto testService = new ServiceDto(1L, "Test Service", "", LocationType.Physical, "", LocalDateTime.of(2022, 02, 01, 10, 00), 3, 20, 0, 1L, "", 00.00, 00.00, null, ServiceStatus.ONGOING, null, null, null, null, 0l, false);
        var mockUser = new MockPrincipal(testUser.getUsername());
        Mockito.when(repository.findUserByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        Mockito.when(flagRepository.findFlagByFlaggingUserAndFlaggedEntityAndType(testUser.getId(), testService.getId(), FlagType.service)).thenReturn(Optional.empty());

        Flag flag = new Flag(FlagType.service, testUser.getId(), testService.getId(), FlagStatus.active);
        flag.setId(1L);
        Mockito.when(flagRepository.save(Mockito.any(Flag.class))).thenReturn(flag);

        Flag result = serviceService.flagService(mockUser, testService.getId());
        assertEquals(testUser.getId(), result.getFlaggingUser());
        assertEquals(testService.getId(), result.getFlaggedEntity());
    }



}
