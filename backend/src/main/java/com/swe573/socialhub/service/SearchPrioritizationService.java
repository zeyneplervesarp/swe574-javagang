package com.swe573.socialhub.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.swe573.socialhub.domain.*;
import com.swe573.socialhub.repository.UserRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@org.springframework.stereotype.Service
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
        final var user = userRepository.findById(key).get();
        final var profileTags = user
                .getTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toUnmodifiableSet());

        final var followingUsers = user
                .getFollowingUsers()
                .parallelStream()
                .map(UserFollowing::getFollowedUser)
                .collect(Collectors.toUnmodifiableSet());

        final var followingUserTags = followingUsers
                .parallelStream()
                .flatMap(uf -> uf.getTags().stream())
                .map(Tag::getName)
                .collect(Collectors.toUnmodifiableSet());

        final var followingUserJoinedServicesTags = getServicesTags(
                followingUsers
                        .parallelStream()
                        .flatMap(uf -> uf.getServiceApprovalSet().stream())
                        .map(UserServiceApproval::getService)
        );

        final var followingUserCreatedServicesTags = getServicesTags(
                followingUsers
                        .parallelStream()
                        .flatMap(uf -> uf.getCreatedServices().stream())
        );

        final var joinedServicesTags = getServicesTags(
                user.getServiceApprovalSet()
                        .parallelStream()
                        .map(UserServiceApproval::getService)
        );

        final var createdServicesTags = getServicesTags(
                user.getCreatedServices()
                        .parallelStream()
        );

        return new SearchPrioritizationParams(
                followingUserTags,
                followingUserCreatedServicesTags,
                joinedServicesTags,
                createdServicesTags,
                profileTags,
                followingUserJoinedServicesTags
        );
    }

    private Set<String> getServicesTags(Stream<Service> services) {
        return services
                .flatMap(s -> s.getServiceTags().stream())
                .map(Tag::getName)
                .collect(Collectors.toUnmodifiableSet());
    }


    static class SearchPrioritizationParams {
        final Set<String> followingUsersTags;
        final Set<String> followingUsersCreatedServicesTags;
        final Set<String> joinedServicesTags;
        final Set<String> givenServicesTags;
        final Set<String> profileTags;
        final Set<String> followingUsersJoinedServicesTags;

        public SearchPrioritizationParams(
                Set<String> followingUsersTags,
                Set<String> followingUsersCreatedServicesTags,
                Set<String> joinedServicesTags,
                Set<String> givenServicesTags,
                Set<String> profileTags,
                Set<String> followingUsersJoinedServicesTags
        ) {
            this.followingUsersTags = followingUsersTags;
            this.followingUsersCreatedServicesTags = followingUsersCreatedServicesTags;
            this.joinedServicesTags = joinedServicesTags;
            this.givenServicesTags = givenServicesTags;
            this.profileTags = profileTags;
            this.followingUsersJoinedServicesTags = followingUsersJoinedServicesTags;
        }
    }

    interface PrioritizationScorer {
        double getScore();
    }

    private static class TagPrioritizationScorer implements PrioritizationScorer {
        private final Set<String> tagsToBeMatched;
        private final Set<String> candidateTags;

        public TagPrioritizationScorer(Set<String> tagsToBeMatched, Set<String> candidates) {
            this.tagsToBeMatched = tagsToBeMatched;
            this.candidateTags = candidates;
        }

        @Override
        public double getScore() {
            if (tagsToBeMatched.isEmpty() || candidateTags.isEmpty()) return 0;

            final var intersection = new HashSet<>(candidateTags);
            intersection.retainAll(tagsToBeMatched);

            return (double) intersection.size() / (double) candidateTags.size();
        }
    }

    public enum PrioritizationCriterion {
        NEWCOMER(7),
        FOLLOWING_USERS_SERVICES_TAGS(10),
        USER_PROFILE_TAGS(8),
        FOLLOWING_USERS_TAGS(2),
        FOLLOWING_USERS_GIVEN_SERVICES_TAGS(1),
        FOLLOWING_USERS_JOINED_SERVICES_TAGS(1),
        RATING(5),
        REPUTATION(5),
        JOINED_SERVICES_TAGS(9),
        CREATED_SERVICES_TAGS(9),
        PROXIMITY(10),
        SERVICE_CREATION_DATE(7),
        SERVICE_DATE(7);

        final int weight;

        PrioritizationCriterion(int weight) {
            this.weight = weight;
        }
    }
}
