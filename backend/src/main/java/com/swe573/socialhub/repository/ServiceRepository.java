package com.swe573.socialhub.repository;

import com.swe573.socialhub.domain.Service;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.enums.ServiceStatus;
import com.swe573.socialhub.repository.activitystreams.DateCountableRepository;
import com.swe573.socialhub.repository.activitystreams.DateQueryableRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


public interface ServiceRepository extends JpaRepository<Service, Long>, DateQueryableRepository<Service>, DateCountableRepository {
    List<Service> findServiceByCreatedUser(User loggedInUser);
    List<Service> findServiceByCreatedUserAndStatus(User loggedInUser, ServiceStatus status);

    @Query("select s from Service s where lower(s.description) like lower(concat('%', :match, '%'))")
    List<Service> findByDescriptionLikeIgnoreCase(@Param("match") String stringToMatch, Pageable pageable);

    @Query("select s from Service s where lower(s.header) like lower(concat('%', :match, '%'))")
    List<Service> findByHeaderLikeIgnoreCase(@Param("match") String stringToMatch, Pageable pageable);

    @Query("select s from Service s where lower(s.location) like lower(concat('%', :match, '%'))")
    List<Service> findByLocationLikeIgnoreCase(@Param("match") String stringToMatch, Pageable pageable);

    @Query("select s from Service s where s.isFeatured = true")
    List<Service> findFeatured();

    String HAVERSINE_DISTANCE = "(6371 * function('acos',(function('cos',(function('radians',(:latStr)))))   * function('cos',(function('radians',(  s.latitude)))) * function('cos',(function('radians',(s.longitude)) - function('radians',(:lngStr)))) + function('sin',(function('radians',(:latStr)))) * function('sin',function('radians', s.latitude))))";

    @Query("select s from Service s where s.created > :createdGt and s.created < :createdLt")
    List<Service> findAllByDateBetween(Date createdGt, Date createdLt, Pageable pageable);

    @Query("select s from Service s where s.created > :createdGt and s.created < :createdLt and s.time > CURRENT_DATE")
    List<Service> findAllByDateBetweenOngoing(Date createdGt, Date createdLt, Pageable pageable);

    @Query("select s from Service s where s.created > :createdGt and s.created < :createdLt and s.createdUser.id = :userId")
    List<Service> findAllByDateBetweenCreatedByUser(Date createdGt, Date createdLt, Pageable pageable, Long userId);

    @Query("select s from Service s where s.created > :createdGt and s.created < :createdLt and s.createdUser.id in :userIds")
    List<Service> findAllByDateBetweenCreatedByUsers(Date createdGt, Date createdLt, Pageable pageable, List<Long> userIds);

    @Query("select s from Service s JOIN s.approvalSet a where s.created > :createdGt and s.created < :createdLt and a.approvalStatus = com.swe573.socialhub.enums.ApprovalStatus.APPROVED and a.user.id = :userId")
    List<Service> findAllByDateBetweenAttendingByUser(Date createdGt, Date createdLt, Pageable pageable, Long userId);

    @Query("select s from Service s where s.created > :createdGt and s.created < :createdLt and s.createdUser.id = :userId and s.time > CURRENT_DATE")
    List<Service> findAllByDateBetweenCreatedByUserOngoing(Date createdGt, Date createdLt, Pageable pageable, Long userId);

    @Query("select s from Service s where s.created > :createdGt and s.created < :createdLt and s.createdUser.id in :userIds and s.time > CURRENT_DATE")
    List<Service> findAllByDateBetweenCreatedByUsersOngoing(Date createdGt, Date createdLt, Pageable pageable, List<Long> userIds);

    @Query("select s from Service s JOIN s.approvalSet a where s.created > :createdGt and s.created < :createdLt and a.approvalStatus = com.swe573.socialhub.enums.ApprovalStatus.APPROVED and a.user.id = :userId and s.time > CURRENT_DATE")
    List<Service> findAllByDateBetweenAttendingByUserOngoing(Date createdGt, Date createdLt, Pageable pageable, Long userId);

    @Query("select s from Service s where  " + HAVERSINE_DISTANCE + "  > :distanceGt and  " + HAVERSINE_DISTANCE + "  < :distanceLt order by  " + HAVERSINE_DISTANCE + "  asc")
    List<Service> findAllByDistanceBetween(Double distanceGt, Double distanceLt, Pageable pageable, String latStr, String lngStr);

    @Query("select s from Service s where  " + HAVERSINE_DISTANCE + "  > :distanceGt and  " + HAVERSINE_DISTANCE + "  < :distanceLt and (s.time > current_date) order by  " + HAVERSINE_DISTANCE + "  asc")
    List<Service> findAllByDistanceBetweenOngoing(Double distanceGt, Double distanceLt, Pageable pageable, String latStr, String lngStr);

    @Query("select s from Service s where  " + HAVERSINE_DISTANCE + "  > :distanceGt and  " + HAVERSINE_DISTANCE + "  < :distanceLt and (s.createdUser.id = :userId) order by  " + HAVERSINE_DISTANCE + "  asc")
    List<Service> findAllByDistanceBetweenCreatedByUser(Double distanceGt, Double distanceLt, Pageable pageable, Long userId, String latStr, String lngStr);

    @Query("select s from Service s where  " + HAVERSINE_DISTANCE + "  > :distanceGt and  " + HAVERSINE_DISTANCE + "  < :distanceLt and (s.createdUser.id in :userIds) order by  " + HAVERSINE_DISTANCE + "  asc")
    List<Service> findAllByDistanceBetweenCreatedByUsers(Double distanceGt, Double distanceLt, Pageable pageable, List<Long> userIds, String latStr, String lngStr);

    @Query("select s from Service s JOIN s.approvalSet a where  " + HAVERSINE_DISTANCE + "  > :distanceGt and  " + HAVERSINE_DISTANCE + "  < :distanceLt and a.approvalStatus = com.swe573.socialhub.enums.ApprovalStatus.APPROVED and (a.user.id = :userId) order by  " + HAVERSINE_DISTANCE + "  asc")
    List<Service> findAllByDistanceBetweenAttendingByUser(Double distanceGt, Double distanceLt, Pageable pageable, Long userId, String latStr, String lngStr);

    @Query("select s from Service s where  " + HAVERSINE_DISTANCE + "  > :distanceGt and  " + HAVERSINE_DISTANCE + "  < :distanceLt and s.createdUser.id = :userId and (s.time > CURRENT_DATE) order by  " + HAVERSINE_DISTANCE + "  asc")
    List<Service> findAllByDistanceBetweenCreatedByUserOngoing(Double distanceGt, Double distanceLt, Pageable pageable, Long userId, String latStr, String lngStr);

    @Query("select s from Service s where  " + HAVERSINE_DISTANCE + "  > :distanceGt and  " + HAVERSINE_DISTANCE + "  < :distanceLt and s.createdUser.id in :userIds and (s.time > CURRENT_DATE) order by  " + HAVERSINE_DISTANCE + "  asc")
    List<Service> findAllByDistanceBetweenCreatedByUsersOngoing(Double distanceGt, Double distanceLt, Pageable pageable, List<Long> userIds, String latStr, String lngStr);

    @Query("select s from Service s JOIN s.approvalSet a where  " + HAVERSINE_DISTANCE + "  > :distanceGt and  " + HAVERSINE_DISTANCE + "  < :distanceLt and s.time > CURRENT_DATE and a.approvalStatus = com.swe573.socialhub.enums.ApprovalStatus.APPROVED and (a.user.id = :userId) order by  " + HAVERSINE_DISTANCE + "  asc")
    List<Service> findAllByDistanceBetweenAttendingByUserOngoing(Double distanceGt, Double distanceLt, Pageable pageable, Long userId, String latStr, String lngStr);

    @Query("select count(s) from Service s where s.created > :createdGt and s.created < :createdLt")
    long countByDateBetween(Date createdGt, Date createdLt);
}