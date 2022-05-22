package com.swe573.socialhub.service;

import com.swe573.socialhub.SocialHubApplication;
import com.swe573.socialhub.dto.*;
import com.swe573.socialhub.enums.UserType;
import com.swe573.socialhub.repository.ServiceRepository;
import com.swe573.socialhub.repository.UserRepository;
import com.swe573.socialhub.repository.UserServiceApprovalRepository;
import com.swe573.socialhub.repository.activitystreams.ApprovedQueryableServiceApprovalRepository;
import com.swe573.socialhub.repository.activitystreams.DateCountableRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.swe573.socialhub.SocialHubApplication.SITE_CREATION_DATE;

@Service
public class StatsService {

    private final UserRepository userRepository;
    private final ServiceRepository serviceRepository;
    private final ApprovedQueryableServiceApprovalRepository approvedServiceRepository;
    private final UserServiceApprovalRepository serviceApprovalRepository;

    public StatsService(UserRepository userRepository, ServiceRepository serviceRepository, UserServiceApprovalRepository serviceApprovalRepository) {
        this.userRepository = userRepository;
        this.serviceRepository = serviceRepository;
        this.approvedServiceRepository = new ApprovedQueryableServiceApprovalRepository(serviceApprovalRepository);
        this.serviceApprovalRepository = serviceApprovalRepository;
    }

    public StatsDto fetchStats(Principal principal) {
        final var loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (!loggedInUser.getUserType().equals(UserType.ADMIN)) {
            throw new IllegalArgumentException("You need to be admin to perform this action.");
        }

        // performance may be improved if done in parallel, but should be good enough
        return new StatsDto(
                fetchStatsFromRepository(serviceRepository),
                fetchStatsFromRepository(serviceApprovalRepository),
                fetchStatsFromRepository(approvedServiceRepository),
                fetchStatsFromRepository(userRepository)
        );
    }

    public PaginatedResponse<DailyStatsDto> fetchDailyStats(
            Principal principal,
            TimestampBasedPagination pagination,
            String endpointBase
    ) {
        final var loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (!loggedInUser.getUserType().equals(UserType.ADMIN)) {
            throw new IllegalArgumentException("You need to be admin to perform this action.");
        }

        if (pagination.getGreaterThan().toInstant().toEpochMilli() > pagination.getLowerThan().toInstant().toEpochMilli())
            throw new IllegalArgumentException("Invalid pagination parameters.");

        final var now = new Date();
        final var fixedGt = pagination.getGreaterThan().toInstant().toEpochMilli() < SITE_CREATION_DATE.toInstant().toEpochMilli() ? SITE_CREATION_DATE : pagination.getGreaterThan();
        final var fixedLt = pagination.getLowerThan().toInstant().toEpochMilli() > now.toInstant().toEpochMilli() ? now : pagination.getLowerThan();

        final var intervals = makeIntervalsForDailyStats(fixedLt, fixedGt);

        final var acc = new DailyStatsDto(
                new LinkedHashMap<>(),
                new LinkedHashMap<>(),
                new LinkedHashMap<>(),
                new LinkedHashMap<>()
        );

        final var dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        final var fetchStart = Instant.now();
        System.out.println("Daily stat fetch started.");
        intervals
                .forEach(interval -> {
                    acc.getApprovedServiceApplications().put(dateFormat.format(interval.getFirst()), approvedServiceRepository.countByDateBetween(interval.getFirst(), interval.getSecond()));
                    acc.getServiceApplications().put(dateFormat.format(interval.getFirst()), serviceApprovalRepository.countByDateBetween(interval.getFirst(), interval.getSecond()));
                    acc.getCreatedServices().put(dateFormat.format(interval.getFirst()), serviceRepository.countByDateBetween(interval.getFirst(), interval.getSecond()));
                    acc.getRegisteredUsers().put(dateFormat.format(interval.getFirst()), userRepository.countByDateBetween(interval.getFirst(), interval.getSecond()));
                });

        System.out.println("Daily stat fetch took " + (Instant.now().toEpochMilli() - fetchStart.toEpochMilli()) + " milliseconds.");

        final var nextPage = pagination
                .nextPage(pagination.getSortDirection().isAscending() ? fixedLt : fixedGt)
                .makeUrlString(endpointBase, "");

        return new PaginatedResponse<>(acc, nextPage);
    }

    private List<Pair<Date, Date>> makeIntervalsForDailyStats(Date fixedLt, Date fixedGt) {
        final var intervalList = new ArrayList<Pair<Date, Date>>();

        var cursor = fixedLt;
        while (cursor.toInstant().toEpochMilli() > fixedGt.toInstant().toEpochMilli()) {
            final var nextCursor = subtractHours(cursor, 24);
            intervalList.add(Pair.of((Date) nextCursor.clone(), (Date) cursor.clone()));
            cursor = nextCursor;
        }

        return intervalList;
    }

    private StatContainerDto fetchStatsFromRepository(DateCountableRepository repository) {
        final var now = new Date();

        final var queryArgs = List.of(
                Pair.of(new Date(1), now),
                Pair.of(subtractHours(now, 24), now),
                Pair.of(subtractHours(now, 24 * 7), now),
                Pair.of(subtractHours(now, 24 * 30), now)
        );

        final var queryResults = queryArgs
                .parallelStream()
                .mapToLong(q -> repository.countByDateBetween(q.getFirst(), q.getSecond()))
                .toArray();

        return new StatContainerDto(queryResults[0], queryResults[1], queryResults[2], queryResults[3]);
    }

    private Date subtractHours(Date fromDate, int times) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(fromDate);
        cal.add(Calendar.HOUR, -1 * times);
        return cal.getTime();
    }
}
