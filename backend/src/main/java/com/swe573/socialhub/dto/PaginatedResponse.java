package com.swe573.socialhub.dto;

public class PaginatedResponse<T> {
    private final T items;
    private final String nextPage;

    public PaginatedResponse(T items, String nextPage) {
        this.items = items;
        this.nextPage = nextPage;
    }

    public T getItems() {
        return items;
    }

    public String getNextPage() {
        return nextPage;
    }
}
