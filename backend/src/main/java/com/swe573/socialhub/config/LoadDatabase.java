package com.swe573.socialhub.config;

import antlr.collections.impl.IntRange;
import com.github.javafaker.Faker;
import com.swe573.socialhub.domain.*;
import com.swe573.socialhub.domain.key.UserEventApprovalKey;
import com.swe573.socialhub.domain.key.UserServiceApprovalKey;
import com.swe573.socialhub.enums.*;
import com.swe573.socialhub.enums.ApprovalStatus;
import com.swe573.socialhub.enums.BadgeType;
import com.swe573.socialhub.enums.UserType;
import com.swe573.socialhub.repository.*;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@Profile("!test")
@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean
    CommandLineRunner initDatabase(
            TagRepository tagRepository,
            UserRepository userRepository,
            ServiceRepository serviceRepository,
            UserServiceApprovalRepository approvalRepository,
            NotificationRepository notificationRepository,
            PasswordEncoder passwordEncoder,
            UserFollowingRepository userFollowingRepository,
            EventRepository eventRepository,
            UserEventApprovalRepository eventApprovalRepository,
            RatingRepository ratingRepository,
            BadgeRepository badgeRepository,
            FlagRepository flagRepository,
            LoginAttemptRepository loginAttemptRepository
    ) {


        return args -> {

            final var started = Instant.now();
            System.out.println("Initial db loading started.");

            final var userCount = 10;

            final var tags = createTags(tagRepository);
            final var faker = new Faker();
            final var users = setCreatedForUsers(createUsers(userRepository, passwordEncoder, faker, userCount, tags), userRepository);
            final var svcs = setCreatedForServices(createServices(serviceRepository, users, faker, tags), serviceRepository);
            final var followGraph = setCreated(makeFollowerGraph(users, userFollowingRepository), userFollowingRepository);
            final var requestGraph = setDatesAndOutcome(makeServiceRequestGraph(followGraph, users, svcs, approvalRepository), approvalRepository);
            final var simulatedSvcResults = persistSimulatedResults(
                    notificationRepository,
                    userRepository,
                    badgeRepository,
                    loginAttemptRepository,
                    ratingRepository,
                    simulateServiceResults(
                            requestGraph,
                            users,
                            followGraph,
                            svcs
                    )
            );


            System.out.println("Created " + users.size() + " users.");
            System.out.println("Created " + svcs.size() + " services.");
            System.out.println("Created " + followGraph.size() + " UserFollowing.");
            System.out.println("Created " + requestGraph.size() + " join requests.");
            System.out.println("Created " + simulatedSvcResults.badges.size() + " badges.");
            System.out.println("Created " + simulatedSvcResults.loginAttempts.size() + " login attempts.");
            System.out.println("Created " + simulatedSvcResults.notifications.size() + " notifications.");
            System.out.println("Created " + simulatedSvcResults.ratings.size() + " ratings.");
            System.out.println("Initial db loading took " + (Instant.now().toEpochMilli() - started.toEpochMilli()) + " milliseconds. Requested user count: " + userCount + ".");
        };
    }

    private static final Date SITE_CREATION_DATE = new Date(1619868495782L);

    private Date randomDate(Date min, Date max) {
        final var minMillis = min.toInstant().toEpochMilli();
        final var maxMillis = max.toInstant().toEpochMilli();
        if (maxMillis == minMillis) return min;
        return new Date(randomLongBetween(minMillis, maxMillis));
    }

    private long randomLongBetween(long min, long max) {
        return ThreadLocalRandom.current().nextLong(min, max);
    }

    private List<User> createUsers(UserRepository userRepository, PasswordEncoder passwordEncoder, Faker faker, int count, List<Tag> tags) {



        final var encodedPw = passwordEncoder.encode("1");
        var admin = new User(null, "admin", "admin@socialhub.com", "I'm the adminest of admins!", Collections.emptySet(), 0, "34", "28", "Admin bvd. 33", UserType.ADMIN, 0);
        admin.setPassword(encodedPw);

        var userList = LongStream.range(0, count).parallel().mapToObj(i -> {
            var username = chooseBetween(List.of(faker.internet().slug(), faker.artist().name().trim().toLowerCase() + faker.random().nextInt(99), faker.internet().slug() + faker.internet().domainSuffix()));
            var email = chooseBetween(List.of(username + "@" + faker.internet().domainName() + "." + faker.internet().domainSuffix(), faker.internet().emailAddress()));
            var bio = chooseBetween(List.of(faker.shakespeare().hamletQuote(), faker.shakespeare().kingRichardIIIQuote(), faker.shakespeare().asYouLikeItQuote(), faker.shakespeare().romeoAndJulietQuote(), faker.rickAndMorty().quote(), faker.backToTheFuture().quote()));
            var u = new User(null, username, email, bio, new HashSet<>(chooseManyBetween(tags, (int) randomLongBetween(0, 3))), 10, faker.address().latitude(), faker.address().longitude(), faker.address().fullAddress(), UserType.USER, 0);
            u.setPassword(encodedPw);
            return u;
        }).collect(Collectors.toList());

        userList.add(admin);

        return userRepository.saveAll(userList);
    }

    private <T> T chooseBetween(List<T> items) {
        return items.get((int) randomLongBetween(0, items.size()));
    }

    private <T> List<T> chooseManyBetween(List<T> items, int count) {
        final var copy = new ArrayList<>(items);
        Collections.shuffle(copy);
        return copy.subList(0, Math.min(count, copy.size()));
    }

    private List<User> setCreatedForUsers(List<User> users, UserRepository userRepository) {
        users.forEach(u -> u.setCreated(u.getUserType().equals(UserType.ADMIN) ? SITE_CREATION_DATE : randomDate(SITE_CREATION_DATE, new Date())));
        return userRepository.saveAll(users);
    }

    private List<Tag> createTags(TagRepository tagRepository) {
        return tagRepository.saveAll(List.of(
                new Tag("movies"),
                new Tag("arts"),
                new Tag("sports"),
                new Tag("comedy"),
                new Tag("misc"),
                new Tag("education"),
                new Tag("nature"))
        );
    }

    private int generateServiceCount(User user) {
        final var userAgeMillis = new Date().toInstant().toEpochMilli() - user.getCreated().toInstant().toEpochMilli();
        final var userAgeWeeks = userAgeMillis / (1000 * 60 * 60 * 24 * 7);
        return (int) randomLongBetween(0, Math.max(userAgeWeeks, 1));
    }

    private Service generateService(User user, Faker faker, List<Tag> tags) {
        final var tagPool = new ArrayList<>(user.getTags());
        tagPool.addAll(tags);
        final var pickedTags = chooseManyBetween(tagPool, faker.random().nextBoolean() ? 1 : 2);
        final var mainTag = pickedTags.get(0);

        final var isPhysical = faker.random().nextBoolean();
        String title = "";
        String subtitle = "";
        switch (mainTag.getName()) {
            case "movies":
                var chosenPair = chooseBetween(List.of(
                        Pair.of(faker.backToTheFuture().character(), faker.backToTheFuture().quote()),
                        Pair.of(faker.princessBride().character(), faker.princessBride().quote()),
                        Pair.of(faker.rickAndMorty().character(), faker.rickAndMorty().quote()),
                        Pair.of(faker.harryPotter().character(), faker.harryPotter().quote()),
                        Pair.of(faker.hitchhikersGuideToTheGalaxy().character(), faker.hitchhikersGuideToTheGalaxy().quote()),
                        Pair.of(faker.lebowski().character(), faker.lebowski().quote()),
                        Pair.of(faker.leagueOfLegends().champion(), faker.leagueOfLegends().quote()),
                        Pair.of(faker.starTrek().character(), "And " + faker.starTrek().specie() + " in " + faker.starTrek().location() + "."),
                        Pair.of(faker.twinPeaks().character(), faker.twinPeaks().quote()),
                        Pair.of(faker.zelda().character(), faker.zelda().game())
                    )
                );

                title = chosenPair.getLeft() + " " + chooseBetween(List.of("Appreciation Day", "Analysis", "Lessons"));
                subtitle = chosenPair.getRight();
                break;
            case "arts":
                chosenPair = chooseBetween(List.of(
                        Pair.of(faker.artist().name() + " Discussions", "And their ties to the making of " + faker.book().title()),
                        Pair.of("A Critique of " + faker.book().author(), "I'll teach you how to be better than that."),
                        Pair.of(faker.book().genre() + " Critical Reading Lessons", "So you can author the next " + faker.book().title()),
                        Pair.of(faker.color().name() + " Masterclass", "To command the palette, one must first command oneself."),
                        Pair.of(faker.nation().capitalCity() + " Culture Lessons", "To understand them better :)")
                    )
                );

                title = chosenPair.getLeft();
                subtitle = chosenPair.getRight();
                break;
            case "sports":
                chosenPair = chooseBetween(List.of(
                            Pair.of(faker.esports().event() + " Recap", "We'll discuss how " + faker.esports().team() + " lost horribly."),
                            Pair.of(faker.nation().flag() + " vs. " + faker.nation().flag(), "Let's see who'll win!")
                    )
                );

                title = chosenPair.getLeft();
                subtitle = chosenPair.getRight();
                break;
            case "comedy":
                title = chooseBetween(List.of("Comedy Lessons for " + faker.funnyName().name(), faker.funnyName().name()));
                subtitle = "Haha not everyone is as funny as I am ;;)))";
                break;
            case "misc":
                var topic = chooseBetween(List.of(faker.pokemon().name(), faker.pokemon().location(), faker.finance().creditCard(), faker.princessBride().character()));
                title = topic + " " + chooseBetween(List.of("Appreciation", "Analysis", "Review", "Discussion"));
                subtitle = "To master our topic, " + topic + ".";
                break;
            case "education":
                final var uni = faker.university().name();
                final var country = faker.country().name();

                title = chooseBetween(List.of(uni, country)) + chooseBetween(List.of(" Exam Prep", " Education Coaching", " Higher Edu. Review"));
                subtitle = "After all, " + uni + " is one of the best schools in " + uni + ". Not to mention " + faker.address().cityName() + " universities.";
                break;
            case "nature":
                final var city = chooseBetween(List.of(faker.address().cityName(), faker.address().cityPrefix(), faker.address().city()));
                final var activity = chooseBetween(List.of("Nature Tour", "Park Hopping", "Park Review", "Chill Walk Group"));

                title = chooseBetween(List.of(activity + " of " + city, city + " " + activity));
                subtitle = "After all, we shouldn't take " + city + "'s beauty for granted!!";
                break;
        }

        if (isPhysical) {
            return Service.createPhysical(
                null,
                title,
                subtitle,
                faker.address().fullAddress(),
                fromDate(randomDate(user.getCreated(), new Date())),
                (int) randomLongBetween(10, 160),
                (int) randomLongBetween(1, 10),
                0,
                user,
                Double.valueOf(faker.address().latitude()), Double.valueOf(faker.address().longitude()),
                new HashSet<>(pickedTags)
            );
        } else {

            return Service.createOnline(
                null,
                title,
                subtitle,
                "zoom.us/" + faker.random().hex(),
                fromDate(randomDate(user.getCreated(), new Date())),
                (int) randomLongBetween(10, 160),
                (int) randomLongBetween(1, 10),
                0,
                user,
                new HashSet<>(pickedTags)
            );
        }
    }

    private LocalDateTime fromDate(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    private Date fromLocalDateTime(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private List<Service> createServices(ServiceRepository repository, List<User> users, Faker faker, List<Tag> tags) {
        final var services = users.stream()
                .parallel()
                .flatMap(user -> IntStream.range(0, generateServiceCount(user)).mapToObj(i -> generateService(user, faker, tags)))
                .collect(Collectors.toUnmodifiableList());
        return repository.saveAll(services);
    }

    private int makeFollowerCount(User user) {
        final var userAgeMillis = new Date().toInstant().toEpochMilli() - user.getCreated().toInstant().toEpochMilli();
        final var userAgeWeeks = userAgeMillis / (1000 * 60 * 60 * 24 * 7);
        return (int) randomLongBetween(0, Math.max(userAgeWeeks, 1));
    }

    private List<UserFollowing> makeFollowerGraph(List<User> users, UserFollowingRepository userFollowingRepository) {
        final var copy = new ArrayList<>(users);
        Collections.shuffle(copy);
        final var userQueue = new LinkedList<>(copy);

        return userFollowingRepository.saveAll(
                users.stream()
                        .flatMap(user -> IntStream.range(0, makeFollowerCount(user)).mapToObj(i -> {
                            var nextUser = userQueue.poll();
                            if (nextUser == null || nextUser.getId().equals(user.getId())) {
                                userQueue.addAll(copy);
                                nextUser = userQueue.poll();
                            }

                            var follow = new UserFollowing();
                            follow.setFollowedUser(user);
                            follow.setFollowingUser(nextUser);
                            return follow;
                        })).collect(Collectors.toUnmodifiableList())
        ) ;
    }

    private List<UserFollowing> setCreated(List<UserFollowing> followerGraph, UserFollowingRepository repository) {
        final var updated = followerGraph.stream().parallel().peek(uf -> {
            final var date1 = uf.getFollowedUser().getCreated();
            final var date2 = uf.getFollowingUser().getCreated();
            final var minDate = new Date(Math.min(date1.toInstant().toEpochMilli(), date2.toInstant().toEpochMilli()));
            final var maxDate = new Date(Math.max(date1.toInstant().toEpochMilli(), date2.toInstant().toEpochMilli()));
            uf.setCreated(randomDate(minDate, maxDate));
        }).collect(Collectors.toUnmodifiableList());

        return repository.saveAll(updated);
    }

    private List<Service> setCreatedForServices(List<Service> services, ServiceRepository repository) {
        final var list = services.parallelStream().peek(svc -> {
            final var minDate = new Date(Math.max(svc.getCreatedUser().getCreated().toInstant().toEpochMilli(), fromLocalDateTime(svc.getTime().minusMonths(1)).toInstant().toEpochMilli()));
            final var maxDate = fromLocalDateTime(svc.getTime());
            svc.setCreated(randomDate(minDate, maxDate));
        }).collect(Collectors.toUnmodifiableList());
        return repository.saveAll(list);
    }

    private static final double USER_SERVICE_REJECTION_RATE = 0.1;

    private List<UserServiceApproval> makeServiceRequestGraph(List<UserFollowing> followGraph, List<User> users, List<Service> services, UserServiceApprovalRepository repository) {
        final var userComparator = Comparator.comparing(User::getCreated);

        final var usersSortedByCreated = new ArrayList<>(users);
        usersSortedByCreated.sort(userComparator);

        final Map<Long, User> userIdMap = new HashMap<>();
        users.forEach(u -> {
            userIdMap.put(u.getId(), u);

        });

        final Map<Long, Set<Long>> followersOfUsers = new HashMap<>();
        followGraph.forEach(f -> {
            final var targetSet = followersOfUsers.getOrDefault(f.getFollowedUser().getId(), new HashSet<>());
            targetSet.add(f.getFollowingUser().getId());
            followersOfUsers.put(f.getFollowedUser().getId(), targetSet);
        });

        final var approvals = services.parallelStream().flatMap(svc -> {
            final var userFollowers = followersOfUsers.get(svc.getCreatedUser().getId());
            final var svcLiveEpochMilli = fromLocalDateTime(svc.getTime()).toInstant().toEpochMilli() - svc.getCreated().toInstant().toEpochMilli();
            final var svcLiveDays = svcLiveEpochMilli / 1000L * 60L * 60L * 24L;
            final var totalRequestorsToRecv = Math.min(randomLongBetween(0, Math.max(1, svcLiveDays) * 3), svc.getQuota() + 2L);

            if (totalRequestorsToRecv < 1) {
                return Stream.empty();
            }
            final var requestorCandidates = Optional.ofNullable(userFollowers)
                    .orElse(new HashSet<>())
                    .stream()
                    .map(userIdMap::get)
                    .filter(u -> u.getCreated().toInstant().toEpochMilli() < svc.getCreated().toInstant().toEpochMilli())
                    .filter(u -> !u.getId().equals(svc.getCreatedUser().getId()))
                    .limit(totalRequestorsToRecv)
                    .collect(Collectors.toList());


                var firstMatchIndex = -1;
                for (int i = 0; i < usersSortedByCreated.size(); i++) {
                    // TODO: binary search would work better here
                    var user = usersSortedByCreated.get(i);
                    if (user.getCreated().toInstant().toEpochMilli() < svc.getCreated().toInstant().toEpochMilli()) {
                        firstMatchIndex = i;
                        break;
                    }
                }

                if (firstMatchIndex != -1) {
                    var possibleIndices = IntStream.rangeClosed(firstMatchIndex, usersSortedByCreated.size() - 1).boxed().collect(Collectors.toList());
                    Collections.shuffle(possibleIndices);

                    for (int i = 0; i < totalRequestorsToRecv && i < possibleIndices.size(); i++) {
                        requestorCandidates.add(usersSortedByCreated.get(possibleIndices.get(i)));
                    }
                }


            Collections.shuffle(requestorCandidates);


            return requestorCandidates.stream().limit(totalRequestorsToRecv).map(cand -> {

                final var app = new UserServiceApproval();
                final var key = new UserServiceApprovalKey(cand.getId(), svc.getId());
                app.setId(key);

                app.setApprovalStatus(ApprovalStatus.PENDING);
                app.setService(svc);
                app.setUser(cand);
                return app;
            });
        }).collect(Collectors.toUnmodifiableList());

        return repository.saveAll(approvals);
    }

    private List<UserServiceApproval> setDatesAndOutcome(List<UserServiceApproval> userServiceApprovals, UserServiceApprovalRepository repository) {
        final var svcQuotaMap = new HashMap<Long, Integer>();
        userServiceApprovals.forEach(usa -> {
            svcQuotaMap.put(usa.getService().getId(), usa.getService().getQuota());
        });

        final var updated = userServiceApprovals.stream().peek(usa -> {

            final var svcCreated = usa.getService().getCreated();
            final var svcTime = fromLocalDateTime(usa.getService().getTime());
            usa.setCreated(randomDate(svcCreated, svcTime));
            final var outcomeDate = randomDate(usa.getCreated(), svcTime);
            final var outcome = ((double) randomLongBetween(0, 100) / 100) > USER_SERVICE_REJECTION_RATE;
            if (outcome && svcQuotaMap.get(usa.getService().getId()) > 0) {
                usa.setApprovalStatus(ApprovalStatus.APPROVED);
                usa.setApprovedDate(outcomeDate);
                svcQuotaMap.put(usa.getService().getId(), svcQuotaMap.get(usa.getService().getId()) - 1);
            } else {
                usa.setApprovalStatus(ApprovalStatus.DENIED);
                usa.setDeniedDate(outcomeDate);
            }
        }).collect(Collectors.toUnmodifiableList());
        return repository.saveAll(updated);
    }

    private long makeLoginCount(User user) {
        final var userAgeMillis = new Date().toInstant().toEpochMilli() - user.getCreated().toInstant().toEpochMilli();
        final var userAgeDays = userAgeMillis / (1000 * 60 * 60 * 24);
        return 1 + LongStream.range(0, userAgeDays).map(i -> randomLongBetween(0, 2)).sum();
    }

    private List<LoginAttempt> simulateLoginAttempts(
            List<User> users
    ) {
        return users
                .parallelStream()
                .flatMap(user -> LongStream
                        .range(0, makeLoginCount(user))
                        .mapToObj(i -> new LoginAttempt(null, user.getUsername(), LoginAttemptType.SUCCESSFUL, randomDate(user.getCreated(), new Date())))
                )
                .collect(Collectors.toUnmodifiableList());

    }

    private Pair<List<User>, List<Badge>> simulateBadgesAndReputation(
            List<User> users,
            List<UserServiceApproval> approvals,
            List<Rating> ratings
    ) {
        final var userReputationMap = new HashMap<Long, Long>();
        users.forEach(u -> userReputationMap.put(u.getId(), 10L));

        ratings.forEach(r -> {
            final var createdUserId = r.getService().getCreatedUser().getId();
            userReputationMap.put(createdUserId, userReputationMap.get(createdUserId) + 5);
            userReputationMap.put(r.getRater().getId(), userReputationMap.get(r.getRater().getId()) + 5);
        });

        final var userJoinedSvcCountMap = approvals
                .parallelStream()
                .filter(usa -> usa.getApprovalStatus().equals(ApprovalStatus.APPROVED))
                .collect(Collectors.groupingBy(usa -> usa.getUser().getId()))
                .entrySet()
                .parallelStream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().size()));

        final var badges = users
                .parallelStream()
                .map(user -> Pair.of(user, userJoinedSvcCountMap.get(user.getId())))
                .filter(pair -> pair.getRight() < 10 || pair.getRight() >= 20)
                .map(pair -> new Badge(pair.getLeft(), pair.getRight() >= 20 ? BadgeType.regular : BadgeType.newcomer))
                .collect(Collectors.toList());

        final var reputationUpdatedUsers = users
                .parallelStream()
                .peek(user -> {
                    user.setReputationPoint(Math.toIntExact(userReputationMap.get(user.getId())));
                })
                .collect(Collectors.toUnmodifiableList());

        return Pair.of(reputationUpdatedUsers, badges);
    }

    private List<Rating> simulateRatings(
            List<UserServiceApproval> approvals
    ) {
        return approvals
                .parallelStream()
                .filter(usa -> LocalDateTime.now().isAfter(usa.getService().getTime().plusMinutes(usa.getService().getCredit())))
                .filter(usa -> usa.getApprovalStatus().equals(ApprovalStatus.APPROVED))
                .map(usa -> new Rating(usa.getService(), (int) randomLongBetween(2, 6), usa.getUser()))
                .collect(Collectors.toUnmodifiableList());

    }

    private List<Notification> simulateNotifications(
            List<UserServiceApproval> approvals,
            List<UserFollowing> followGraph,
            List<Service> services
    ) {
        final var followNotifications = followGraph
                .parallelStream()
                .map(fg ->  new Notification(
                        null,
                        "You are being followed by " + fg.getFollowingUser().getUsername(),
                        "/profile/" + fg.getFollowingUser().getUsername(),
                        true,
                        fg.getFollowedUser(),
                        fromDate(fg.getCreated()))
                ).collect(Collectors.toUnmodifiableList());



        final var usaNotifications = approvals
                .parallelStream()
                .flatMap(usa -> {
                    final var service = usa.getService();
                    final var serviceApplicant = usa.getUser();
                    final var svcUrl = "/service/" + service.getId();

                    final var creationNotification = new Notification(
                            null,
                            "Hooray! There is a new request for " + service.getHeader() + " by " + serviceApplicant.getUsername(),
                            svcUrl,
                            true,
                            service.getCreatedUser(),
                            fromDate(usa.getCreated())
                    );
                    final var collector = new ArrayList<>(List.of(creationNotification));
                    switch (usa.getApprovalStatus()) {
                        case APPROVED:
                            collector.add(new Notification(
                                    null,
                                    "Your request for service " + service.getHeader() + " has been " + usa.getApprovalStatus().name().toLowerCase(),
                                    svcUrl,
                                    true,
                                    serviceApplicant,
                                    fromDate(usa.getApprovedDate())
                            ));

                            collector.add(new Notification(
                                    null,
                                    "Your request for service " + service.getHeader() + " has been " + usa.getApprovalStatus().name().toLowerCase(),
                                    svcUrl,
                                    true,
                                    serviceApplicant,
                                    fromDate(usa.getApprovedDate())
                            ));

                            final var svcEndTime = service.getTime().plusMinutes(service.getCredit());
                            if (svcEndTime.isBefore(LocalDateTime.now())) {
                                // service is over

                                collector.add(new Notification(
                                        null,
                                        "It looks like " + service.getHeader() + " has been over. Please confirm it to complete this service.",
                                        svcUrl,
                                        true,
                                        serviceApplicant,
                                        svcEndTime
                                ));

                                collector.add(new Notification(
                                        null,
                                        "The service " + service.getHeader() + " is complete. Your balance has been updated",
                                        svcUrl,
                                        true,
                                        serviceApplicant,
                                        svcEndTime
                                ));
                            }
                            break;
                        case DENIED:
                            collector.add(new Notification(
                                    null,
                                    "Your request for service " + service.getHeader() + " has been denied.",
                                    svcUrl,
                                    true,
                                    serviceApplicant,
                                    fromDate(usa.getDeniedDate()))
                            );
                            break;
                        case PENDING:
                            break;
                    }

                    return collector.stream();

                })
                .collect(Collectors.toUnmodifiableList());

        final var svcOverNotifications = services
                .parallelStream()
                .filter(svc -> LocalDateTime.now().isAfter(svc.getTime().plusMinutes(svc.getCredit())))
                .map(service -> new Notification(
                        null,
                        "The service " + service.getHeader() + " is complete. Your balance has been updated",
                        "/service/" + service.getId(),
                        true,
                        service.getCreatedUser(),
                        service.getTime().plusMinutes(service.getCredit())
                ))
                .collect(Collectors.toUnmodifiableList());

        return Stream.of(
                followNotifications,
                svcOverNotifications,
                usaNotifications
        ).flatMap(Collection::stream).collect(Collectors.toUnmodifiableList());
    }

    private SimulatedServiceResults persistSimulatedResults(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            BadgeRepository badgeRepository,
            LoginAttemptRepository loginAttemptRepository,
            RatingRepository ratingRepository,
            SimulatedServiceResults results
    ) {
        return new SimulatedServiceResults(
                badgeRepository.saveAll(results.badges),
                userRepository.saveAll(results.updatedUsers),
                notificationRepository.saveAll(results.notifications),
                loginAttemptRepository.saveAll(results.loginAttempts),
                ratingRepository.saveAll(results.ratings)
        );
    }

    private SimulatedServiceResults simulateServiceResults(
        List<UserServiceApproval> serviceApprovals,
        List<User> users,
        List<UserFollowing> followGraph,
        List<Service> services
    ) {
        final var simulatedRatings = simulateRatings(serviceApprovals);
        final var simulatedNotifications = simulateNotifications(serviceApprovals, followGraph, services);
        final var simulatedLoginAttempts = simulateLoginAttempts(users);
        final var simulatedBadgesAndReps = simulateBadgesAndReputation(users, serviceApprovals, simulatedRatings);
        return new SimulatedServiceResults(
                simulatedBadgesAndReps.getRight(),
                simulatedBadgesAndReps.getLeft(),
                simulatedNotifications,
                simulatedLoginAttempts,
                simulatedRatings
        );
    }

    private static class SimulatedServiceResults {
        final List<Badge> badges;
        final List<User> updatedUsers;
        final List<Notification> notifications;
        final List<LoginAttempt> loginAttempts;
        final List<Rating> ratings;

        public SimulatedServiceResults(List<Badge> badges, List<User> updatedUsers, List<Notification> notifications, List<LoginAttempt> loginAttempts, List<Rating> ratings) {
            this.badges = badges;
            this.updatedUsers = updatedUsers;
            this.notifications = notifications;
            this.loginAttempts = loginAttempts;
            this.ratings = ratings;
        }
    }

}
