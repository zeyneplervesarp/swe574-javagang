package com.swe573.socialhub.controller;

import com.swe573.socialhub.dto.DistanceBasedPagination;
import com.swe573.socialhub.dto.TimestampBasedPagination;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class ControllerUtils {
    static TimestampBasedPagination parseTimestampPagination(
            Long gt,
            Long lt,
            Integer size,
            String sort
    ) {

        final var sortDirection = sort != null && sort.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;

        return new TimestampBasedPagination(
                gt != null ? new Date(gt) : null,
                lt != null ? new Date(lt) : null,
                size != null ? size : 20,
                sortDirection
        );
    }

    static DistanceBasedPagination parseDistancePagination(
            Double gt,
            Double lt,
            Integer size
    ) {
        return new DistanceBasedPagination(
                gt,
                lt,
                size != null ? size : 20
        );
    }

    static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
