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

    @Query("select s from Service s where s.created > :createdGt and s.created < :createdLt")
    List<Service> findAllByDateBetween(Date createdGt, Date createdLt, Pageable pageable);

    @Query("select s from Service s where s.created > :createdGt and s.created < :createdLt and s.time > CURRENT_DATE")
    List<Service> findAllByDateBetweenOngoing(Date createdGt, Date createdLt, Pageable pageable);

    @Query("select count(s) from Service s where s.created > :createdGt and s.created < :createdLt")
    long countByDateBetween(Date createdGt, Date createdLt);
}