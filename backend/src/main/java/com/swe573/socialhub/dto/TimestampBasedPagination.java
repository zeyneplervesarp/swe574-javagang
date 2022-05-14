package com.swe573.socialhub.dto;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Date;

public class TimestampBasedPagination extends Pagination<Date> {
    private static final Date DEFAULT_GT = new Date(0L);
    private static final Date DEFAULT_LT = new Date(System.currentTimeMillis() + System.currentTimeMillis());

    public TimestampBasedPagination(Date greaterThan, Date lowerThan, int size, Sort.Direction sortDirection) {
        super(
                greaterThan != null ? greaterThan : DEFAULT_GT,
                lowerThan != null ? lowerThan : DEFAULT_LT,
                size,
                sortDirection != null ? sortDirection : Sort.Direction.ASC
        );
    }

    public Pageable toPageable() {
        return toPageable("created");
    }
}
