package com.swe573.socialhub.service;

import com.swe573.socialhub.domain.Event;
import com.swe573.socialhub.domain.Tag;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.dto.SearchMatchDto;
import com.swe573.socialhub.enums.SearchMatchType;
import com.swe573.socialhub.repository.EventRepository;
import com.swe573.socialhub.repository.ServiceRepository;
import com.swe573.socialhub.repository.TagRepository;
import com.swe573.socialhub.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final UserRepository userRepository;
    private final TagRepository tagRepository;
    private final ServiceRepository serviceRepository;
    private final EventRepository eventRepository;
    private final SearchPrioritizationService prioritizationService;

    private static final int SEARCH_LIMIT = 50;

    public SearchService(UserRepository userRepository, TagRepository tagRepository, ServiceRepository serviceRepository, EventRepository eventRepository, SearchPrioritizationService prioritizationService) {
        this.userRepository = userRepository;
        this.tagRepository = tagRepository;
        this.serviceRepository = serviceRepository;
        this.eventRepository = eventRepository;
        this.prioritizationService = prioritizationService;
    }

    public List<SearchMatchDto> search(String stringToMatch, int limit, Principal principal) {
        final var loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        validate(limit);

        if (stringToMatch.isEmpty() || stringToMatch.isBlank()) {
            return Collections.emptyList();
        }

        final var searchOperations = prepareSearchOperations(stringToMatch);

        final var svcResults = searchOperations.serviceOps
                .stream()
                .flatMap(f -> f.apply(limit).stream())
                .collect(Collectors.toUnmodifiableList());

        final var userResults = searchOperations.userOps
                .stream()
                .flatMap(f -> f.apply(limit).stream())
                .collect(Collectors.toUnmodifiableList());

        final var userScores = prioritizationService.assignScoresToUsers(userResults, loggedInUser);
        final var svcScores = prioritizationService.assignScoresToServices(svcResults, loggedInUser);

        final var searchResults = new LinkedHashSet<SearchMatchDto>();

        svcResults.forEach(svc -> {
            searchResults.add(mapToDto(svc, svcScores.getOrDefault(svc.getId(), 0.0)));
        });

        userResults.forEach(user -> {
            searchResults.add(mapToDto(user, userScores.getOrDefault(user.getId(), 0.0)));
        });

        if (searchResults.size() < limit) {
            searchOperations.tagOps
                    .stream()
                    .flatMap(f -> f.apply(limit).stream())
                    .map(this::mapToDto)
                    .forEach(searchResults::add);

            searchOperations.eventOps
                    .stream()
                    .flatMap(f -> f.apply(limit).stream())
                    .map(this::mapToDto)
                    .forEach(searchResults::add);
        }

        return searchResults
                .stream()
                .sorted(Comparator.comparing(SearchMatchDto::getScore).reversed())
                .limit(limit)
                .collect(Collectors.toUnmodifiableList());
    }

    private void validate(int limit) {
        if (limit > SEARCH_LIMIT) {
            throw new IllegalArgumentException("Maximum search limit is " + SEARCH_LIMIT + ".");
        }

        if (limit < 1) {
            throw new IllegalArgumentException("Minimum search limit is 1.");
        }
    }

    private static class SearchOperations {
        final List<Function<Integer, List<com.swe573.socialhub.domain.Service>>> serviceOps;
        final List<Function<Integer, List<Event>>> eventOps;
        final List<Function<Integer, List<User>>> userOps;
        final List<Function<Integer, List<Tag>>> tagOps;

        public SearchOperations(
                List<Function<Integer, List<com.swe573.socialhub.domain.Service>>> serviceOps,
                List<Function<Integer, List<Event>>> eventOps,
                List<Function<Integer, List<User>>> userOps,
                List<Function<Integer, List<Tag>>> tagOps
        ) {
            this.serviceOps = serviceOps;
            this.eventOps = eventOps;
            this.userOps = userOps;
            this.tagOps = tagOps;
        }
    }

    private SearchOperations prepareSearchOperations(String stringToMatch) {
        return new SearchOperations(
                List.of(
                        i -> serviceRepository.findByLocationLikeIgnoreCase(stringToMatch, Pageable.ofSize(i)),
                        i -> serviceRepository.findByDescriptionLikeIgnoreCase(stringToMatch, Pageable.ofSize(i)),
                        i -> serviceRepository.findByHeaderLikeIgnoreCase(stringToMatch, Pageable.ofSize(i))
                ),
                List.of(
                        i -> eventRepository.findByLocationLikeIgnoreCase(stringToMatch, Pageable.ofSize(i)),
                        i -> eventRepository.findByDescriptionLikeIgnoreCase(stringToMatch, Pageable.ofSize(i)),
                        i -> eventRepository.findByHeaderLikeIgnoreCase(stringToMatch, Pageable.ofSize(i))
                ),
                List.of(
                        i -> userRepository.findByUsernameLikeIgnoreCase(stringToMatch, Pageable.ofSize(i)),
                        i -> userRepository.findByBioLikeIgnoreCase(stringToMatch, Pageable.ofSize(i))
                ),
                List.of(
                        i -> tagRepository.findByNameLikeIgnoreCase(stringToMatch, Pageable.ofSize(i))
                )
        );
    }

    private SearchMatchDto mapToDto(Event event) {
        return new SearchMatchDto(event.getHeader(), "/event/" + event.getId(), SearchMatchType.EVENT, 0);
    }

    private SearchMatchDto mapToDto(com.swe573.socialhub.domain.Service service, double score) {
        return new SearchMatchDto(service.getHeader(), "/service/" + service.getId(), SearchMatchType.SERVICE, score);
    }

    private SearchMatchDto mapToDto(com.swe573.socialhub.domain.User user, double score) {
        return new SearchMatchDto(user.getUsername(), "/user/" + user.getId(), SearchMatchType.USER, score);
    }

    private SearchMatchDto mapToDto(com.swe573.socialhub.domain.Tag tag) {
        return new SearchMatchDto(tag.getName(), "/tags/" + tag.getId(), SearchMatchType.TAG, 0);
    }
}
