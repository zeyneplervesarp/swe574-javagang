package com.swe573.socialhub.dto;

import org.springframework.data.domain.Sort;

public class DistanceBasedPagination extends Pagination<Double> {
    private static final Double DEFAULT_GT = 0D;
    private static final Double DEFAULT_LT = 50000000000D;

    public DistanceBasedPagination(Double greaterThanKms, Double lowerThanKms, int size) {
        super(
                greaterThanKms != null ? greaterThanKms : DEFAULT_GT,
                lowerThanKms != null ? lowerThanKms : DEFAULT_LT,
                size,
                Sort.Direction.ASC
        );
    }
}
