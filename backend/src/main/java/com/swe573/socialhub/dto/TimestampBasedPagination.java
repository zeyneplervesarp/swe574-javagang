package com.swe573.socialhub.dto;

import org.springframework.data.domain.Sort;

import java.util.Date;

public class TimestampBasedPagination {
    private final Date greaterThan;
    private final Date lowerThan;
    private final int size;
    private final Sort.Direction sortDirection;

    public TimestampBasedPagination(Date greaterThan, Date lowerThan, int size, Sort.Direction sortDirection) {
        this.greaterThan = greaterThan;
        this.lowerThan = lowerThan;
        this.size = size;
        this.sortDirection = sortDirection;
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
}
