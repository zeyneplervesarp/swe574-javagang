package com.swe573.socialhub.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.swe573.socialhub.domain.*;
import com.swe573.socialhub.enums.LocationType;
import com.swe573.socialhub.enums.SearchMatchType;
import com.swe573.socialhub.repository.UserRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@org.springframework.stereotype.Service
public class SearchPrioritizationService extends CacheLoader<Long, SearchPrioritizationService.SearchPrioritizationParams> {

    private final UserRepository userRepository;
    private final RatingService ratingService;
    private final LoadingCache<Long, SearchPrioritizationParams> prioritizationParamCache = CacheBuilder.newBuilder()
            .expireAfterAccess(2, TimeUnit.MINUTES)
            .build(this);

    public SearchPrioritizationService(UserRepository userRepository, RatingService ratingService) {
        this.userRepository = userRepository;
        this.ratingService = ratingService;
    }

    public SearchPrioritizationParams getPrioritizationParams(User user) {
        return prioritizationParamCache.getUnchecked(user.getId());
    }

    private final int weightTotal = Arrays
            .stream(PrioritizationCriterion.values())
            .mapToInt(c -> c.weight)
            .sum();

    private final int serviceWeightTotal = Arrays
            .stream(PrioritizationCriterion.values())
            .filter(c -> c.applicableTypes.contains(SearchMatchType.SERVICE))
            .mapToInt(c -> c.weight)
            .sum();

    private final int userWeightTotal = Arrays
            .stream(PrioritizationCriterion.values())
            .filter(c -> c.applicableTypes.contains(SearchMatchType.USER))
            .mapToInt(c -> c.weight)
            .sum();

    public Map<Long, Double> assignScoresToServices(List<Service> services, User user) {
        final var userPrioritizationParams = getPrioritizationParams(user);
        final var map = new HashMap<Long, Double>();

        services.forEach(s -> {
            final var score = findScore(
                    s,
                    userPrioritizationParams,
                    Arrays.stream(PrioritizationCriterion.values())
                            .filter(c -> c.applicableTypes.contains(SearchMatchType.SERVICE))
                            .collect(Collectors.toUnmodifiableList())
            );
            map.put(s.getId(), score);
        });

        return map;
    }

    public Map<Long, Double> assignScoresToUsers(List<User> users, User user) {
        final var userPrioritizationParams = getPrioritizationParams(user);
        final var map = new HashMap<Long, Double>();

        users.forEach(s -> {
            final var score = findScore(
                    s,
                    userPrioritizationParams,
                    Arrays.stream(PrioritizationCriterion.values())
                            .filter(c -> c.applicableTypes.contains(SearchMatchType.USER))
                            .collect(Collectors.toUnmodifiableList())
            );
            map.put(s.getId(), score);
        });

        return map;
    }

    private double findScore(
            User user,
            SearchPrioritizationParams userParams,
            List<PrioritizationCriterion> criteria
    ) {
       final var userTags = user
               .getUserTags()
               .stream()
               .map(Tag::getName)
               .collect(Collectors.toUnmodifiableSet());
        final var accumulatedScore = criteria.parallelStream()
                .mapToDouble(c -> {
                    PrioritizationScorer currentScorer = null;
                    switch (c) {
                        case NEWCOMER:
                            currentScorer = new DateProximityScorer(user.getCreated());
                            break;
                        case USER_PROFILE_TAGS:
                            currentScorer = new TagPrioritizationScorer(userParams.profileTags, userTags);
                            break;
                        case FOLLOWING_USERS_TAGS:
                            currentScorer = new TagPrioritizationScorer(userParams.followingUsersTags, userTags);
                            break;
                        case FOLLOWING_USERS_GIVEN_SERVICES_TAGS:
                            currentScorer = new TagPrioritizationScorer(userParams.followingUsersCreatedServicesTags, userTags);
                            break;
                        case FOLLOWING_USERS_JOINED_SERVICES_TAGS:
                            currentScorer = new TagPrioritizationScorer(userParams.followingUsersJoinedServicesTags, userTags);
                            break;
                        case RATING:
                            currentScorer = new RatingScorer(ratingService.getUserRatingSummary(user).getRatingAverage());
                            break;
                        case REPUTATION:
                            currentScorer = new ReputationScorer(user.getReputationPoint());
                            break;
                        case JOINED_SERVICES_TAGS:
                            currentScorer = new TagPrioritizationScorer(userParams.joinedServicesTags, userTags);
                            break;
                        case CREATED_SERVICES_TAGS:
                            currentScorer = new TagPrioritizationScorer(userParams.givenServicesTags, userTags);
                            break;
                        // Unapplicable cases below, it's fine to throw an exception because it's likely a programming error
                        case PROXIMITY:
                        case SERVICE_CREATION_DATE:
                        case SERVICE_DATE:
                            break;
                    }
                    return currentScorer.getScore() * c.weight;
                })
                .sum();

        return (accumulatedScore * weightTotal) / userWeightTotal;

    }

    private double findScore(
            Service service,
            SearchPrioritizationParams userParams,
            List<PrioritizationCriterion> criteria
    ) {
        final var serviceTags = service
                .getServiceTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.toUnmodifiableSet());

        final var accumulatedScore = criteria.parallelStream()
                .mapToDouble(c -> {
                    PrioritizationScorer currentScorer = null;
                    switch (c) {
                        case NEWCOMER:
                            currentScorer = new DateProximityScorer(service.getCreatedUser().getCreated());
                            break;
                        case USER_PROFILE_TAGS:
                            currentScorer = new TagPrioritizationScorer(userParams.profileTags, serviceTags);
                            break;
                        case FOLLOWING_USERS_TAGS:
                            currentScorer = new TagPrioritizationScorer(userParams.followingUsersTags, serviceTags);
                            break;
                        case FOLLOWING_USERS_GIVEN_SERVICES_TAGS:
                            currentScorer = new TagPrioritizationScorer(userParams.followingUsersCreatedServicesTags, serviceTags);
                            break;
                        case FOLLOWING_USERS_JOINED_SERVICES_TAGS:
                            currentScorer = new TagPrioritizationScorer(userParams.followingUsersJoinedServicesTags, serviceTags);
                            break;
                        case RATING:
                            currentScorer = new RatingScorer(ratingService.getUserRatingSummary(service.getCreatedUser()).getRatingAverage());
                            break;
                        case REPUTATION:
                            currentScorer = new ReputationScorer(service.getCreatedUser().getReputationPoint());
                            break;
                        case JOINED_SERVICES_TAGS:
                            currentScorer = new TagPrioritizationScorer(userParams.joinedServicesTags, serviceTags);
                            break;
                        case CREATED_SERVICES_TAGS:
                            currentScorer = new TagPrioritizationScorer(userParams.givenServicesTags, serviceTags);
                            break;
                        case PROXIMITY:
                            if (userParams.lastPhysicalService == null || service.getLocationType().equals(LocationType.Online)) {
                                return 0;
                            }
                            currentScorer = new ProximityScorer(
                                    userParams.lastPhysicalService.getLatitude(),
                                    userParams.lastPhysicalService.getLongitude(),
                                    service.getLatitude(),
                                    service.getLongitude()
                            );
                            break;
                        case SERVICE_CREATION_DATE:
                            currentScorer = new DateProximityScorer(service.getCreated());
                            break;
                        case SERVICE_DATE:
                            currentScorer = new DateProximityScorer(Date.from(service.getTime().atZone(ZoneId.systemDefault()).toInstant()));
                            break;
                    }
                    return currentScorer.getScore() * c.weight;
                })
                .sum();

        return (accumulatedScore * weightTotal) / serviceWeightTotal;
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

        final var userPhysicalSvcsSorted = user
                .getCreatedServices()
                .stream()
                .filter(s -> s.getLocationType().equals(LocationType.Physical))
                .sorted(Comparator.comparing(Service::getCreated))
                .collect(Collectors.toUnmodifiableList());

        return new SearchPrioritizationParams(
                followingUserTags,
                followingUserCreatedServicesTags,
                joinedServicesTags,
                createdServicesTags,
                profileTags,
                followingUserJoinedServicesTags,
                userPhysicalSvcsSorted.isEmpty() ? null : userPhysicalSvcsSorted.get(userPhysicalSvcsSorted.size() - 1)
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
        final Service lastPhysicalService;

        public SearchPrioritizationParams(
                Set<String> followingUsersTags,
                Set<String> followingUsersCreatedServicesTags,
                Set<String> joinedServicesTags,
                Set<String> givenServicesTags,
                Set<String> profileTags,
                Set<String> followingUsersJoinedServicesTags,
                Service lastPhysicalService
        ) {
            this.followingUsersTags = followingUsersTags;
            this.followingUsersCreatedServicesTags = followingUsersCreatedServicesTags;
            this.joinedServicesTags = joinedServicesTags;
            this.givenServicesTags = givenServicesTags;
            this.profileTags = profileTags;
            this.followingUsersJoinedServicesTags = followingUsersJoinedServicesTags;
            this.lastPhysicalService = lastPhysicalService;
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

    private static class RatingScorer implements PrioritizationScorer {
        private final double rating;

        public RatingScorer(double rating) {
            this.rating = rating;
        }

        @Override
        public double getScore() {
            return rating / 5;
        }
    }

    private static class ReputationScorer implements PrioritizationScorer {
        private final int reputation;

        private static final int REPUTATION_CEILING = 500;

        public ReputationScorer(int reputation) {
            this.reputation = reputation;
        }

        @Override
        public double getScore() {
            return reputation > REPUTATION_CEILING ? 1 : (double) reputation / REPUTATION_CEILING;
        }
    }

    private static class ProximityScorer implements PrioritizationScorer {

        private static final int DISTANCE_CEILING_KM = 100;

        private final double haversineDistanceKilometers;

        public ProximityScorer(double lat1, double lng1, double lat2, double lng2) {
            // haversine formula
            // https://en.wikipedia.org/wiki/Haversine_formula
            final var latDistance = toRad(lat2 - lat1);
            final var lonDistance = toRad(lng2 - lng1);
            final var a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                    Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) *
                            Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
            final var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

            final int r = 6371; // radius of the earth
            haversineDistanceKilometers = r * c;
        }

        private static double toRad(double value) {
            return value * Math.PI / 180;
        }

        @Override
        public double getScore() {
            return haversineDistanceKilometers > DISTANCE_CEILING_KM
                    ? 0
                    : (DISTANCE_CEILING_KM - haversineDistanceKilometers) / DISTANCE_CEILING_KM;
        }
    }

    private static class DateProximityScorer implements PrioritizationScorer {

        private final Date date;

        public DateProximityScorer(Date date) {
            this.date = date;
        }

        @Override
        public double getScore() {
            final var nowMillis = Instant.now().toEpochMilli();
            final var creationDateMillis = date.toInstant().toEpochMilli();
            return creationDateMillis > nowMillis ? 1 : (double) creationDateMillis / (double) nowMillis;
        }
    }

    public enum PrioritizationCriterion {
        NEWCOMER(7, Set.of(SearchMatchType.USER, SearchMatchType.SERVICE)),
        USER_PROFILE_TAGS(8, Set.of(SearchMatchType.USER, SearchMatchType.SERVICE)),
        FOLLOWING_USERS_TAGS(2, Set.of(SearchMatchType.USER, SearchMatchType.SERVICE)),
        FOLLOWING_USERS_GIVEN_SERVICES_TAGS(10, Set.of(SearchMatchType.USER, SearchMatchType.SERVICE)),
        FOLLOWING_USERS_JOINED_SERVICES_TAGS(1, Set.of(SearchMatchType.USER, SearchMatchType.SERVICE)),
        RATING(5, Set.of(SearchMatchType.USER, SearchMatchType.SERVICE)),
        REPUTATION(5, Set.of(SearchMatchType.USER, SearchMatchType.SERVICE)),
        JOINED_SERVICES_TAGS(9, Set.of(SearchMatchType.USER, SearchMatchType.SERVICE)),
        CREATED_SERVICES_TAGS(9, Set.of(SearchMatchType.USER, SearchMatchType.SERVICE)),
        PROXIMITY(10, Set.of(SearchMatchType.SERVICE)),
        SERVICE_CREATION_DATE(7, Set.of(SearchMatchType.SERVICE)),
        SERVICE_DATE(7, Set.of(SearchMatchType.SERVICE));

        final int weight;
        final Set<SearchMatchType> applicableTypes;

        PrioritizationCriterion(int weight, Set<SearchMatchType> applicableTypes) {
            this.weight = weight;
            this.applicableTypes = applicableTypes;
        }
    }
}
