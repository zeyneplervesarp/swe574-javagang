package com.swe573.socialhub.dto;

import java.util.List;
import java.util.function.Function;

public class PaginatedListResponse<T> extends PaginatedResponse<List<T>> {
    public <R> PaginatedListResponse(List<T> items, String endpointPrefix, String endpointSuffix, Pagination<R> currentPagination, Function<T, R> lastValueFn) {
        super(items, items.isEmpty() ? null : currentPagination.nextPage(lastValueFn.apply(items.get(items.size() - 1))).makeUrlString(endpointPrefix, endpointSuffix));
    }
}
