package com.swe573.socialhub.repository;

import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.domain.UserFollowing;
import com.swe573.socialhub.domain.key.UserFollowingKey;
import com.swe573.socialhub.repository.activitystreams.DateQueryableRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface UserFollowingRepository extends JpaRepository<UserFollowing, UserFollowingKey>, DateQueryableRepository<UserFollowing> {
    Optional<UserFollowing> findUserFollowingByFollowingUserAndFollowedUser(User followingUser, User followedUser);

    @Query("select f from UserFollowing f where f.created > :createdGt and f.created < :createdLt")
    List<UserFollowing> findAllByDateBetween(Date createdGt, Date createdLt, Pageable pageable);

    @Query("select count (f) from UserFollowing  f where f.created > :createdGt and f.created < :createdLt")
    Long countByDateBetween(Date createdGt, Date createdLt);
}