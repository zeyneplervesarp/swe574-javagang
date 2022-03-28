package com.swe573.socialhub;


import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.dto.ServiceDto;
import com.swe573.socialhub.enums.ServiceStatus;
import com.swe573.socialhub.repository.ServiceRepository;
import com.swe573.socialhub.repository.TagRepository;
import com.swe573.socialhub.repository.UserRepository;
import com.swe573.socialhub.repository.UserServiceApprovalRepository;
import com.swe573.socialhub.service.NotificationService;
import com.swe573.socialhub.service.ServiceService;
import com.swe573.socialhub.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.security.auth.Subject;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ServiceServiceUnitTests {

    @TestConfiguration
    static class ServiceServiceUnitTestsConfiguration {
        @Bean
        ServiceService service()
        {

            return new ServiceService();
        }
    }
    @Autowired
    private ServiceService service;


    @MockBean
    private ServiceRepository serviceRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TagRepository tagRepository;

    @MockBean
    private UserServiceApprovalRepository approvalRepository;

    @MockBean
    private NotificationService notificationService;
    @MockBean
    UserService userService;

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
        assertNotNull(service);
    }

    @Test
    public void Service_ShouldThrowError_IfCreditAboveThreshold() {
        var testUser = new User();
        testUser.setBalance(18);
        testUser.setId(1L);
        testUser.setUsername("test user");

        var testService = new ServiceDto(1L, "Test Service", ",", "", LocalDateTime.of(2022, 02, 01, 10, 00), 3, 20, 0, 1L, "", 00.00, 00.00, null, ServiceStatus.ONGOING, null, null, null, null, 0l);


        var mockUser = new MockPrincipal(testUser.getUsername());
        Mockito.when(userRepository.findUserByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        Mockito.when(userService.getBalanceToBe(testUser)).thenReturn(testUser.getBalance());


        assertThrows(IllegalArgumentException.class, () -> service.save(mockUser, testService));
    }
}
