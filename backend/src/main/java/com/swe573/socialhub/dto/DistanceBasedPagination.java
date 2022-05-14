package com.swe573.socialhub.dto;

import org.springframework.data.domain.Sort;

public class DistanceBasedPagination extends Pagination<Long> {
    private static final Long DEFAULT_GT = 0L;
    private static final Long DEFAULT_LT = 50000000000L;

    public DistanceBasedPagination(Long greaterThanMeters, Long lowerThanMeters, int size, Sort.Direction sortDirection) {
        super(
                greaterThanMeters != null ? greaterThanMeters : DEFAULT_GT,
                lowerThanMeters != null ? lowerThanMeters : DEFAULT_LT,
                size,
                sortDirection != null ? sortDirection : Sort.Direction.ASC
        );
    }
}
