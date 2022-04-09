package com.swe573.socialhub.config;

import com.swe573.socialhub.domain.*;
import com.swe573.socialhub.domain.key.UserEventApprovalKey;
import com.swe573.socialhub.domain.key.UserServiceApprovalKey;
import com.swe573.socialhub.enums.*;
import com.swe573.socialhub.enums.ApprovalStatus;
import com.swe573.socialhub.enums.BadgeType;
import com.swe573.socialhub.enums.ServiceStatus;
import com.swe573.socialhub.enums.UserType;
import com.swe573.socialhub.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;

@Profile("!test")
@Configuration
class LoadDatabase {

    private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

    @Bean

    CommandLineRunner initDatabase(TagRepository tagRepository, UserRepository userRepository, ServiceRepository serviceRepository, UserServiceApprovalRepository approvalRepository, NotificationRepository notificationRepository, PasswordEncoder passwordEncoder, UserFollowingRepository userFollowingRepository, EventRepository eventRepository, UserEventApprovalRepository eventApprovalRepository, RatingRepository ratingRepository, BadgeRepository badgeRepository, FlagRepository flagRepository) {
      

        return args -> {


            //region Tags

            var tag1 = new Tag("movies");
            var tag2 = new Tag("arts");
            var tag3 = new Tag("sports");
            var tag4 = new Tag("comedy");
            var tag5 = new Tag("misc");
            var tag6 = new Tag("education");
            var tag7 = new Tag("nature");
            tagRepository.save(tag1);
            tagRepository.save(tag2);
            tagRepository.save(tag3);
            tagRepository.save(tag4);
            tagRepository.save(tag5);
            tagRepository.save(tag6);
            tagRepository.save(tag7);


            tagRepository.findAll().forEach(tag -> {
                log.info("Preloaded " + tag);
            });

            //endregion

            //region User

            var userAdmin = saveAndGetUser(userRepository, passwordEncoder, "admin", "admin@gmail.com", "No need, I am the admin!", new HashSet<Tag>() {}, 0, "41.084148", "29.035460", "Etiler", UserType.ADMIN);

            var user1 = saveAndGetUser(userRepository, passwordEncoder, "miranda", "miranda.osborne@gmail.com", "Gamer. Award-winning music buff. Social media maven. Zombie fan. Student. Professional internet fanatic. Thinker. Freelance baconaholic.", new HashSet<Tag>() {{
                add(tag2);
                add(tag5);
            }}, 2, "41.084148", "29.035460", "Etiler", UserType.USER);

            var user2 = saveAndGetUser(userRepository, passwordEncoder, "joshua", "joshua.osborne@gmail.com", "Life's uncertain. Eat dessert first.", new HashSet<Tag>() {{
                add(tag4);
                add(tag3);
                add(tag1);
            }}, 5, "41.084148", "29.035460", "Etiler", UserType.USER);

            var user3 = saveAndGetUser(userRepository, passwordEncoder, "jane", "jane.austen@gmail.com", "Probably the best TV binge-watcher you’ll ever find.", new HashSet<Tag>() {{
                add(tag4);
                add(tag5);
            }}, 2, "41.084148", "29.035460", "Etiler", UserType.USER);

            var user4 = saveAndGetUser(userRepository, passwordEncoder, "labelcaution", "labelcaution@gmail.com", "Incurable tv fan. Twitter junkie. Evil food fanatic. Certified travel maven. Social media advocate. Total thinker.", new HashSet<Tag>() {{
                add(tag1);
                add(tag6);
            }}, 3, "41.084148", "29.035460", "Etiler", UserType.USER);

            var user5 = saveAndGetUser(userRepository, passwordEncoder, "orangejuicecucumber", "orangejuicecucumber@gmail.com", "A human. Being.", new HashSet<Tag>() {{
                add(tag2);
            }}, 2, "41.084148", "29.035460", "Etiler", UserType.USER);

            var userNewcomer = saveAndGetUser(userRepository, passwordEncoder, "noob", "noob@gmail.com", " I haven't failed. I've just found 10,000 ways that won't work.", new HashSet<Tag>() { { add(tag7); add(tag4);}}, 5, "41.084148", "29.035460", "Etiler", UserType.USER);

            userRepository.findAll().forEach(user -> {
                log.info("Preloaded " + user);
            });

            //endregion

            //region Service

            var service = Service.createPhysical(null,
                    "Film Analysis",
                    "I will be teaching film analysis. This is a service that is open to people who do not have any experience in film analysis",
                    "SineBU, Boğaziçi University, Istanbul",
                    LocalDateTime.of(2022, 1, 19, 18, 0),
                    2,
                    20,
                    8, user2,
                    41.08486331615685,
                    29.033532028860485,
                    new HashSet<Tag>() {{
                        add(tag1);
                        add(tag2);
                    }});


            var service2 = Service.createPhysical(null,
                    "Football!",
                    "I will be teaching how to play football! We can have a small match afterwards as well.",
                    "Istanbul",
                    LocalDateTime.of(2022, 2, 20, 20, 0),
                    3,
                    10,
                    30, user1,
                    40.98713967228238, 28.839091492848105,
                    new HashSet<Tag>() {{
                        add(tag3);
                    }});

            var service3 = Service.createPhysical(null,
                    "Eminönü Tour",
                    "Hey everyone! I'm a professional tourist and I would like to give you a tour of Eminönü. We will start and finish at Eminönü Meydan. We will be visiting many historical places as well as bazaars. We will also visit popular restaurants.",
                    "Eminönü, Istanbul",
                    LocalDateTime.of(2021, 12, 15, 12, 0),
                    4,
                    10,
                    10, user1,
                    41.012524056384144, 28.951326923194756,
                    new HashSet<Tag>() {{
                        add(tag5);
                    }});

            var service4 = Service.createPhysical(null,
                    "Pet My Dog",
                    "Well technically this is a service from my dog but anyways you can come to Maçka Park and pet my cute dog. He won't bite(I can't promise). He's definitely worth your time.",
                    "Maçka Park, Istanbul",
                    LocalDateTime.of(2022, 2, 23, 13, 0),
                    1,
                    100,
                    29, user3,
                    41.045570653598446, 28.993261953340998,
                    new HashSet<Tag>() {{
                        add(tag3);
                        add(tag4);
                        add(tag5);
                    }});

            var service5 = Service.createPhysical(null,
                    "Talk in spanish",
                    "I'm a native spanish speaker and I would love to have  a chat with you and help you out if you are learning the language or want to improve yourselves.",
                    "Maçka Park, Istanbul",
                    LocalDateTime.of(2022, 2, 23, 13, 0),
                    1,
                    4,
                    29,
                    user4,
                    41.045570653598446, 28.993261953340998,
                    new HashSet<Tag>() {{
                        add(tag5);
                        add(tag6);
                    }});


            var service6 = Service.createPhysical(null,
                    "Camping 101",
                    "Going camping for the first time can be a challenge. Let's all go camping and I will teach you the basics like making a fire, tent making and cooking. You can go enjoy the nature afterwards",
                    "Yedigöller, Bolu",
                    LocalDateTime.of(2022, 4, 23, 13, 0),
                    6,
                    5,
                    2,
                    user5,
                    40.943974536882706, 31.75010211097686,
                    new HashSet<Tag>() {{
                        add(tag7);
                    }});



            var service7 = Service.createPhysical(null,
                    "How to cook a lasagna",
                    "I'll be teaching how to cook lasagna, everyone is welcome.",
                    "Maçka Park, Istanbul",
                    LocalDateTime.of(2022, 5, 15, 16, 0),
                    2,
                    10,
                    1,
                    user1,
                    41.045570653598446, 28.993261953340998,
                    new HashSet<Tag>() {{
                        add(tag5);
                    }});

            var service8 = Service.createPhysical(null,
                    "Candle meditation",
                    "I'll be guiding you to meditate with assistnace of a candle!",
                    "Okyanusfly Fitness Center",
                    LocalDateTime.of(2022, 1, 15, 16, 0),
                    2,
                    10,
                    2,
                    user1,
                    41.00805947202053, 29.139052057272078,
                    new HashSet<Tag>() {{
                        add(tag5);
                    }});

            service8.setStatus(ServiceStatus.COMPLETED);

            var mockEvent = new Event(
                    null,
                    "Programming Meetup",
                    "Let's write a simple game together",
                    "Kolektif House",
                    LocalDateTime.of(2022, 5, 16, 16, 0),
                    2,
                    10,
                    1,
                    user1,
                    41.145570653598446, 28.973261953340998,
                    new HashSet<Tag>() {{
                        add(tag5);
                        add(tag6);
                    }});


            var serviceNewComer = Service.createPhysical(null,
                    "D&D",
                    "Let's play a game of D&D. I'll be the storyteller.",
                    "Maçka Park, Istanbul",
                    LocalDateTime.of(2022, 8, 15, 16, 0),
                    2,
                    10,
                    1,
                    userNewcomer,
                    41.045570653598446, 28.993261953340998,
                    new HashSet<Tag>() {{
                        add(tag5);
                    }});

            eventRepository.save(mockEvent);

            serviceRepository.save(service);
            serviceRepository.save(service2);
            serviceRepository.save(service3);
            serviceRepository.save(service4);
            serviceRepository.save(service5);
            serviceRepository.save(service6);
            serviceRepository.save(service7);
            serviceRepository.save(service8);
            serviceRepository.save(serviceNewComer);


            serviceRepository.findAll().forEach(s -> {
                log.info("Preloaded " + s);
            });
            //endregion

            //region Approval
            var approval = saveAndGetApproval(approvalRepository, user1, service, ApprovalStatus.APPROVED);
            var approval2 = saveAndGetApproval(approvalRepository, user3, service, ApprovalStatus.APPROVED);
            var approval22 = saveAndGetApproval(approvalRepository, user4, service, ApprovalStatus.APPROVED);
            var approval21 = saveAndGetApproval(approvalRepository, user5, service, ApprovalStatus.APPROVED);
            var approval3 = saveAndGetApproval(approvalRepository, user2, service2, ApprovalStatus.DENIED);
            var approval4 = saveAndGetApproval(approvalRepository, user3, service2, ApprovalStatus.APPROVED);
            var approval41 = saveAndGetApproval(approvalRepository, user4, service2, ApprovalStatus.APPROVED);
            var approval42 = saveAndGetApproval(approvalRepository, user5, service2, ApprovalStatus.APPROVED);
            var approval5 = saveAndGetApproval(approvalRepository, user3, service3, ApprovalStatus.APPROVED);
            var approval6 = saveAndGetApproval(approvalRepository, user2, service3, ApprovalStatus.PENDING);
            var approval8 = saveAndGetApproval(approvalRepository, user2, service4, ApprovalStatus.APPROVED);
            var approval81 = saveAndGetApproval(approvalRepository, user1, service4, ApprovalStatus.APPROVED);
            var approval82 = saveAndGetApproval(approvalRepository, user3, service4, ApprovalStatus.APPROVED);
            var approval9 = saveAndGetApproval(approvalRepository, user1, service4, ApprovalStatus.DENIED);
            var approval10 = saveAndGetApproval(approvalRepository, user1, service5, ApprovalStatus.PENDING);
            var approval102 = saveAndGetApproval(approvalRepository, user1, service6, ApprovalStatus.APPROVED);
            var approval103 = saveAndGetApproval(approvalRepository, user2, service6, ApprovalStatus.APPROVED);
            var approval104 = saveAndGetApproval(approvalRepository, user3, service6, ApprovalStatus.APPROVED);
            var approval105 = saveAndGetApproval(approvalRepository, user2, service7, ApprovalStatus.PENDING);
            var approval106 = saveAndGetApproval(approvalRepository, user3, service7, ApprovalStatus.APPROVED);
            var approval107 = saveAndGetApproval(approvalRepository, user4, service7, ApprovalStatus.APPROVED);
            var approval108 = saveAndGetApproval(approvalRepository, user5, service7, ApprovalStatus.APPROVED);
            var approval113 = saveAndGetApproval(approvalRepository, user4, service8, ApprovalStatus.APPROVED);
            var approval114 = saveAndGetApproval(approvalRepository, user5, service8, ApprovalStatus.APPROVED);
            var approval115 = saveAndGetApproval(approvalRepository, userNewcomer, service7, ApprovalStatus.APPROVED);
            var approval116 = saveAndGetApproval(approvalRepository, user1, serviceNewComer, ApprovalStatus.APPROVED);


            var approval109 = saveAndGetApproval(eventApprovalRepository, user2, mockEvent, ApprovalStatus.PENDING);
            var approval110 = saveAndGetApproval(eventApprovalRepository, user3, mockEvent, ApprovalStatus.APPROVED);
            var approval111 = saveAndGetApproval(eventApprovalRepository, user4, mockEvent, ApprovalStatus.APPROVED);
            var approval112 = saveAndGetApproval(eventApprovalRepository, user5, mockEvent, ApprovalStatus.APPROVED);

            approvalRepository.findAll().forEach(s -> {
                log.info("Preloaded " + s);
            });

            //endregion

            //region notification

            var notif1 = saveAndGetNotification(userRepository, notificationRepository, "Your Request for Service Film Analysis has been approved.", "/service/" + service.getId(), false, user1, LocalDateTime.now());
            var notif2 = saveAndGetNotification(userRepository, notificationRepository, "Your Request for Service Film Analysis has been sent.", "/service/" + service.getId(), true, user1, LocalDateTime.now().minusDays(2));
            var notif3 = saveAndGetNotification(userRepository, notificationRepository, "Your Service Eminönü Tour's date has passed, don't forget to approve the service!", "/service/" + service3.getId(), false, user1, LocalDateTime.now());
            var notif4 = saveAndGetNotification(userRepository, notificationRepository, "Hooray! There is a new request for Eminönü Tour by jane! You can approve or deny this request. ", "/service/" + service3.getId(), false, user1, LocalDateTime.now().minusDays(3));
            var notif6 = saveAndGetNotification(userRepository, notificationRepository, "Hooray! There is a new request for Football! by joshua! You can approve or deny this request.", "/service/" + service2.getId(), true, user1, LocalDateTime.now().minusDays(5));
            var notif5 = saveAndGetNotification(userRepository, notificationRepository, "Hooray! There is a new request for Eminönü Tour by joshua! You can approve or deny this request.", "/service/" + service3.getId(), true, user1, LocalDateTime.now().minusDays(4));
            notificationRepository.findAll().forEach(s -> {
                log.info("Preloaded " + s);
            });
            //endregion

            //region Following
            var following = saveAndGetUserFollowing(userFollowingRepository, user1, user2);
            var following1 = saveAndGetUserFollowing(userFollowingRepository, user2, user1);
            var following2 = saveAndGetUserFollowing(userFollowingRepository, user3, user1);
            var following3 = saveAndGetUserFollowing(userFollowingRepository, user3, user2);
            var following8 = saveAndGetUserFollowing(userFollowingRepository, user4, user1);
            var following4 = saveAndGetUserFollowing(userFollowingRepository, user4, user2);
            var following5 = saveAndGetUserFollowing(userFollowingRepository, user4, user3);
            var following6 = saveAndGetUserFollowing(userFollowingRepository, user4, user5);
            var following7 = saveAndGetUserFollowing(userFollowingRepository, user5, user1);
            var following9 = saveAndGetUserFollowing(userFollowingRepository, user5, user3);

            userFollowingRepository.findAll().forEach(s -> {
                log.info("Preloaded " + s);
            });
            //endregion

            //region Rating
            var rating1 = saveRatingForService(ratingRepository, user4, service8, 2);
            var rating2 = saveRatingForService(ratingRepository, user5, service8, 2);
            //endregion


            //region Flagging

            //miranda is flagging users and cannot avoid getting flagged
            var flag1 = saveFlagForTargetUser(flagRepository, user1, user4.getId());
            var flag2 = saveFlagForTargetUser(flagRepository, user4, user1.getId());

            //miranda flags service6
            var flag3 = saveFlagForTargetService(flagRepository, user1, service6);

            //miranda flags the mockevent
            var flag4 = saveFlagForTargetEvent(flagRepository, user1, mockEvent);


            //region Badge
            var badge1 = saveAndGetBadge(badgeRepository,user1, BadgeType.guru);
            var badge2 = saveAndGetBadge(badgeRepository,user1, BadgeType.superMentor);
            var badge3 = saveAndGetBadge(badgeRepository,user1, BadgeType.regular);
            var badge4 = saveAndGetBadge(badgeRepository, userNewcomer,BadgeType.newcomer);
            var badge5 = saveAndGetBadge(badgeRepository, user2,BadgeType.mentor);
            var badge6 = saveAndGetBadge(badgeRepository, user3,BadgeType.mentor);
            var badge7 = saveAndGetBadge(badgeRepository, user4,BadgeType.regular);
            var badge8 = saveAndGetBadge(badgeRepository, user4,BadgeType.superMentor);

            //endregion

        };
    }

    private User saveAndGetUser(UserRepository userRepository, PasswordEncoder passwordEncoder, String username, String email, String bio, HashSet<Tag> tags, Integer balance, String latitude, String longitude, String formattedAddress, UserType userType) {
        var user = new User(null, username, email, bio, tags, balance,latitude,longitude, formattedAddress, userType);
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
}