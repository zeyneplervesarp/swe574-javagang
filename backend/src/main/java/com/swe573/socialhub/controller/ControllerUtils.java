package com.swe573.socialhub.controller;

import com.swe573.socialhub.dto.TimestampBasedPagination;
import org.springframework.data.domain.Sort;

import java.util.Date;

public class ControllerUtils {
    static TimestampBasedPagination parsePagination(
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
}
