package com.swe573.socialhub.config;

import com.swe573.socialhub.domain.*;
import com.swe573.socialhub.domain.key.UserEventApprovalKey;
import com.swe573.socialhub.domain.key.UserServiceApprovalKey;
import com.swe573.socialhub.enums.ApprovalStatus;
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
    CommandLineRunner initDatabase(TagRepository tagRepository, UserRepository userRepository, ServiceRepository serviceRepository, UserServiceApprovalRepository approvalRepository, NotificationRepository notificationRepository, PasswordEncoder passwordEncoder, UserFollowingRepository userFollowingRepository, EventRepository eventRepository, UserEventApprovalRepository eventApprovalRepository) {

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

            var user1 = saveAndGetUser(userRepository, passwordEncoder, "miranda", "miranda.osborne@gmail.com", "Gamer. Award-winning music buff. Social media maven. Zombie fan. Student. Professional internet fanatic. Thinker. Freelance baconaholic.", new HashSet<Tag>() {{
                add(tag2);
                add(tag5);
            }}, 2, "41.084148", "29.035460", "Etiler");

            var user2 = saveAndGetUser(userRepository, passwordEncoder, "joshua", "joshua.osborne@gmail.com", "Life's uncertain. Eat dessert first.", new HashSet<Tag>() {{
                add(tag4);
                add(tag3);
                add(tag1);
            }}, 5, "41.084148", "29.035460", "Etiler");

            var user3 = saveAndGetUser(userRepository, passwordEncoder, "jane", "jane.austen@gmail.com", "Probably the best TV binge-watcher you’ll ever find.", new HashSet<Tag>() {{
                add(tag4);
                add(tag5);
            }}, 2, "41.084148", "29.035460", "Etiler");

            var user4 = saveAndGetUser(userRepository, passwordEncoder, "labelcaution", "labelcaution@gmail.com", "Incurable tv fan. Twitter junkie. Evil food fanatic. Certified travel maven. Social media advocate. Total thinker.", new HashSet<Tag>() {{
                add(tag1);
                add(tag6);
            }}, 3, "41.084148", "29.035460", "Etiler");

            var user5 = saveAndGetUser(userRepository, passwordEncoder, "orangejuicecucumber", "orangejuicecucumber@gmail.com", "A human. Being.", new HashSet<Tag>() {{
                add(tag2);
            }}, 2, "41.084148", "29.035460", "Etiler");

            userRepository.findAll().forEach(user -> {
                log.info("Preloaded " + user);
            });

            //endregion

            //region Service

            var service = new Service(null,
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


            var service2 = new Service(null,
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

            var service3 = new Service(null,
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

            var service4 = new Service(null,
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

            var service5 = new Service(null,
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


            var service6 = new Service(null,
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



            var service7 = new Service(null,
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

            eventRepository.save(mockEvent);

            serviceRepository.save(service);
            serviceRepository.save(service2);
            serviceRepository.save(service3);
            serviceRepository.save(service4);
            serviceRepository.save(service5);
            serviceRepository.save(service6);
            serviceRepository.save(service7);

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

        };
    }

    private User saveAndGetUser(UserRepository userRepository, PasswordEncoder passwordEncoder, String username, String email, String bio, HashSet<Tag> tags, Integer balance, String latitude, String longitude, String formattedAddress) {
        var user = new User(null, username, email, bio, tags, balance,latitude,longitude, formattedAddress);
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
}