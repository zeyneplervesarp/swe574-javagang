package com.swe573.socialhub.dto;

public class RatingSummaryDto {
    private final double ratingAverage;
    private final int raterCount;

    public RatingSummaryDto(double ratingAverage, int raterCount) {
        this.ratingAverage = ratingAverage;
        this.raterCount = raterCount;
    }

    public double getRatingAverage() {
        return ratingAverage;
    }

    public int getRaterCount() {
        return raterCount;
    }
}
