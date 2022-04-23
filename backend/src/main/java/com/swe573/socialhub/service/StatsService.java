package com.swe573.socialhub.service;

import com.swe573.socialhub.dto.StatContainerDto;
import com.swe573.socialhub.dto.StatsDto;
import com.swe573.socialhub.enums.UserType;
import com.swe573.socialhub.repository.ServiceRepository;
import com.swe573.socialhub.repository.UserRepository;
import com.swe573.socialhub.repository.UserServiceApprovalRepository;
import com.swe573.socialhub.repository.activitystreams.ApprovedQueryableServiceApprovalRepository;
import com.swe573.socialhub.repository.activitystreams.DateCountableRepository;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
