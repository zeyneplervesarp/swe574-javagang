package com.swe573.socialhub.service;


import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.ibm.common.activitystreams.Activity;
import com.ibm.common.activitystreams.Collection;
import com.ibm.common.activitystreams.LinkValue;
import com.swe573.socialhub.domain.Event;
import com.swe573.socialhub.domain.LoginAttempt;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.dto.TimestampBasedPagination;
import com.swe573.socialhub.enums.FeedEvent;
import com.swe573.socialhub.repository.EventRepository;
import com.swe573.socialhub.repository.LoginAttemptRepository;
import com.swe573.socialhub.repository.ServiceRepository;
import com.swe573.socialhub.repository.UserRepository;
import com.swe573.socialhub.repository.activitystreams.CreatedQueryableRepository;
import com.swe573.socialhub.repository.activitystreams.CreatedQueryableSuccessfulLoginAttemptRepository;
import com.swe573.socialhub.repository.activitystreams.CreatedQueryableUnsuccessfulLoginAttemptRepository;
import com.swe573.socialhub.repository.activitystreams.TimestampPaginatedRepository;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ibm.common.activitystreams.Makers.*;

@Service
public class ActivityStreamService {

    private final TimestampPaginatedRepository<LoginAttempt> successfulLoginAttemptRepository;
    private final TimestampPaginatedRepository<LoginAttempt> unsuccessfulLoginAttemptRepository;
    private final UserRepository userRepository;
    private final TimestampPaginatedRepository<com.swe573.socialhub.domain.Service> serviceTimestampPaginatedRepository;
    private final TimestampPaginatedRepository<Event> eventTimestampPaginatedRepository;

    public ActivityStreamService(
            LoginAttemptRepository loginAttemptRepository,
            UserRepository userRepository,
            ServiceRepository serviceRepository,
            EventRepository eventRepository
    ) {
        this.successfulLoginAttemptRepository = new TimestampPaginatedRepository<>(new CreatedQueryableSuccessfulLoginAttemptRepository(loginAttemptRepository));
        this.unsuccessfulLoginAttemptRepository = new TimestampPaginatedRepository<>(new CreatedQueryableUnsuccessfulLoginAttemptRepository(loginAttemptRepository));
        this.userRepository = userRepository;
        this.serviceTimestampPaginatedRepository = new TimestampPaginatedRepository<>(serviceRepository);
        this.eventTimestampPaginatedRepository = new TimestampPaginatedRepository<>(eventRepository);
    }

    public Collection fetchFeed(Set<FeedEvent> eventTypes, TimestampBasedPagination pagination) {

        // data retrieval
        final var userLogins = new ArrayList<LoginAttempt>();
        final var createdServices = new ArrayList<com.swe573.socialhub.domain.Service>();

        for (final var type : eventTypes) {
            switch (type) {
                case USER_LOGIN_FAILED:
                    userLogins.addAll(unsuccessfulLoginAttemptRepository.findAllMatching(pagination));
                    break;
                case USER_LOGIN_SUCCESSFUL:
                    userLogins.addAll(successfulLoginAttemptRepository.findAllMatching(pagination));
                    break;
                case SERVICE_CREATED:
                    createdServices.addAll(serviceTimestampPaginatedRepository.findAllMatching(pagination));
                    break;
                case SERVICE_JOIN_REQUESTED:
                    break;
                case SERVICE_JOIN_APPROVED:
                    break;
                case EVENT_CREATED:
                    break;
                case EVENT_JOIN_REQUESTED:
                    break;
                case EVENT_JOIN_APPROVED:
                    break;
            }
        }

        // mapping

        final var userNamesToFetch = userLogins.stream().map(LoginAttempt::getUsername).collect(Collectors.toList());
        final var userCache = userRepository
                .findAllByUsername(userNamesToFetch)
                .stream()
                .collect(Collectors.toUnmodifiableMap(User::getUsername, Function.identity()));


        final var userLoginActivities = userLogins.stream()
                .sorted(pagination.getSortDirection().isAscending() ? Comparator.comparing(LoginAttempt::getCreated) : Comparator.comparing(LoginAttempt::getCreated).reversed())
                .map(loginAttempt -> mapToActivity(loginAttempt, userCache.get(loginAttempt.getUsername())));

        final var serviceCreationActivities = createdServices
                .stream()
                .map(this::mapCreatedServiceActivity);

        final var masterStream = Stream
                .concat(
                        userLoginActivities,
                        serviceCreationActivities
                )
                .sorted(pagination.getSortDirection().isAscending() ? Comparator.comparing(Activity::published) : Comparator.comparing(Activity::published).reversed())
                .limit(pagination.getSize());


        return mapToCollection(masterStream.collect(Collectors.toUnmodifiableList()), pagination);
    }

    private TimestampBasedPagination makeNextPagination(Date lastDate, TimestampBasedPagination currentPagination) {
        return new TimestampBasedPagination(
                currentPagination.getSortDirection().isAscending() ? lastDate : currentPagination.getGreaterThan(),
                currentPagination.getSortDirection().isAscending() ? currentPagination.getLowerThan() : lastDate,
                currentPagination.getSize(),
                currentPagination.getSortDirection()
        );
    }

    private String makeUrlString(TimestampBasedPagination pagination) {
        var map =  Map.of("sort", pagination.getSortDirection().toString(),
                "gt", Long.toString(pagination.getGreaterThan().toInstant().toEpochMilli()),
                "lt", Long.toString(pagination.getLowerThan().toInstant().toEpochMilli()),
                "size", Integer.toString(pagination.getSize())
        );

        return Joiner.on("&").withKeyValueSeparator("=").join(map);
    }

    private Collection mapToCollection(List<Activity> activityList, TimestampBasedPagination pagination) {
        final var builder = collection()
                .items(activityList)
                .itemsPerPage(activityList.size());

        if (!activityList.isEmpty()) {
            final var nextPagination = makeNextPagination(activityList.get(activityList.size() - 1).published().toDate(), pagination);
            final var nextUrl = makeUrlString(nextPagination);
            builder.pageLink(Collection.Page.NEXT, nextUrl);
        }

        return builder.get();
    }

    private Supplier<? extends LinkValue> mapToObject(User user) {
        var idString = user.getId().toString();
        return object("user")
                .displayName(user.getUsername())
                .id(idString)
                .link("url", "/user/" + idString);
    }

    private Supplier<? extends LinkValue> mapToObject(com.swe573.socialhub.domain.Service service) {
        var idString = service.getId().toString();
        return object("service")
                .displayName(service.getHeader())
                .id(idString)
                .link("url", "/service/" + idString);
    }

    private Supplier<? extends LinkValue> mapToObject(LoginAttempt loginAttempt) {
        var idString = loginAttempt.getId().toString();
        return object("login-attempt")
                .displayName(loginAttempt.getAttemptType().toString())
                .id(idString);
    }

    private Activity mapCreatedServiceActivity(com.swe573.socialhub.domain.Service service) {
        return activity()
                .summary(service.getCreatedUser().getUsername() + " created a service named " + service.getHeader())
                .verb("create")
                .actor(mapToObject(service.getCreatedUser()))
                .object(mapToObject(service))
                .published(new DateTime(service.getCreated()))
                .get();
    }

    private Activity mapToActivity(LoginAttempt loginAttempt, User user) {
        Objects.requireNonNull(loginAttempt);
        switch (loginAttempt.getAttemptType()) {
            case SUCCESSFUL:
                Objects.requireNonNull(user);
                return activity()
                        .summary(user.getUsername() + " successfully logged in")
                        .verb("login")
                        .actor(mapToObject(user))
                        .published(new DateTime(loginAttempt.getCreated()))
                        .get();
            default:
                var obj = user != null ? mapToObject(user) : mapToObject(loginAttempt);
                return activity()
                        .summary("Someone tried to log in unsuccessfully")
                        .verb("login-failed")
                        .actor(object("unknown").displayName("Unknown person"))
                        .object(obj)
                        .published(new DateTime(loginAttempt.getCreated()))
                        .get();
        }
    }

}
