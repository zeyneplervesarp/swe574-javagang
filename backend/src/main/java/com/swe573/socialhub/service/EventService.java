package com.swe573.socialhub.service;

import com.swe573.socialhub.domain.*;
import com.swe573.socialhub.dto.EventDto;
import com.swe573.socialhub.dto.ServiceDto;
import com.swe573.socialhub.dto.TagDto;
import com.swe573.socialhub.enums.*;
import com.swe573.socialhub.repository.*;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private FlagRepository flagRepository;

    @Autowired
    private UserEventApprovalRepository approvalRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    public List<EventDto> findAllEvents() {
        var entities = eventRepository.findAll();
        entities = entities.stream().filter(x -> x.getTime().isAfter(LocalDateTime.now())).limit(3).collect(Collectors.toUnmodifiableList());

        var list = entities.stream().map(event -> mapToDto(event, Optional.empty())).collect(Collectors.toUnmodifiableList());


        return list;
    }

    @Transactional
    public EventDto deleteEvent(Long eventId, Principal principal) {
        final var loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (!loggedInUser.getUserType().equals(UserType.ADMIN)) {
            throw new IllegalArgumentException("You need to be admin to perform this action.");
        }
        final var eventToDelete = eventRepository.findById(eventId);
        if (eventToDelete.isEmpty()) {
            throw new IllegalArgumentException("Service does not exist.");
        }
        final var event = eventToDelete.get();
        final var dto = mapToDto(event, Optional.of(loggedInUser));
        eventRepository.delete(event);
        return dto;
    }

    public List<EventDto> findAllEvents(Principal principal, Boolean getOngoingOnly, ServiceFilter filter, ServiceSortBy sortBy) {
        //set getongoinonly and get all event entities
        if (filter == ServiceFilter.all) getOngoingOnly = true;
        var entities = eventRepository.findAll();
        //get logged in user
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();

        //filter if getongoingonly
        if (getOngoingOnly) {
            entities = entities.stream().filter(x -> x.getTime().isAfter(LocalDateTime.now())).collect(Collectors.toUnmodifiableList());
        }
        //filter by filter
        switch (filter) {
            case createdByUser:
                entities = entities.stream().filter(x -> x.getCreatedUser() == loggedInUser).collect(Collectors.toUnmodifiableList());
                break;
            case first3:
                entities = entities.stream().limit(3).collect(Collectors.toUnmodifiableList());
                break;
            case attending:
                entities = entities.stream().filter(x -> x.getApprovalSet().stream().anyMatch(y -> y.getUser() == loggedInUser && y.getApprovalStatus() == ApprovalStatus.APPROVED)).collect(Collectors.toUnmodifiableList());
                break;
            case followingUser:
                var followingUsers = loggedInUser.getFollowingUsers();
                var tempList = new ArrayList<Event>();

                for(UserFollowing followingUser: followingUsers)
                {
                    var user = followingUser.getFollowedUser();
                    tempList.addAll(entities.stream().filter(x -> x.getCreatedUser() == user).collect(Collectors.toUnmodifiableList()));

                }
                entities = tempList;
                break;
            default:
                // code block
        }


        //map to dto and return list
        var list = entities.stream().map(event -> mapToDto(event, Optional.of(loggedInUser))).collect(Collectors.toUnmodifiableList());


        if (sortBy != null)
        //if sortBy is not null, sort
        {
            switch (sortBy) {
                case distanceAsc:
                    list = list.stream()
                            .sorted(Comparator.comparing(EventDto::getDistanceToUser))
                            .collect(Collectors.toList());
                    break;
                case distanceDesc:
                    list = list.stream()
                            .sorted(Comparator.comparing(EventDto::getDistanceToUser).reversed())
                            .collect(Collectors.toList());
                    break;
                case createdDateAsc:
                    list = list.stream()
                            .sorted(Comparator.comparing(EventDto::getId))
                            .collect(Collectors.toList());
                    break;
                case createdDateDesc:
                    list = list.stream()
                            .sorted(Comparator.comparing(EventDto::getId).reversed())
                            .collect(Collectors.toList());
                    break;
                case serviceDateAsc:
                    list = list.stream()
                            .sorted(Comparator.comparing(EventDto::getTime))
                            .collect(Collectors.toList());
                    break;
                case serviceDateDesc:
                    list = list.stream()
                            .sorted(Comparator.comparing(EventDto::getTime).reversed())
                            .collect(Collectors.toList());
                    break;
            }
        }
        return list;
    }

    public Optional<EventDto> findById(Long id) {

        Optional<Event> event = eventRepository.findById(id);

        if (event.isPresent()) {
            var dto = mapToDto(event.get(), Optional.empty());
            return Optional.ofNullable(dto);
        } else {
            throw new IllegalArgumentException("No events have been found");
        }
    }

    @Transactional
    public Long save(Principal principal, EventDto dto) {
        //check token => if username is null, throw an error
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");

        try {
            var entity = mapToEntity(dto);
            entity.setCreatedUser(loggedInUser);

            var tags = dto.getEventTags();
            if (tags != null) {
                for (TagDto tagDto : tags) {
                    var addedTag = tagRepository.findById(tagDto.getId());
                    if (addedTag.isEmpty()) {
                        throw new IllegalArgumentException("There is no tag with this Id.");
                    }
                    entity.addTag(addedTag.get());
                }
            }

            var savedEntity = eventRepository.save(entity);
            return savedEntity.getId();
        } catch (DataException e) {
            throw new IllegalArgumentException("There was a problem trying to save event to db");
        }
    }

    public List<EventDto> findByUser(Principal principal) {
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");
        try {
            var entities = eventRepository.findEventByCreatedUser(loggedInUser);
            var dtoList = entities.stream().map(event -> mapToDto(event, Optional.empty())).collect(Collectors.toUnmodifiableList());
            return dtoList;
        } catch (DataException e) {
            throw new IllegalArgumentException("There was a problem trying to save event to db");
        }

    }

    @Transactional
    public void complete(Principal principal, Long eventId) {
        //get logged in user
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");
        //get event by id
        var event = eventRepository.findById(eventId);

        //event null check
        if (event.isPresent()) {
            //set to approve
            var entity = event.get();
            entity.setStatus(ServiceStatus.COMPLETED);

        } else {
            throw new IllegalArgumentException("No events have been found");
        }

    }

    @Transactional
    public void approve(Principal principal, Long eventId) {
        //get logged in user
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");
        //get event by id
        var event = eventRepository.findById(eventId);

        //event null check
        if (event.isPresent()) {
            //set to approve
            var entity = event.get();
            entity.setStatus(ServiceStatus.APPROVED);

            //get all attendees
            var pendingUserRequests = approvalRepository.findUserEventApprovalByEvent_IdAndApprovalStatus(eventId, ApprovalStatus.PENDING);
            //if there are pending requests set them as denied and send notification
            for (var pendingUserRequest : pendingUserRequests) {
                pendingUserRequest.setApprovalStatus(ApprovalStatus.DENIED);
                notificationService.sendNotification("Your request for event " + entity.getHeader() + " has been denied.",
                        "/event/" + entity.getId(), pendingUserRequest.getUser());
            }
            //send notification to attendees
            var approvedUserRequests = approvalRepository.findUserEventApprovalByEvent_IdAndApprovalStatus(eventId, ApprovalStatus.APPROVED);
            for (var approvedUserRequest : approvedUserRequests) {
                notificationService.sendNotification(String.format("It looks like " + entity.getHeader() + " has been over. Please confirm it to complete this event."),
                        "/event/" + entity.getId(), approvedUserRequest.getUser());
            }


        } else {
            throw new IllegalArgumentException("No events have been found");
        }
    }

    private EventDto mapToDto(Event event, Optional<User> loggedInUser) {
        var list = new ArrayList<TagDto>();
        if (event.getEventTags() != null) {
            for (Tag tag : event.getEventTags()) {
                var dto = new TagDto(tag.getId(), tag.getName());
                list.add(dto);
            }
        }
        var approvals = event.getApprovalSet();
        var attendingUserList =  approvals.stream().filter(x -> x.getApprovalStatus() == ApprovalStatus.APPROVED).map(users -> userService.mapUserToDTO(users.getUser())).collect(Collectors.toList());

        Double distanceToUser;

        if (loggedInUser.isPresent()) {

            distanceToUser = getDistance(event.getLatitude(), event.getLongitude(), loggedInUser.get().getLatitude(), loggedInUser.get().getLongitude());

        } else {
            distanceToUser = null;
        }
        var attending = approvals.stream().filter(x -> x.getApprovalStatus() == ApprovalStatus.APPROVED).count();
        var pending = approvals.stream().filter(x -> x.getApprovalStatus() == ApprovalStatus.PENDING).count();
        long flagCount = flagRepository.countByTypeAndFlaggedEntityAndStatus(FlagType.event, event.getId(), FlagStatus.active);
        return new EventDto(event.getId(), event.getHeader(), event.getDescription(), event.getLocation(), event.getTime(), event.getMinutes(), event.getQuota(), attending, event.getCreatedUser().getId(), event.getCreatedUser().getUsername(), event.getLatitude(), event.getLongitude(), list, event.getStatus(), pending, distanceToUser, attendingUserList, flagCount);
    }

    private Event mapToEntity(EventDto dto) {
        return new Event(null, dto.getHeader(), dto.getDescription(), dto.getLocation(), dto.getTime(), dto.getMinutes(), dto.getQuota(), 0, null, dto.getLatitude(), dto.getLongitude(), null);
    }

    private double getDistance(double lat1, double lng1, String lat2, String lng2) {
        double lat2Double = Double.parseDouble(lat2);
        double lng2Double = Double.parseDouble(lng2);
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2Double - lat1);
        double dLng = Math.toRadians(lng2Double - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2Double)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        float dist = (float) (earthRadius * c);

        return dist * 0.001;
    }

    public Flag flagEvent(Principal principal, Long eventId) {
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        var eventToFlag = eventRepository.getById(eventId);
        // check for existing flag for duplicate
        Optional<Flag> existingFlag = flagRepository.findFlagByFlaggingUserAndFlaggedEntityAndType(loggedInUser.getId(), eventId, FlagType.event);
        if (existingFlag.isPresent()) {
            throw new IllegalArgumentException("You have already flagged event " + eventToFlag.getHeader());
        }
        // flag the event
        try {
            Flag flag = new Flag(FlagType.event, loggedInUser.getId(), eventId, FlagStatus.active);
            return flagRepository.save(flag);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Boolean checkForExistingFlag(Principal principal, Long eventId) {
        try {
            final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
            Optional<Flag> existingFlag = flagRepository.findFlagByFlaggingUserAndFlaggedEntityAndType(loggedInUser.getId(), eventId, FlagType.event);
            return existingFlag.isPresent();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
