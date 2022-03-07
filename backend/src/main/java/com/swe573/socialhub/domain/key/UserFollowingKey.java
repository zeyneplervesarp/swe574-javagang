package com.swe573.socialhub.domain.key;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserFollowingKey implements Serializable {
    @Column(name = "following_user_id")
    Long followingUserId;

    @Column(name = "followed_user_id")
    Long followedUserId;

    // standard constructors, getters, and setters
    // hashcode and equals implementation


    public UserFollowingKey() {
    }

    public UserFollowingKey(Long followingUserId, Long followedUserId) {
        this.followingUserId = followingUserId;
        this.followedUserId = followedUserId;
    }
}
