package com.swe573.socialhub.dto;

public class StatContainerDto {
    private final long totalCount;
    private final long last24HoursCount;
    private final long lastWeekCount;
    private final long lastMonthCount;

    public StatContainerDto(long totalCount, long last24HoursCount, long lastWeekCount, long lastMonthCount) {
        this.totalCount = totalCount;
        this.last24HoursCount = last24HoursCount;
        this.lastWeekCount = lastWeekCount;
        this.lastMonthCount = lastMonthCount;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public long getLast24HoursCount() {
        return last24HoursCount;
    }

    public long getLastWeekCount() {
        return lastWeekCount;
    }

    public long getLastMonthCount() {
        return lastMonthCount;
    }
}
