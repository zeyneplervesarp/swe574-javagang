package com.swe573.socialhub.dto;

import com.swe573.socialhub.enums.SearchMatchType;

import java.util.Objects;

public class SearchMatchDto {
    private final String name;
    private final String url;
    private final SearchMatchType matchType;

    public SearchMatchDto(String name, String url, SearchMatchType matchType) {
        this.name = name;
        this.url = url;
        this.matchType = matchType;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public SearchMatchType getMatchType() {
        return matchType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchMatchDto that = (SearchMatchDto) o;
        return name.equals(that.name) && Objects.equals(url, that.url) && matchType == that.matchType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, url, matchType);
    }
}
