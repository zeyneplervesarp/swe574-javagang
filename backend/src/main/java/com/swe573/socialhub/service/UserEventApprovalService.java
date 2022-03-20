package com.swe573.socialhub.service;

import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.domain.UserEventApproval;
import com.swe573.socialhub.domain.key.UserEventApprovalKey;
import com.swe573.socialhub.dto.EventDto;
import com.swe573.socialhub.dto.SimpleEventApprovalDto;
import com.swe573.socialhub.dto.UserEventApprovalDto;
import com.swe573.socialhub.enums.ApprovalStatus;
import com.swe573.socialhub.repository.EventRepository;
import com.swe573.socialhub.repository.UserEventApprovalRepository;
import com.swe573.socialhub.repository.UserRepository;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserEventApprovalService {

    @Autowired
    UserEventApprovalRepository repository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserService userService;

    @Autowired
    NotificationService notificationService;


    @Transactional
    public UserEventApprovalDto RequestApproval(Principal principal, Long eventId) {
        //check token => if username is null, throw an error
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");

        //validate event
        var event = eventRepository.findById(eventId).get();
        if (event == null)
            throw new IllegalArgumentException("Event doesn't exist.");

        //create new entity
        var key = new UserEventApprovalKey(loggedInUser.getId(), eventId);
        var entity = new UserEventApproval(key, loggedInUser, event, ApprovalStatus.PENDING);
        
        try {
            final var approval = repository.save(entity);
            var dto = getApprovalDto(approval);
            notificationService.sendNotification("Hooray! There is a new request for " + event.getHeader() + " by " + loggedInUser.getUsername(),
                    "/event/" + entity.getId(), event.getCreatedUser());
            return dto;
        } catch (Exception e) {
            throw new IllegalArgumentException("There was an error trying to send the request");
        }

    }

    public List<UserEventApprovalDto> findEventRequestsByUser(Principal principal) {
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");
        var eventList = repository.findUserEventApprovalByEvent_CreatedUserAndApprovalStatus(loggedInUser, ApprovalStatus.PENDING);
        var returnList = new ArrayList<UserEventApprovalDto>();
        for (var entity : eventList) {
            var dto = getApprovalDto(entity);
            returnList.add(dto);
        }
        return returnList;
    }

    private UserEventApprovalDto getApprovalDto(UserEventApproval entity) {
        var event = entity.getEvent();
        var userDto = userService.mapUserToDTO(entity.getUser());
        var eventDto = new EventDto(event.getId(), event.getHeader(), "", event.getLocation(), event.getTime(), 0, event.getQuota(), event.getAttendingUserCount(), 0L, "", 0.0, 0.0, Collections.emptyList(), event.getStatus(), 0L, null, null);
        var dto = new UserEventApprovalDto(userDto, eventDto, entity.getApprovalStatus());
        return dto;
    }


    public void updateRequestStatus(SimpleEventApprovalDto dto, ApprovalStatus status) {
        var request = repository.findUserEventApprovalByEvent_IdAndUser_Id(dto.getEventId(), dto.getUserId());
        if (!request.isPresent()) {
            throw new IllegalArgumentException("No approval request has been found");
        }
        var entity = request.get();
        entity.setApprovalStatus(status);
        var event = request.get().getEvent();
        var current = event.getAttendingUserCount();
        event.setAttendingUserCount(current + 1);

        try {
            repository.save(entity);
            notificationService.sendNotification("Your request for event " + event.getHeader() + " has been " + status.name().toLowerCase(), "/event/" + entity.getId(), entity.getUser());
        } catch (DataException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
