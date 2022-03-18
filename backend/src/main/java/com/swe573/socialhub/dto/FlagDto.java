package com.swe573.socialhub.dto;

import com.swe573.socialhub.enums.FlagType;

public class FlagDto {
    private Long id;
    private FlagType type;
    private Long flaggingUser;
    private Long flaggedEntity;

    public FlagDto(Long id, FlagType type, Long flaggingUser, Long flaggedEntity) {
        this.id = id;
        this.type = type;
        this.flaggingUser = flaggingUser;
        this.flaggedEntity = flaggedEntity;
    }

    public Long getId() {
        return id;
    }

    public FlagType getType() {
        return type;
    }

    public void setType(FlagType type) {
        this.type = type;
    }

    public Long getFlaggingUser() {
        return flaggingUser;
    }

    public void setFlaggingUser(Long flaggingUser) {
        this.flaggingUser = flaggingUser;
    }

    public Long getFlaggedEntity() {
        return flaggedEntity;
    }

    public void setFlaggedEntity(Long flaggedEntity) {
        this.flaggedEntity = flaggedEntity;
    }
}
