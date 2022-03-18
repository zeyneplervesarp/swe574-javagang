package com.swe573.socialhub.domain;

import com.swe573.socialhub.enums.FlagType;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Flag {

    @Id
    @GeneratedValue
    private Long id;
    private FlagType type;
    private Long flaggingUser;
    private Long flaggedEntity;

    public Flag() {}

    public Flag(FlagType type, Long flaggingUser, Long flaggedEntity) {
        this.type = type;
        this.flaggingUser = flaggingUser;
        this.flaggedEntity = flaggedEntity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
