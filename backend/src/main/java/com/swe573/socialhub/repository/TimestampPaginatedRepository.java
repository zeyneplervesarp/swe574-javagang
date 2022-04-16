package com.swe573.socialhub.repository;

import com.swe573.socialhub.dto.TimestampBasedPagination;

import java.util.List;

public class TimestampPaginatedRepository<T> {

    public TimestampPaginatedRepository(CreatedQueryableRepository<T> innerRepository) {
        this.innerRepository = innerRepository;
    }

    private final CreatedQueryableRepository<T> innerRepository;

    public List<T> findAllMatching(TimestampBasedPagination pagination) {
        return innerRepository
                .findAllByCreatedBetween(pagination.getGreaterThan(), pagination.getLowerThan(), pagination.toPageable());
    }

    public CreatedQueryableRepository<T> getInnerRepository() {
        return innerRepository;
    }
}
