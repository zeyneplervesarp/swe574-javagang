package com.swe573.socialhub.dto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class Pagination<T> {
    private final T greaterThan;
    private final T lowerThan;
    private final int size;
    private final Sort.Direction sortDirection;

    public Pagination(T greaterThan, T lowerThan, int size, Sort.Direction sortDirection) {
        if (size < 1) throw new IllegalArgumentException("Size must be greater than 1");
        this.greaterThan = greaterThan;
        this.lowerThan = lowerThan;
        this.size = size;
        this.sortDirection = sortDirection;
    }

    public T getGreaterThan() {
        return greaterThan;
    }

    public T getLowerThan() {
        return lowerThan;
    }

    public int getSize() {
        return size;
    }

    public Sort.Direction getSortDirection() {
        return sortDirection;
    }

    public Pageable toPageable(String sortByField) {
        var sort = Sort.by(sortByField);
        if (sortDirection == Sort.Direction.ASC) {
            sort = sort.ascending();
        } else {
            sort = sort.descending();
        }
        return PageRequest.of(0, size, sort);
    }

    public Pagination<T> nextPage(T lastValue) {
        final var currentPagination = this;
        return new Pagination<>(
                currentPagination.getSortDirection().isAscending() ? lastValue : currentPagination.getGreaterThan(),
                currentPagination.getSortDirection().isAscending() ? currentPagination.getLowerThan() : lastValue,
                currentPagination.getSize(),
                currentPagination.getSortDirection()
        );
    }
}
