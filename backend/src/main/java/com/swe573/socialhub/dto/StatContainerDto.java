package com.swe573.socialhub.dto;

public class StatContainerDto {
    private final int totalCount;
    private final int last24HoursCount;
    private final int lastWeekCount;
    private final int lastMonthCount;

    public StatContainerDto(int totalCount, int last24HoursCount, int lastWeekCount, int lastMonthCount) {
        this.totalCount = totalCount;
        this.last24HoursCount = last24HoursCount;
        this.lastWeekCount = lastWeekCount;
        this.lastMonthCount = lastMonthCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getLast24HoursCount() {
        return last24HoursCount;
    }

    public int getLastWeekCount() {
        return lastWeekCount;
    }

    public int getLastMonthCount() {
        return lastMonthCount;
    }
}
