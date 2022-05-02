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

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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

            final var tags = createTags(tagRepository);
            final var faker = new Faker();
            final var users = setCreatedForUsers(createUsers(userRepository, passwordEncoder, faker, 500, tags), userRepository);
            final var svcs = setCreatedForServices(createServices(serviceRepository, users, faker, tags), serviceRepository);
            final var followGraph = setCreated(makeFollowerGraph(users, userFollowingRepository), userFollowingRepository);
            final var requestGraph = setDatesAndOutcome(makeServiceRequestGraph(followGraph, users, svcs, approvalRepository), approvalRepository);
            // TODO: set service handshakes/badges/user balances/reputation
            System.out.println("Created " + svcs.size() + " services.");
            System.out.println("Created " + followGraph.size() + " UserFollowing.");
            System.out.println("Created " + requestGraph.size() + " join requests.");

//
//            saveLoginAttempts(loginAttemptRepository, users);
//
//            //endregion
//
//            //region Service
//
//            var service = Service.createPhysical(null,
//                    "Film Analysis",
//                    "I will be teaching film analysis. This is a service that is open to people who do not have any experience in film analysis",
//                    "SineBU, Boğaziçi University, Istanbul",
//                    LocalDateTime.of(2022, 1, 19, 18, 0),
//                    2,
//                    20,
//                    8, user2,
//                    41.08486331615685,
//                    29.033532028860485,
//                    new HashSet<Tag>() {{
//                        add(tag1);
//                        add(tag2);
//                    }});
//
//
//            var service2 = Service.createPhysical(null,
//                    "Football!",
//                    "I will be teaching how to play football! We can have a small match afterwards as well.",
//                    "Istanbul",
//                    LocalDateTime.of(2022, 2, 20, 20, 0),
//                    3,
//                    10,
//                    30, user1,
//                    40.98713967228238, 28.839091492848105,
//                    new HashSet<Tag>() {{
//                        add(tag3);
//                    }});
//
//            var service3 = Service.createPhysical(null,
//                    "Eminönü Tour",
//                    "Hey everyone! I'm a professional tourist and I would like to give you a tour of Eminönü. We will start and finish at Eminönü Meydan. We will be visiting many historical places as well as bazaars. We will also visit popular restaurants.",
//                    "Eminönü, Istanbul",
//                    LocalDateTime.of(2021, 12, 15, 12, 0),
//                    4,
//                    10,
//                    10, user1,
//                    41.012524056384144, 28.951326923194756,
//                    new HashSet<Tag>() {{
//                        add(tag5);
//                    }});
//
//            var service4 = Service.createPhysical(null,
//                    "Pet My Dog",
//                    "Well technically this is a service from my dog but anyways you can come to Maçka Park and pet my cute dog. He won't bite(I can't promise). He's definitely worth your time.",
//                    "Maçka Park, Istanbul",
//                    LocalDateTime.of(2022, 2, 23, 13, 0),
//                    1,
//                    100,
//                    29, user3,
//                    41.045570653598446, 28.993261953340998,
//                    new HashSet<Tag>() {{
//                        add(tag3);
//                        add(tag4);
//                        add(tag5);
//                    }});
//
//            var service5 = Service.createPhysical(null,
//                    "Talk in spanish",
//                    "I'm a native spanish speaker and I would love to have  a chat with you and help you out if you are learning the language or want to improve yourselves.",
//                    "Maçka Park, Istanbul",
//                    LocalDateTime.of(2022, 2, 23, 13, 0),
//                    1,
//                    4,
//                    29,
//                    user4,
//                    41.045570653598446, 28.993261953340998,
//                    new HashSet<Tag>() {{
//                        add(tag5);
//                        add(tag6);
//                    }});
//
//
//            var service6 = Service.createPhysical(null,
//                    "Camping 101",
//                    "Going camping for the first time can be a challenge. Let's all go camping and I will teach you the basics like making a fire, tent making and cooking. You can go enjoy the nature afterwards",
//                    "Yedigöller, Bolu",
//                    LocalDateTime.of(2022, 4, 23, 13, 0),
//                    6,
//                    5,
//                    2,
//                    user5,
//                    40.943974536882706, 31.75010211097686,
//                    new HashSet<Tag>() {{
//                        add(tag7);
//                    }});
//
//
//
//            var service7 = Service.createPhysical(null,
//                    "How to cook a lasagna",
//                    "I'll be teaching how to cook lasagna, everyone is welcome.",
//                    "Maçka Park, Istanbul",
//                    LocalDateTime.of(2022, 5, 15, 16, 0),
//                    2,
//                    10,
//                    1,
//                    user1,
//                    41.045570653598446, 28.993261953340998,
//                    new HashSet<Tag>() {{
//                        add(tag5);
//                    }});
//
//            var service8 = Service.createPhysical(null,
//                    "Candle meditation",
//                    "I'll be guiding you to meditate with assistnace of a candle!",
//                    "Okyanusfly Fitness Center",
//                    LocalDateTime.of(2022, 1, 15, 16, 0),
//                    2,
//                    10,
//                    2,
//                    user1,
//                    41.00805947202053, 29.139052057272078,
//                    new HashSet<Tag>() {{
//                        add(tag5);
//                    }});
//
//            service8.setStatus(ServiceStatus.COMPLETED);
//
//            var mockEvent = new Event(
//                    null,
//                    "Programming Meetup",
//                    "Let's write a simple game together",
//                    "Kolektif House",
//                    LocalDateTime.of(2022, 5, 16, 16, 0),
//                    2,
//                    10,
//                    1,
//                    user1,
//                    41.145570653598446, 28.973261953340998,
//                    new HashSet<Tag>() {{
//                        add(tag5);
//                        add(tag6);
//                    }});
//
//
//            var serviceNewComer = Service.createPhysical(null,
//                    "D&D",
//                    "Let's play a game of D&D. I'll be the storyteller.",
//                    "Maçka Park, Istanbul",
//                    LocalDateTime.of(2022, 8, 15, 16, 0),
//                    2,
//                    10,
//                    1,
//                    userNewcomer,
//                    41.045570653598446, 28.993261953340998,
//                    new HashSet<Tag>() {{
//                        add(tag5);
//                    }});
//
//            var orangeSearchService = Service.createPhysical(null,
//                    "Let's pick some fruit",
//                    "I have many fruit trees in my garden. You can come pick oranges and apples during this season.",
//                    "My Garden in Bahçeköy",
//                    LocalDateTime.of(2022, 6, 15, 10, 0),
//                    120,
//                    20,
//                    1,
//                    user3,
//                    41.53123, 28.15247,
//                    new HashSet<Tag>() {{
//                        add(tag7);
//                    }});
//
//
//
//            //region feature service
//            service.setFeatured(true);
//            service3.setFeatured(true);
//            service8.setFeatured(true);
//            //endregion
//
//
//            serviceRepository.save(service);
//            serviceRepository.save(service2);
//            serviceRepository.save(service3);
//            serviceRepository.save(service4);
//            serviceRepository.save(service5);
//            serviceRepository.save(service6);
//            serviceRepository.save(service7);
//            serviceRepository.save(service8);
//            serviceRepository.save(serviceNewComer);
//            serviceRepository.save(orangeSearchService);
//            eventRepository.save(mockEvent);
//
//
//
//            serviceRepository.findAll().forEach(s -> {
//                log.info("Preloaded " + s);
//            });
//            //endregion
//
//            //region Approval
//            var approval = saveAndGetApproval(approvalRepository, user1, service, ApprovalStatus.APPROVED);
//            var approval2 = saveAndGetApproval(approvalRepository, user3, service, ApprovalStatus.APPROVED);
//            var approval22 = saveAndGetApproval(approvalRepository, user4, service, ApprovalStatus.APPROVED);
//            var approval21 = saveAndGetApproval(approvalRepository, user5, service, ApprovalStatus.APPROVED);
//            var approval3 = saveAndGetApproval(approvalRepository, user2, service2, ApprovalStatus.DENIED);
//            var approval4 = saveAndGetApproval(approvalRepository, user3, service2, ApprovalStatus.APPROVED);
//            var approval41 = saveAndGetApproval(approvalRepository, user4, service2, ApprovalStatus.APPROVED);
//            var approval42 = saveAndGetApproval(approvalRepository, user5, service2, ApprovalStatus.APPROVED);
//            var approval5 = saveAndGetApproval(approvalRepository, user3, service3, ApprovalStatus.APPROVED);
//            var approval6 = saveAndGetApproval(approvalRepository, user2, service3, ApprovalStatus.PENDING);
//            var approval8 = saveAndGetApproval(approvalRepository, user2, service4, ApprovalStatus.APPROVED);
//            var approval81 = saveAndGetApproval(approvalRepository, user1, service4, ApprovalStatus.APPROVED);
//            var approval82 = saveAndGetApproval(approvalRepository, user3, service4, ApprovalStatus.APPROVED);
//            var approval9 = saveAndGetApproval(approvalRepository, user1, service4, ApprovalStatus.DENIED);
//            var approval10 = saveAndGetApproval(approvalRepository, user1, service5, ApprovalStatus.PENDING);
//            var approval102 = saveAndGetApproval(approvalRepository, user1, service6, ApprovalStatus.APPROVED);
//            var approval103 = saveAndGetApproval(approvalRepository, user2, service6, ApprovalStatus.APPROVED);
//            var approval104 = saveAndGetApproval(approvalRepository, user3, service6, ApprovalStatus.APPROVED);
//            var approval105 = saveAndGetApproval(approvalRepository, user2, service7, ApprovalStatus.PENDING);
//            var approval106 = saveAndGetApproval(approvalRepository, user3, service7, ApprovalStatus.APPROVED);
//            var approval107 = saveAndGetApproval(approvalRepository, user4, service7, ApprovalStatus.APPROVED);
//            var approval108 = saveAndGetApproval(approvalRepository, user5, service7, ApprovalStatus.APPROVED);
//            var approval113 = saveAndGetApproval(approvalRepository, user4, service8, ApprovalStatus.APPROVED);
//            var approval114 = saveAndGetApproval(approvalRepository, user5, service8, ApprovalStatus.APPROVED);
//            var approval115 = saveAndGetApproval(approvalRepository, userNewcomer, service7, ApprovalStatus.APPROVED);
//            var approval116 = saveAndGetApproval(approvalRepository, user1, serviceNewComer, ApprovalStatus.APPROVED);
//
//
//            var approval109 = saveAndGetApproval(eventApprovalRepository, user2, mockEvent, ApprovalStatus.PENDING);
//            var approval110 = saveAndGetApproval(eventApprovalRepository, user3, mockEvent, ApprovalStatus.APPROVED);
//            var approval111 = saveAndGetApproval(eventApprovalRepository, user4, mockEvent, ApprovalStatus.APPROVED);
//            var approval112 = saveAndGetApproval(eventApprovalRepository, user5, mockEvent, ApprovalStatus.APPROVED);
//
//            approvalRepository.findAll().forEach(s -> {
//                log.info("Preloaded " + s);
//            });
//
//            //endregion
//
//            //region notification
//
//            var notif1 = saveAndGetNotification(userRepository, notificationRepository, "Your Request for Service Film Analysis has been approved.", "/service/" + service.getId(), false, user1, LocalDateTime.now());
//            var notif2 = saveAndGetNotification(userRepository, notificationRepository, "Your Request for Service Film Analysis has been sent.", "/service/" + service.getId(), true, user1, LocalDateTime.now().minusDays(2));
//            var notif3 = saveAndGetNotification(userRepository, notificationRepository, "Your Service Eminönü Tour's date has passed, don't forget to approve the service!", "/service/" + service3.getId(), false, user1, LocalDateTime.now());
//            var notif4 = saveAndGetNotification(userRepository, notificationRepository, "Hooray! There is a new request for Eminönü Tour by jane! You can approve or deny this request. ", "/service/" + service3.getId(), false, user1, LocalDateTime.now().minusDays(3));
//            var notif6 = saveAndGetNotification(userRepository, notificationRepository, "Hooray! There is a new request for Football! by joshua! You can approve or deny this request.", "/service/" + service2.getId(), true, user1, LocalDateTime.now().minusDays(5));
//            var notif5 = saveAndGetNotification(userRepository, notificationRepository, "Hooray! There is a new request for Eminönü Tour by joshua! You can approve or deny this request.", "/service/" + service3.getId(), true, user1, LocalDateTime.now().minusDays(4));
//            notificationRepository.findAll().forEach(s -> {
//                log.info("Preloaded " + s);
//            });
//            //endregion
//
//            //region Following
//            var following = saveAndGetUserFollowing(userFollowingRepository, user1, user2);
//            var following1 = saveAndGetUserFollowing(userFollowingRepository, user2, user1);
//            var following2 = saveAndGetUserFollowing(userFollowingRepository, user3, user1);
//            var following3 = saveAndGetUserFollowing(userFollowingRepository, user3, user2);
//            var following8 = saveAndGetUserFollowing(userFollowingRepository, user4, user1);
//            var following4 = saveAndGetUserFollowing(userFollowingRepository, user4, user2);
//            var following5 = saveAndGetUserFollowing(userFollowingRepository, user4, user3);
//            var following6 = saveAndGetUserFollowing(userFollowingRepository, user4, user5);
//            var following7 = saveAndGetUserFollowing(userFollowingRepository, user5, user1);
//            var following9 = saveAndGetUserFollowing(userFollowingRepository, user5, user3);
//
//            userFollowingRepository.findAll().forEach(s -> {
//                log.info("Preloaded " + s);
//            });
//            //endregion
//
//            //region Rating
//            var rating1 = saveRatingForService(ratingRepository, user4, service8, 2);
//            var rating2 = saveRatingForService(ratingRepository, user5, service8, 2);
//            //endregion
//
//
//            //region Flagging
//
//            //miranda is flagging users and cannot avoid getting flagged
//            var flag1 = saveFlagForTargetUser(flagRepository, user1, user4.getId());
//            var flag2 = saveFlagForTargetUser(flagRepository, user4, user1.getId());
//
//            //miranda flags service6
//            var flag3 = saveFlagForTargetService(flagRepository, user1, service6);
//
//            //miranda flags the mockevent
//            var flag4 = saveFlagForTargetEvent(flagRepository, user1, mockEvent);
//
//
//            //region Badge
//            var badge1 = saveAndGetBadge(badgeRepository,user1, BadgeType.guru);
//            var badge2 = saveAndGetBadge(badgeRepository,user1, BadgeType.superMentor);
//            var badge3 = saveAndGetBadge(badgeRepository,user1, BadgeType.regular);
//            var badge4 = saveAndGetBadge(badgeRepository, userNewcomer,BadgeType.newcomer);
//            var badge5 = saveAndGetBadge(badgeRepository, user2,BadgeType.mentor);
//            var badge6 = saveAndGetBadge(badgeRepository, user3,BadgeType.mentor);
//            var badge7 = saveAndGetBadge(badgeRepository, user4,BadgeType.regular);
//            var badge8 = saveAndGetBadge(badgeRepository, user4,BadgeType.superMentor);
//
//            //endregion

        };
    }

    private User saveAndGetUser(UserRepository userRepository, PasswordEncoder passwordEncoder, String username, String email, String bio, HashSet<Tag> tags, Integer balance, String latitude, String longitude, String formattedAddress, UserType userType, int reputationPoint) {
        var user = new User(null, username, email, bio, tags, balance,latitude,longitude, formattedAddress, userType, reputationPoint);
        user.setPassword(passwordEncoder.encode("1"));
        userRepository.save(user);
        return user;
    }

    private UserFollowing saveAndGetUserFollowing(UserFollowingRepository userFollowingRepository, User user1, User user2) {
        var userFollowing = new UserFollowing(user1, user2);
        userFollowingRepository.save(userFollowing);
        return userFollowing;
    }

    private Notification saveAndGetNotification(UserRepository userRepository, NotificationRepository notificationRepository, String message, String url, Boolean read, User user, LocalDateTime sentDate) {
        var notification = new Notification(null, message, url, read, user, sentDate);
        notificationRepository.save(notification);
        return notification;
    }

    private UserServiceApproval saveAndGetApproval(UserServiceApprovalRepository approvalRepository, User user1, Service service, ApprovalStatus approved) {
        var approval = new UserServiceApproval(new UserServiceApprovalKey(user1.getId(), service.getId()), user1, service, approved);
        approvalRepository.save(approval);
        return approval;
    }

    private UserEventApproval saveAndGetApproval(UserEventApprovalRepository approvalRepository, User user1, Event event, ApprovalStatus approved) {
        var approval = new UserEventApproval(new UserEventApprovalKey(user1.getId(), event.getId()), user1, event, approved);
        approvalRepository.save(approval);
        return approval;
    }

    private Rating saveRatingForService(RatingRepository ratingRepository, User user1, Service service, int ratingParam){
        var rating = new Rating(service,ratingParam,user1);
        ratingRepository.save(rating);
        return rating;
    }

    private Flag saveFlagForTargetUser(FlagRepository flagRepository, User user1, long targetUserId){
        var flag = new Flag(FlagType.user, user1.getId(), targetUserId, FlagStatus.active);
        flagRepository.save(flag);
        return flag;
    }

    private Flag saveFlagForTargetService(FlagRepository flagRepository, User user1, Service service){
        var flag = new Flag(FlagType.service, user1.getId(), service.getId(), FlagStatus.active);
        flagRepository.save(flag);
        return flag;
    }

    private Flag saveFlagForTargetEvent(FlagRepository flagRepository, User user1, Event event) {
        Flag flag = new Flag(FlagType.event, user1.getId(), event.getId(), FlagStatus.active);
        flagRepository.save(flag);
        return flag;
    }

    private Badge saveAndGetBadge(BadgeRepository badgeRepository, User user, BadgeType badgeType) {
        var badge = new Badge(user, badgeType);
        badgeRepository.save(badge);
        return badge;
    }

    private List<LoginAttempt> saveLoginAttempts(LoginAttemptRepository repository, List<User> users) {
        var attempts = users
                .stream()
                .map(u -> new LoginAttempt(0L, u.getUsername(), LoginAttemptType.SUCCESSFUL, new Date()))
                .collect(Collectors.toList());
        return repository.saveAll(attempts);
    }

    private static final Date SITE_CREATION_DATE = new Date(1619868495782L);

    private Date randomDate(Date min, Date max) {
        final var minMillis = min.toInstant().toEpochMilli();
        final var maxMillis = max.toInstant().toEpochMilli();
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
            var u = new User(null, username, email, bio, new HashSet<>(chooseManyBetween(tags, (int) randomLongBetween(0, 3))), 0, faker.address().latitude(), faker.address().longitude(), faker.address().fullAddress(), UserType.USER, 0);
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
