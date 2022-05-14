package com.swe573.socialhub.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Date;

public class TimestampBasedPagination {
    private final Date greaterThan;
    private final Date lowerThan;
    private final int size;
    private final Sort.Direction sortDirection;

    private static final Date DEFAULT_GT = new Date(0L);
    private static final Date DEFAULT_LT = new Date(System.currentTimeMillis() + System.currentTimeMillis());

    public TimestampBasedPagination(Date greaterThan, Date lowerThan, int size, Sort.Direction sortDirection) {
        if (size < 1) throw new IllegalArgumentException("Size must be greater than 1");
        this.greaterThan = greaterThan != null ? greaterThan : DEFAULT_GT;
        this.lowerThan = lowerThan != null ? lowerThan : DEFAULT_LT;
        this.size = size;
        this.sortDirection = sortDirection != null ? sortDirection : Sort.Direction.ASC;
    }

    public Date getGreaterThan() {
        return greaterThan;
    }

    public Date getLowerThan() {
        return lowerThan;
    }

    public int getSize() {
        return size;
    }

    public Sort.Direction getSortDirection() {
        return sortDirection;
    }

    public Pageable toPageable() {
        return toPageable("created");
    }

    public Pageable toPageable(String sortByField) {
        var sort = Sort.by(sortByField);
        if (sortDirection == Sort.Direction.ASC) {
            sort = sort.ascending();
        } else {
            sort = sort.descending();
        }
        return PageRequest.of(0, size, sort);
    }

    public TimestampBasedPagination nextPage(Date lastDate) {
        final var currentPagination = this;
        return new TimestampBasedPagination(
                currentPagination.getSortDirection().isAscending() ? lastDate : currentPagination.getGreaterThan(),
                currentPagination.getSortDirection().isAscending() ? currentPagination.getLowerThan() : lastDate,
                currentPagination.getSize(),
                currentPagination.getSortDirection()
        );
    }
}
