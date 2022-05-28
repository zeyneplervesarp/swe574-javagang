package com.swe573.socialhub.service;

import com.swe573.socialhub.dto.DistanceBasedPagination;
import com.swe573.socialhub.dto.ServiceDto;
import com.swe573.socialhub.enums.ServiceFilter;
import com.swe573.socialhub.enums.ServiceSortBy;
import com.swe573.socialhub.repository.ServiceRepository;
import com.swe573.socialhub.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

@Service
public class RecommendationsService {

    private final ServiceRepository serviceRepository;
    private final SearchPrioritizationService prioritizationService;
    private final UserRepository userRepository;
    private final ServiceService serviceService;

    public RecommendationsService(
            ServiceRepository serviceRepository,
            SearchPrioritizationService prioritizationService,
            UserRepository userRepository,
            ServiceService serviceService
    ) {
        this.serviceRepository = serviceRepository;
        this.prioritizationService = prioritizationService;
        this.userRepository = userRepository;
        this.serviceService = serviceService;
    }

    public List<ServiceDto> getRecommendedServices(Principal principal, DistanceBasedPagination pagination) {
        final var loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        final var entities = serviceRepository.findAllByDistanceBetweenOngoing(
                pagination.getGreaterThan(),
                pagination.getLowerThan(),
                Pageable.ofSize(pagination.getSize()),
                loggedInUser.getLatitude(),
                loggedInUser.getLongitude()
        );
        final var scores = sortByValue(prioritizationService.assignScoresToServices(entities, loggedInUser));

        final var services = new ArrayList<ServiceDto>();

        scores.forEach((k, v) -> {
            final var thisService = entities.stream().filter(s -> s.getId().equals(k)).findFirst().get();
            services.add(serviceService.mapToDto(thisService, Optional.of(loggedInUser)));
        });

        Collections.reverse(services);

        return services;
    }


    private <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());

        LinkedHashMap<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

}
