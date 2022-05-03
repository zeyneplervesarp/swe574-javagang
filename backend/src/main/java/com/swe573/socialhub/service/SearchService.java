package com.swe573.socialhub.service;

import com.swe573.socialhub.domain.Event;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.dto.SearchMatchDto;
import com.swe573.socialhub.enums.SearchMatchType;
import com.swe573.socialhub.repository.EventRepository;
import com.swe573.socialhub.repository.ServiceRepository;
import com.swe573.socialhub.repository.TagRepository;
import com.swe573.socialhub.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public List<SearchMatchDto> search(String stringToMatch, int limit) {
        validate(limit);

        if (stringToMatch.isEmpty() || stringToMatch.isBlank()) {
            return Collections.emptyList();
        }

        final var searchOperations = prepareSearchOperations(stringToMatch);
        final var searchResults = new LinkedHashSet<SearchMatchDto>();

        for (int i = 0; i < searchOperations.size() && searchResults.size() < limit; i++) {
            final var opResult = searchOperations.get(i).apply(limit - searchResults.size());
            searchResults.addAll(opResult);
        }

        return searchResults.size() > limit ? new ArrayList<>(searchResults).subList(0, limit) : new ArrayList<>(searchResults);
    }

    private void validate(int limit) {
        if (limit > SEARCH_LIMIT) {
            throw new IllegalArgumentException("Maximum search limit is " + SEARCH_LIMIT + ".");
        }

        if (limit < 1) {
            throw new IllegalArgumentException("Minimum search limit is 1.");
        }
    }

    private List<Function<Integer, List<SearchMatchDto>>> prepareSearchOperations(String stringToMatch) {
        Function<Integer, List<SearchMatchDto>> searchByServiceLocation = i -> mapServiceListToDtos(serviceRepository
                .findByLocationLikeIgnoreCase(stringToMatch, Pageable.ofSize(i)));

        Function<Integer, List<SearchMatchDto>> searchByServiceDescription = i -> mapServiceListToDtos(serviceRepository
                .findByDescriptionLikeIgnoreCase(stringToMatch, Pageable.ofSize(i)));

        Function<Integer, List<SearchMatchDto>> searchByServiceHeader = i -> mapServiceListToDtos(serviceRepository
                .findByHeaderLikeIgnoreCase(stringToMatch, Pageable.ofSize(i)));

        Function<Integer, List<SearchMatchDto>> searchByEventLocation = i -> mapEventListToDtos(eventRepository
                .findByLocationLikeIgnoreCase(stringToMatch, Pageable.ofSize(i)));

        Function<Integer, List<SearchMatchDto>> searchByEventDescription = i -> mapEventListToDtos(eventRepository
                .findByDescriptionLikeIgnoreCase(stringToMatch, Pageable.ofSize(i)));

        Function<Integer, List<SearchMatchDto>> searchByEventHeader = i -> mapEventListToDtos(eventRepository
                .findByHeaderLikeIgnoreCase(stringToMatch, Pageable.ofSize(i)));

        Function<Integer, List<SearchMatchDto>> searchByUsername = i -> mapUserListToDtos(userRepository
                .findByUsernameLikeIgnoreCase(stringToMatch, Pageable.ofSize(i)));

        Function<Integer, List<SearchMatchDto>> searchByBio = i -> mapUserListToDtos(userRepository
                .findByBioLikeIgnoreCase(stringToMatch, Pageable.ofSize(i)));

        Function<Integer, List<SearchMatchDto>> searchByTagName = i -> tagRepository
                .findByNameLikeIgnoreCase(stringToMatch, Pageable.ofSize(i))
                .stream().map(this::mapToDto).collect(Collectors.toUnmodifiableList());

        return List.of(
                searchByServiceLocation,
                searchByEventLocation,
                searchByUsername,
                searchByTagName,
                searchByServiceHeader,
                searchByEventHeader,
                searchByServiceDescription,
                searchByEventDescription,
                searchByBio
        );
    }

    private List<SearchMatchDto> mapServiceListToDtos(List<com.swe573.socialhub.domain.Service> services) {
        return services.stream().map(this::mapToDto).collect(Collectors.toUnmodifiableList());
    }

    private List<SearchMatchDto> mapEventListToDtos(List<Event> events) {
        return events.stream().map(this::mapToDto).collect(Collectors.toUnmodifiableList());
    }

    private List<SearchMatchDto> mapUserListToDtos(List<User> users) {
        return users.stream().map(this::mapToDto).collect(Collectors.toUnmodifiableList());
    }

    private SearchMatchDto mapToDto(Event event) {
        return new SearchMatchDto(event.getHeader(), "/event/" + event.getId(), SearchMatchType.EVENT);
    }

    private SearchMatchDto mapToDto(com.swe573.socialhub.domain.Service service) {
        return new SearchMatchDto(service.getHeader(), "/service/" + service.getId(), SearchMatchType.SERVICE);
    }

    private SearchMatchDto mapToDto(com.swe573.socialhub.domain.User user) {
        return new SearchMatchDto(user.getUsername(), "/user/" + user.getId(), SearchMatchType.USER);
    }

    private SearchMatchDto mapToDto(com.swe573.socialhub.domain.Tag tag) {
        return new SearchMatchDto(tag.getName(), "/tags/" + tag.getId(), SearchMatchType.TAG);
    }
}
