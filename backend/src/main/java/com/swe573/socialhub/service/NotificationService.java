package com.swe573.socialhub.service;

import com.swe573.socialhub.domain.Notification;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.dto.NotificationDto;
import com.swe573.socialhub.repository.NotificationRepository;
import com.swe573.socialhub.repository.UserRepository;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository repository;

    @Autowired
    private UserRepository userRepository;

    public void sendNotification(String message, String url, User user) {
        var notification = new Notification(null, message, url, false, user, LocalDateTime.now());
        repository.save(notification);
    }

    public NotificationDto mapNotificationToDTO(Notification notification) {
        return new NotificationDto(notification.getId(), notification.getMessage(), notification.getMessageUrl(), notification.getRead(), notification.getSentDate());
    }

    public List<NotificationDto> getListByUser(Principal principal) {
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");
        try {
            var notifications = loggedInUser.getNotificationSet();
            var dtoList = notifications.stream().map(notification -> mapNotificationToDTO(notification)).collect(Collectors.toList());
            Collections.sort(dtoList);
            return Collections.unmodifiableList(dtoList);
        } catch (DataException e) {
            throw new IllegalArgumentException("There was a problem trying to save service to db");
        }

    }

    public Boolean setStatusToRead(Principal principal) {
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");
        try {
            var notifications = loggedInUser.getNotificationSet();
            for (Notification notification : notifications)
            {
                notification.setRead(true);
                repository.save(notification);
            }
            return true;
        } catch (DataException e) {
            throw new IllegalArgumentException("There was a problem trying to save service to db");
        }
    }
}
