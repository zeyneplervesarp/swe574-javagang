package com.swe573.socialhub.repository;

import com.swe573.socialhub.domain.Service;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.domain.UserServiceApproval;
import com.swe573.socialhub.domain.key.UserServiceApprovalKey;
import com.swe573.socialhub.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserServiceApprovalRepository extends JpaRepository<UserServiceApproval, UserServiceApprovalKey> {
    Optional<UserServiceApproval> findUserServiceApprovalByServiceAndUser(Service service, User user);
    List<UserServiceApproval> findUserServiceApprovalByService_CreatedUserAndApprovalStatus(User user, ApprovalStatus status);
    Optional<UserServiceApproval> findUserServiceApprovalByService_IdAndUser_Id(Long serviceId, Long userId);
    List<UserServiceApproval> findUserServiceApprovalByService_IdAndApprovalStatus(Long serviceId, ApprovalStatus status);
    List<UserServiceApproval> findUserServiceApprovalByUserAndApprovalStatus(User user, ApprovalStatus status);



}