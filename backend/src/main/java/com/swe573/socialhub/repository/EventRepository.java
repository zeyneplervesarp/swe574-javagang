package com.swe573.socialhub.repository;

import com.swe573.socialhub.domain.Event;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.enums.ServiceStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findEventByCreatedUser(User loggedInUser);
    List<Event> findEventByCreatedUserAndStatus(User loggedInUser, ServiceStatus status);

    @Query("select s from Event s where lower(s.description) like lower(concat('%', :match, '%'))")
    List<Event> findByDescriptionLikeIgnoreCase(@Param("match") String stringToMatch, Pageable pageable);

    @Query("select s from Event s where lower(s.header) like lower(concat('%', :match, '%'))")
    List<Event> findByHeaderLikeIgnoreCase(@Param("match") String stringToMatch, Pageable pageable);

    @Query("select s from Event s where lower(s.location) like lower(concat('%', :match, '%'))")
    List<Event> findByLocationLikeIgnoreCase(@Param("match") String stringToMatch, Pageable pageable);
}
