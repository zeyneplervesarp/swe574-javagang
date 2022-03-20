package com.swe573.socialhub.repository;

import com.swe573.socialhub.domain.*;
import com.swe573.socialhub.domain.key.UserEventApprovalKey;
import com.swe573.socialhub.enums.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserEventApprovalRepository extends JpaRepository<UserEventApproval, UserEventApprovalKey> {
    Optional<UserEventApproval> findUserEventApprovalByEventAndUser(Event event, User user);
    List<UserEventApproval> findUserEventApprovalByEvent_CreatedUserAndApprovalStatus(User user, ApprovalStatus status);
    Optional<UserEventApproval> findUserEventApprovalByEvent_IdAndUser_Id(Long eventId, Long userId);
    List<UserEventApproval> findUserEventApprovalByEvent_IdAndApprovalStatus(Long eventId, ApprovalStatus status);
    List<UserEventApproval> findUserEventApprovalByUserAndApprovalStatus(User user, ApprovalStatus status);
}
