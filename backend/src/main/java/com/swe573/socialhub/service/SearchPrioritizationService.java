package com.swe573.socialhub.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.repository.UserRepository;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchPrioritizationService extends CacheLoader<Long, SearchPrioritizationService.SearchPrioritizationParams> {


    private final UserRepository userRepository;
    private final LoadingCache<Long, SearchPrioritizationParams> prioritizationParamCache = CacheBuilder.newBuilder()
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build(this);

    public SearchPrioritizationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public SearchPrioritizationParams getPrioritizationParams(User user) {
        return prioritizationParamCache.getUnchecked(user.getId());
    }

    @Override
    public SearchPrioritizationParams load(Long key) throws Exception {
        return null;
    }


    static class SearchPrioritizationParams {
        final List<String> followingUsersTags;
        final List<String> followingUsersCreatedServicesTags;
        final List<String> joinedServicesTags;
        final List<String> givenServicesTags;
        final List<String> profileTags;
        final List<String> followingUsersJoinedServicesTags;

        public SearchPrioritizationParams(
                List<String> followingUsersTags,
                List<String> followingUsersCreatedServicesTags,
                List<String> joinedServicesTags,
                List<String> givenServicesTags,
                List<String> profileTags,
                List<String> followingUsersJoinedServicesTags
        ) {
            this.followingUsersTags = followingUsersTags;
            this.followingUsersCreatedServicesTags = followingUsersCreatedServicesTags;
            this.joinedServicesTags = joinedServicesTags;
            this.givenServicesTags = givenServicesTags;
            this.profileTags = profileTags;
            this.followingUsersJoinedServicesTags = followingUsersJoinedServicesTags;
        }
    }
}
