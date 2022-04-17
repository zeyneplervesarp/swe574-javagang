package com.swe573.socialhub.repository;

import com.swe573.socialhub.domain.LoginAttempt;
import com.swe573.socialhub.repository.activitystreams.DateQueryableRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long>, DateQueryableRepository<LoginAttempt> {
    @Query("select s from LoginAttempt s where s.created > :createdGt and s.created < :createdLt")
    List<LoginAttempt> findAllByDateBetween(@Param("createdGt") Date createdGt, @Param("createdLt") Date createdLt, Pageable pageable);

    @Query("select s from LoginAttempt s where s.created > :createdGt and s.created < :createdLt and s.attemptType = com.swe573.socialhub.enums.LoginAttemptType.SUCCESSFUL")
    List<LoginAttempt> findAllSuccessfulByCreatedBetween(@Param("createdGt") Date createdGt, @Param("createdLt") Date createdLt, Pageable pageable);

    @Query("select s from LoginAttempt s where s.created > :createdGt and s.created < :createdLt and (s.attemptType = com.swe573.socialhub.enums.LoginAttemptType.WRONG_PASSWORD or s.attemptType = com.swe573.socialhub.enums.LoginAttemptType.WRONG_USERNAME)")
    List<LoginAttempt> findAllUnsuccessfulByCreatedBetween(@Param("createdGt") Date createdGt, @Param("createdLt") Date createdLt, Pageable pageable);
}
