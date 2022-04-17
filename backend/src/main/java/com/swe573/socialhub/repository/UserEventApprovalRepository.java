package com.swe573.socialhub.repository;

import com.swe573.socialhub.domain.*;
import com.swe573.socialhub.domain.key.UserEventApprovalKey;
import com.swe573.socialhub.enums.ApprovalStatus;
import com.swe573.socialhub.repository.activitystreams.DateQueryableRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserEventApprovalRepository extends JpaRepository<UserEventApproval, UserEventApprovalKey>, DateQueryableRepository<UserEventApproval> {
    Optional<UserEventApproval> findUserEventApprovalByEventAndUser(Event event, User user);
    List<UserEventApproval> findUserEventApprovalByEvent_CreatedUserAndApprovalStatus(User user, ApprovalStatus status);
    Optional<UserEventApproval> findUserEventApprovalByEvent_IdAndUser_Id(Long eventId, Long userId);
    List<UserEventApproval> findUserEventApprovalByEvent_IdAndApprovalStatus(Long eventId, ApprovalStatus status);
    List<UserEventApproval> findUserEventApprovalByUserAndApprovalStatus(User user, ApprovalStatus status);

    @Query("select s from UserEventApproval s where s.approvedDate is not null and s.approvedDate > :createdGt and s.approvedDate < :createdLt")
    List<UserEventApproval> findAllByApprovedDateBetween(Date createdGt, Date createdLt, Pageable pageable);

    @Query("select s from UserEventApproval s where s.created > :createdGt and s.created < :createdLt")
    List<UserEventApproval> findAllByDateBetween(Date createdGt, Date createdLt, Pageable pageable);
}
