package com.swe573.socialhub.repository;

import com.swe573.socialhub.domain.Service;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.domain.UserServiceApproval;
import com.swe573.socialhub.domain.key.UserServiceApprovalKey;
import com.swe573.socialhub.enums.ApprovalStatus;
import com.swe573.socialhub.repository.activitystreams.DateCountableRepository;
import com.swe573.socialhub.repository.activitystreams.DateQueryableRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserServiceApprovalRepository extends JpaRepository<UserServiceApproval, UserServiceApprovalKey>, DateQueryableRepository<UserServiceApproval>, DateCountableRepository {
    Optional<UserServiceApproval> findUserServiceApprovalByServiceAndUser(Service service, User user);
    List<UserServiceApproval> findUserServiceApprovalByService_CreatedUserAndApprovalStatus(User user, ApprovalStatus status);
    Optional<UserServiceApproval> findUserServiceApprovalByService_IdAndUser_Id(Long serviceId, Long userId);
    List<UserServiceApproval> findUserServiceApprovalByService_IdAndApprovalStatus(Long serviceId, ApprovalStatus status);
    List<UserServiceApproval> findUserServiceApprovalByUserAndApprovalStatus(User user, ApprovalStatus status);

    @Query("select s from UserServiceApproval s where s.approvedDate is not null and s.approvedDate > :createdGt and s.approvedDate < :createdLt")
    List<UserServiceApproval> findAllByApprovedDateBetween(Date createdGt, Date createdLt, Pageable pageable);

    @Query("select s from UserServiceApproval s where s.created > :createdGt and s.created < :createdLt")
    List<UserServiceApproval> findAllByDateBetween(Date createdGt, Date createdLt, Pageable pageable);

    @Query("select count(s) from UserServiceApproval s where s.created > :createdGt and s.created < :createdLt")
    long countByDateBetween(Date createdGt, Date createdLt);

    @Query("select count(s) from UserServiceApproval s where s.approvedDate is not null and s.approvedDate > :createdGt and s.approvedDate < :createdLt")
    Long countAllApprovedByDateBetween(Date createdGt, Date createdLt);
}