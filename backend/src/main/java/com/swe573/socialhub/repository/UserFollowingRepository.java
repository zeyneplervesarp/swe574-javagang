package com.swe573.socialhub.repository;

import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.domain.UserFollowing;
import com.swe573.socialhub.domain.key.UserFollowingKey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserFollowingRepository extends JpaRepository<UserFollowing, UserFollowingKey> {
    Optional<UserFollowing> findUserFollowingByFollowingUserAndFollowedUser(User followingUser, User followedUser);

}