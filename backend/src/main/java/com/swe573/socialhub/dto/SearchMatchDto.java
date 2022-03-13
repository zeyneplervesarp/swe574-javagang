package com.swe573.socialhub.dto;

import com.swe573.socialhub.enums.SearchMatchType;

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
}
