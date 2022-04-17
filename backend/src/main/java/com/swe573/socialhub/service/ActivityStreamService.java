package com.swe573.socialhub.service;


import com.google.common.base.Joiner;
import com.google.common.base.Supplier;
import com.ibm.common.activitystreams.Activity;
import com.ibm.common.activitystreams.Collection;
import com.ibm.common.activitystreams.LinkValue;
import com.swe573.socialhub.domain.*;
import com.swe573.socialhub.dto.TimestampBasedPagination;
import com.swe573.socialhub.enums.FeedEvent;
import com.swe573.socialhub.enums.LoginAttemptType;
import com.swe573.socialhub.repository.*;
import com.swe573.socialhub.repository.activitystreams.*;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ibm.common.activitystreams.Makers.*;

@Service
public class ActivityStreamService {

    private final Map<FeedEvent, ActivityMapper<?>> mappers;

    public ActivityStreamService(
            LoginAttemptRepository loginAttemptRepository,
            UserRepository userRepository,
            ServiceRepository serviceRepository,
            EventRepository eventRepository,
            UserEventApprovalRepository eventApprovalRepository,
            UserServiceApprovalRepository serviceApprovalRepository
    ) {
        final var successfulLoginAttemptRepository = new TimestampPaginatedRepository<>(new CreatedQueryableSuccessfulLoginAttemptRepository(loginAttemptRepository));
        final var unsuccessfulLoginAttemptRepository = new TimestampPaginatedRepository<>(new CreatedQueryableUnsuccessfulLoginAttemptRepository(loginAttemptRepository));
        final var serviceTimestampPaginatedRepository = new TimestampPaginatedRepository<>(serviceRepository);
        final var eventTimestampPaginatedRepository = new TimestampPaginatedRepository<>(eventRepository);
        final var tsEventApprovalRepository = new TimestampPaginatedRepository<>(eventApprovalRepository);
        final var tsServiceApprovalRepository = new TimestampPaginatedRepository<>(serviceApprovalRepository);
        final var eventApprovalTimestampPaginatedRepository = new TimestampPaginatedRepository<>(new ApprovedQueryableEventApprovalRepository(eventApprovalRepository));
        final var serviceApprovalTimestampPaginatedRepository = new TimestampPaginatedRepository<>(new ApprovedQueryableServiceApprovalRepository(serviceApprovalRepository));

        this.mappers = Map.of(
                FeedEvent.EVENT_CREATED, new EventCreatedActivityMapper(new RepositoryDataSource<>(eventTimestampPaginatedRepository)),
                FeedEvent.SERVICE_CREATED, new ServiceCreatedActivityMapper(new RepositoryDataSource<>(serviceTimestampPaginatedRepository)),
                FeedEvent.USER_LOGIN_SUCCESSFUL, new UserLoginActivityMapper(userRepository, new RepositoryDataSource<>(successfulLoginAttemptRepository)),
                FeedEvent.USER_LOGIN_FAILED, new UserLoginActivityMapper(userRepository, new RepositoryDataSource<>(unsuccessfulLoginAttemptRepository))
        );



    }

    public Collection fetchFeed(Set<FeedEvent> eventTypes, TimestampBasedPagination pagination) {
        final var activityStreams = eventTypes
                .parallelStream()
                .map(et -> mappers.get(et).fetchAndMap(pagination));
        final var masterStream = flattenStreams(activityStreams)
                .sorted(pagination.getSortDirection().isAscending() ? Comparator.comparing(Activity::published) : Comparator.comparing(Activity::published).reversed())
                .limit(pagination.getSize());

        return mapToCollection(masterStream.collect(Collectors.toUnmodifiableList()), pagination);
    }

    private <T> Stream<T> flattenStreams(Stream<Stream<T>> streams) {
        return streams.reduce(Stream.empty(), Stream::concat);
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
            final var nextPagination = pagination.nextPage(activityList.get(activityList.size() - 1).published().toDate());
            final var nextUrl = makeUrlString(nextPagination);
            builder.pageLink(Collection.Page.NEXT, nextUrl);
        }

        return builder.get();
    }

    private interface TimestampPaginatedDataSource<T> {
        List<T> fetch(TimestampBasedPagination query);
    }

    private static class RepositoryDataSource<T> implements TimestampPaginatedDataSource<T> {
        private final TimestampPaginatedRepository<T> repository;

        public RepositoryDataSource(TimestampPaginatedRepository<T> repository) {
            this.repository = repository;
        }

        @Override
        public List<T> fetch(TimestampBasedPagination query) {
            return repository.findAllMatching(query);
        }
    }


    private static abstract class ActivityMapper<T> {
        public abstract TimestampPaginatedDataSource<T> getDataSource();
        public Stream<Activity> map(List<T> objects) {
            return objects.stream().map(this::mapOne);
        }
        public abstract Activity mapOne(T object);
        public Stream<Activity> fetchAndMap(TimestampBasedPagination query) {
            return map(getDataSource().fetch(query));
        }
    }

    private static abstract class RepositoryBasedActivityMapper<T> extends ActivityMapper<T> {
        final RepositoryDataSource<T> dataSource;

        public RepositoryBasedActivityMapper(RepositoryDataSource<T> dataSource) {
            this.dataSource = dataSource;
        }

        @Override
        public RepositoryDataSource<T> getDataSource() {
            return dataSource;
        }
    }

    private class ServiceCreatedActivityMapper extends RepositoryBasedActivityMapper<com.swe573.socialhub.domain.Service> {
        public ServiceCreatedActivityMapper(RepositoryDataSource<com.swe573.socialhub.domain.Service> dataSource) {
            super(dataSource);
        }

        public Activity mapOne(com.swe573.socialhub.domain.Service object) {
            return activity()
                    .summary(object.getCreatedUser().getUsername() + " created a service named " + object.getHeader())
                    .verb("create")
                    .actor(mapToObject(object.getCreatedUser()))
                    .object(mapToObject(object))
                    .published(new DateTime(object.getCreated()))
                    .get();
        }
    }

    private class EventCreatedActivityMapper extends RepositoryBasedActivityMapper<Event> {
        public EventCreatedActivityMapper(RepositoryDataSource<Event> dataSource) {
            super(dataSource);
        }

        @Override
        public Activity mapOne(Event object) {
            return activity()
                    .summary(object.getCreatedUser().getUsername() + " created an event named " + object.getHeader())
                    .verb("create")
                    .actor(mapToObject(object.getCreatedUser()))
                    .object(mapToObject(object))
                    .published(new DateTime(object.getCreated()))
                    .get();
        }
    }

    private class UserLoginActivityMapper extends ActivityMapper<LoginAttempt> {
        private final TimestampPaginatedDataSource<LoginAttempt> dataSource;
        private final UserRepository repository;

        public UserLoginActivityMapper(UserRepository userRepository, TimestampPaginatedDataSource<LoginAttempt> dataSource) {
            this.repository = userRepository;
            this.dataSource = dataSource;
        }

        @Override
        public TimestampPaginatedDataSource<LoginAttempt> getDataSource() {
            return null;
        }

        @Override
        public Activity mapOne(LoginAttempt object) {
            return null;
        }

        @Override
        public Stream<Activity> fetchAndMap(TimestampBasedPagination query) {
            final var objs = dataSource.fetch(query);
            final var userNamesToFetch = objs.stream().map(LoginAttempt::getUsername).collect(Collectors.toList());
            final var userCache = repository
                    .findAllByUsername(userNamesToFetch)
                    .stream()
                    .collect(Collectors.toUnmodifiableMap(User::getUsername, Function.identity()));
            return objs.stream().map(a -> mapToActivity(a, userCache.get(a.getUsername())));
        }

        private Activity mapToActivity(LoginAttempt loginAttempt, User user) {
            Objects.requireNonNull(loginAttempt);
            if (loginAttempt.getAttemptType() == LoginAttemptType.SUCCESSFUL) {
                Objects.requireNonNull(user);
                return activity()
                        .summary(user.getUsername() + " successfully logged in")
                        .verb("login")
                        .actor(mapToObject(user))
                        .published(new DateTime(loginAttempt.getCreated()))
                        .get();
            }
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

    private Supplier<? extends LinkValue> mapToObject(Event event) {
        var idString = event.getId().toString();
        return object("event")
                .displayName(event.getHeader())
                .id(idString)
                .link("url", "/event/" + idString);
    }

    private Supplier<? extends LinkValue> mapToObject(LoginAttempt loginAttempt) {
        var idString = loginAttempt.getId().toString();
        return object("login-attempt")
                .displayName(loginAttempt.getAttemptType().toString())
                .id(idString);
    }

}
