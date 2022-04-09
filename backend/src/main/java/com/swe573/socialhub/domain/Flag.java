package com.swe573.socialhub.domain;

import com.swe573.socialhub.enums.FlagStatus;
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
    private FlagStatus status;

    public Flag() {}

    public Flag(FlagType type, Long flaggingUser, Long flaggedEntity, FlagStatus status) {
        this.type = type;
        this.flaggingUser = flaggingUser;
        this.flaggedEntity = flaggedEntity;
        this.status = status;
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

    public FlagStatus getStatus() {
        return status;
    }

    public void setStatus(FlagStatus status) {
        this.status = status;
    }
}
