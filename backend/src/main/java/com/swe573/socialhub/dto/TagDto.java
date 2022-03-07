package com.swe573.socialhub.dto;

public class TagDto {
    Long id;
    private String name;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public TagDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

}
