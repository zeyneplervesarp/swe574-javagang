package com.swe573.socialhub.repository;

import com.swe573.socialhub.domain.Service;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.enums.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findServiceByCreatedUser(User loggedInUser);
    List<Service> findServiceByCreatedUserAndStatus(User loggedInUser, ServiceStatus status);
}