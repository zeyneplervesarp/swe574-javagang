package com.swe573.socialhub.repository;

import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.repository.activitystreams.DateCountableRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, DateCountableRepository {
    Optional<User> findUserByUsername(String userName);
    @Query("select s from User s where lower(s.username) like lower(concat('%', :match, '%'))")
    List<User> findByUsernameLikeIgnoreCase(@Param("match") String stringToMatch, Pageable pageable);

    @Query("select s from User s where lower(s.bio) like lower(concat('%', :match, '%'))")
    List<User> findByBioLikeIgnoreCase(@Param("match") String stringToMatch, Pageable pageable);

    @Query("select s from User s where s.username in ?1")
    List<User> findAllByUsername(List<String> userNames);

    @Query("select count(s) from User s where s.created > :createdGt and s.created < :createdLt")
    long countByDateBetween(Date createdGt, Date createdLt);
}