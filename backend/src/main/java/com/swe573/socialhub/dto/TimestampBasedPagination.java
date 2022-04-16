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
    private static final Date DEFAULT_LT = new Date(Long.MAX_VALUE);

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
        var sort = Sort.by("created");
        if (sortDirection == Sort.Direction.ASC) {
            sort = sort.ascending();
        } else {
            sort = sort.descending();
        }
        return PageRequest.of(0, size, sort);
    }
}
