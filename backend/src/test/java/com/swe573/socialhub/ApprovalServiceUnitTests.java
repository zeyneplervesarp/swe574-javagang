package com.swe573.socialhub;


import com.swe573.socialhub.domain.Service;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.repository.ServiceRepository;
import com.swe573.socialhub.repository.UserRepository;
import com.swe573.socialhub.service.ServiceService;
import com.swe573.socialhub.service.UserService;
import com.swe573.socialhub.service.UserServiceApprovalService;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ApprovalServiceUnitTests {

    @TestConfiguration
    static class NotificationTestContextConfiguration {
        @Bean
        UserServiceApprovalService service() {
            return new UserServiceApprovalService();
        }
    }

    @Autowired
    UserServiceApprovalService service;

    @MockBean
    ServiceService serviceService;

    @MockBean
    UserService userService;

    @MockBean
    ServiceRepository serviceRepository;

    @MockBean
    UserRepository userRepository;

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
    public void UserServiceApproval_ShouldThrowError_IfCreditBelowThreshold() {
        var testUser = new User();
        testUser.setBalance(-2);
        testUser.setId(1L);
        testUser.setUsername("test user");

        var testService = new Service();
        testService.setCredit(5);
        testService.setId(1L);
        testService.setHeader("test service");

        var mockUser = new MockPrincipal(testUser.getUsername());
        Mockito.when(userRepository.findUserByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        Mockito.when(serviceRepository.findById(testService.getId())).thenReturn(Optional.of(testService));
        Mockito.when(userService.getBalanceToBe(testUser)).thenReturn(testUser.getBalance());

        assertThrows(IllegalArgumentException.class, () -> service.RequestApproval(mockUser, testService.getId()));
    }

}
