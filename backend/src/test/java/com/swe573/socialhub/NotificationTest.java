package com.swe573.socialhub;

import com.swe573.socialhub.domain.Notification;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.repository.ServiceRepository;
import com.swe573.socialhub.repository.UserRepository;
import com.swe573.socialhub.service.NotificationService;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@TestPropertySource(locations = "classpath:application-test.properties")
class NotificationTest {

    @TestConfiguration
    static class NotificationTestContextConfiguration {
        @Bean
        public NotificationService service() {
            return new NotificationService();
        }
    }

    @Autowired
    private NotificationService service;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    ServiceRepository serviceRepository;

    @Test
    public void contextLoads() throws Exception {
        assertNotNull(service);
    }

    @Test
    public void mapToDto_ReturnsSameProperties() {
        LocalDateTime now = LocalDateTime.now();
        var notification = new Notification(null, "test message", "test", true, new User(), now);
        var dto = service.mapNotificationToDTO(notification);
        assertEquals(notification.getMessage(), dto.getMessage());
        assertEquals(notification.getMessageUrl(), dto.getMessageBody());
        assertEquals(notification.getRead(), dto.getRead());
        assertEquals(notification.getSentDate(), dto.getSentDate());
    }

    @Test
    public void getNotificationsOrdered() {

    }
}