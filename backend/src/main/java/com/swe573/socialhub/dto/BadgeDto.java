package com.swe573.socialhub.dto;

import com.swe573.socialhub.enums.BadgeType;

import java.io.Serializable;
import java.util.Objects;

public class

BadgeDto implements Serializable {
    private final Long id;
    private final BadgeType badgeType;

    public BadgeDto(Long id, BadgeType badgeType) {
        this.id = id;
        this.badgeType = badgeType;
    }

    public Long getId() {
        return id;
    }

    public BadgeType getBadgeType() {
        return badgeType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BadgeDto entity = (BadgeDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.badgeType, entity.badgeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, badgeType);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "badgeType = " + badgeType + ")";
    }
}
