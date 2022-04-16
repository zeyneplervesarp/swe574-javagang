package com.swe573.socialhub.repository.activitystreams;

import com.swe573.socialhub.domain.LoginAttempt;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CreatedQueryableRepository<T> {
    List<T> findAllByCreatedBetween(Date createdGt, Date createdLt, Pageable pageable);
}
