package com.swe573.socialhub.domain;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
public class UserFollowing {
    private @Id
    @GeneratedValue
    Long id;

    @ManyToOne
//    @MapsId("followingUserId")
    @JoinColumn(name = "followingUser")
    User followingUser;

    @ManyToOne
//    @MapsId("followedUserId")
    @JoinColumn(name = "followedUser")
    User followedUser;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    private Date created;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public UserFollowing( User followingUser, User followedUser) {
        this.followingUser = followingUser;
        this.followedUser = followedUser;
    }

    public UserFollowing() {

    }

    // standard constructors, getters, and setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getFollowingUser() {
        return followingUser;
    }

    public void setFollowingUser(User followingUser) {
        this.followingUser = followingUser;
    }

    public User getFollowedUser() {
        return followedUser;
    }

    public void setFollowedUser(User followedUser) {
        this.followedUser = followedUser;
    }

    @Override
    public String toString() {
        return "UserServiceApproval{" +
                "followingUser=" + followingUser.getUsername() +
                ", followedUser=" + followedUser.getUsername() +
                '}';
    }
}
