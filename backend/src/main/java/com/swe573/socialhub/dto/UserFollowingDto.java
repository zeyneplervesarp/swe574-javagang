package com.swe573.socialhub.dto;

import java.io.Serializable;
import java.util.Objects;

public class UserFollowingDto implements Serializable {
    private final Long id;
    private final Long followingUserId;
    private final Long followedUserId;

    public UserFollowingDto(Long id, Long followingUserId, Long followedUserId) {
        this.id = id;
        this.followingUserId = followingUserId;
        this.followedUserId = followedUserId;
    }

    public Long getId() {
        return id;
    }

    public Long getFollowingUserId() {
        return followingUserId;
    }

    public Long getFollowedUserId() {
        return followedUserId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFollowingDto entity = (UserFollowingDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.followingUserId, entity.followingUserId) &&
                Objects.equals(this.followedUserId, entity.followedUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, followingUserId,  followedUserId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "followingUserId = " + followingUserId + ", " +
                "followedUserId = " + followedUserId + ")";
    }
}
