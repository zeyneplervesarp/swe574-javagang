package com.swe573.socialhub.domain;

import com.swe573.socialhub.enums.BadgeType;

import javax.persistence.*;

@Entity
public class Badge {
    private @Id
    @GeneratedValue
    Long id;
    @ManyToOne
    @JoinColumn(name = "owner")
    User owner;
    private BadgeType badgeType;

    public Badge() {

    }

    public Badge(User owner, BadgeType badgeType) {
        this.owner = owner;
        this.badgeType = badgeType;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public BadgeType getBadgeType() {
        return badgeType;
    }

    public void setBadgeType(BadgeType badgeType) {
        this.badgeType = badgeType;
    }
}
