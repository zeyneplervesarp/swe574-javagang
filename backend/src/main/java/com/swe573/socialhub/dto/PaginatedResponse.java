package com.swe573.socialhub.dto;

import java.util.List;
import java.util.function.Function;

public class PaginatedResponse<T> {
    private final List<T> items;
    private final String nextPage;

    public PaginatedResponse(List<T> items, String nextPage) {
        this.items = items;
        this.nextPage = nextPage;
    }

    public <R> PaginatedResponse(List<T> items, String endpointPrefix, String endpointSuffix, Pagination<R> currentPagination, Function<T, R> lastValueFn) {
        this.items = items;
        if (!items.isEmpty()) {
            final var lastValue = lastValueFn.apply(items.get(items.size() - 1));
            final var nextPage = currentPagination.nextPage(lastValue);
            this.nextPage = nextPage.makeUrlString(endpointPrefix, endpointSuffix);
        } else {
            this.nextPage = null;
        }
    }

    public List<T> getItems() {
        return items;
    }

    public String getNextPage() {
        return nextPage;
    }
}
