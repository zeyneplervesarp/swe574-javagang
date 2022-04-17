package com.swe573.socialhub.repository.activitystreams;

import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;

public interface DateQueryableRepository<T> {
    List<T> findAllByDateBetween(Date createdGt, Date createdLt, Pageable pageable);
}
