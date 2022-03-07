package com.swe573.socialhub.repository;

import com.swe573.socialhub.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}