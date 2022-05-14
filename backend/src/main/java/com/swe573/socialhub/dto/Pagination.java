package com.swe573.socialhub.dto;

import com.google.common.base.Joiner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Map;

public abstract class Pagination<T> {
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

    public abstract Pagination<T> nextPage(T lastValue);

    abstract String toUrlString(T lastValue);

    public String makeUrlString(String endpointPrefix) {
        var map =  Map.of("sort", this.getSortDirection().toString(),
                "gt", toUrlString(getGreaterThan()),
                "lt", toUrlString(getLowerThan()),
                "size", Integer.toString(getSize())
        );

        return endpointPrefix + "?" + Joiner.on("&").withKeyValueSeparator("=").join(map);
    }
}
