package com.swe573.socialhub.repository.activitystreams;

import com.swe573.socialhub.dto.TimestampBasedPagination;

import java.util.List;

public class TimestampPaginatedRepository<T> {

    public TimestampPaginatedRepository(DateQueryableRepository<T> innerRepository) {
        this.innerRepository = innerRepository;
    }

    private final DateQueryableRepository<T> innerRepository;

    public List<T> findAllMatching(TimestampBasedPagination pagination) {
        return innerRepository
                .findAllByDateBetween(pagination.getGreaterThan(), pagination.getLowerThan(), pagination.toPageable());
    }

    public DateQueryableRepository<T> getInnerRepository() {
        return innerRepository;
    }
}
