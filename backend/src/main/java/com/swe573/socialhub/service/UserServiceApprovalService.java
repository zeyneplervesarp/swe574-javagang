package com.swe573.socialhub.service;

import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.domain.UserServiceApproval;
import com.swe573.socialhub.domain.key.UserServiceApprovalKey;
import com.swe573.socialhub.dto.ServiceDto;
import com.swe573.socialhub.dto.SimpleApprovalDto;
import com.swe573.socialhub.dto.UserServiceApprovalDto;
import com.swe573.socialhub.enums.ApprovalStatus;
import com.swe573.socialhub.enums.FlagStatus;
import com.swe573.socialhub.enums.FlagType;
import com.swe573.socialhub.enums.LocationType;
import com.swe573.socialhub.repository.FlagRepository;
import com.swe573.socialhub.repository.ServiceRepository;
import com.swe573.socialhub.repository.UserRepository;
import com.swe573.socialhub.repository.UserServiceApprovalRepository;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class UserServiceApprovalService {

    @Autowired
    UserServiceApprovalRepository repository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ServiceRepository serviceRepository;

    @Autowired
    FlagRepository flagRepository;

    @Autowired
    UserService userService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private BadgeService badgeService;


    @Transactional
    public UserServiceApprovalDto RequestApproval(Principal principal, Long serviceId) {
        //check token => if username is null, throw an error
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");

        //validate service
        var serviceQuery = serviceRepository.findById(serviceId);
        if (serviceQuery.isEmpty())
            throw new IllegalArgumentException("Service doesn't exist.");

        final var service = serviceQuery.get();
        // check for deadline
            if (service.getLocationType().equals(LocationType.Physical)) {
                if (LocalDateTime.now().isAfter(service.getTime().minusHours(24))) {
                    throw new IllegalArgumentException("You can only approve applications for physical services until 24 hours before their time");
                }
            } else {
                if (LocalDateTime.now().isAfter(service.getTime().minusMinutes(30))) {
                    throw new IllegalArgumentException("You can only approve applications for online services until 30 mimutes before their time");
                }
           }

        //create new entity
        var key = new UserServiceApprovalKey(loggedInUser.getId(), serviceId);
        var entity = new UserServiceApproval(key, loggedInUser, service, ApprovalStatus.PENDING);

        //check pending credits and balance if the sum is above 20 => throw an error
        var currentUserBalance = userService.getBalanceToBe(loggedInUser);
        var balanceToBe = currentUserBalance - service.getCredit();
        if (balanceToBe <= 0)
            throw new IllegalArgumentException("You have reached the minimum limit of credits. You cannot make a request to this service");

        try {
            final UserServiceApproval approval = repository.save(entity);
            var dto = getApprovalDto(approval);
            notificationService.sendNotification("Hooray! There is a new request for " + service.getHeader() + " by " + loggedInUser.getUsername(),
                    "/service/" + service.getId(), service.getCreatedUser());
            return dto;
        } catch (Exception e) {
            throw new IllegalArgumentException("There was an error trying to send the request");
        }
    }

    public List<UserServiceApprovalDto> findServiceRequestsByUser(Principal principal) {
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");
        var serviceList = repository.findUserServiceApprovalByService_CreatedUserAndApprovalStatus(loggedInUser, ApprovalStatus.PENDING);
        var returnList = new ArrayList<UserServiceApprovalDto>();
        for (UserServiceApproval entity : serviceList) {
            UserServiceApprovalDto dto = getApprovalDto(entity);
            returnList.add(dto);
        }
        return returnList;
    }

    private UserServiceApprovalDto getApprovalDto(UserServiceApproval entity) {
        var service = entity.getService();
            var userDto = userService.mapUserToDTO(entity.getUser(), false);
        var serviceDto = new ServiceDto(service.getId(), service.getHeader(), "", service.getLocationType(), service.getLocation(), service.getTime(), 0, service.getQuota(), service.getAttendingUserCount(), 0L, "", 0.0, 0.0, Collections.emptyList(), service.getStatus(), 0L, null, null, ratingService.getServiceRatingSummary(service), flagRepository.countByTypeAndFlaggedEntityAndStatus(FlagType.service, service.getId(), FlagStatus.active), service.getImageUrl(),service.isFeatured(), entity.getService().getCreated());
        var dto = new UserServiceApprovalDto(userDto, serviceDto, entity.getApprovalStatus());
        return dto;
    }

    public void updateRequestStatus(SimpleApprovalDto dto, ApprovalStatus status) {
        var request = repository.findUserServiceApprovalByService_IdAndUser_Id(dto.getServiceId(), dto.getUserId());
        if (!request.isPresent()) {
            throw new IllegalArgumentException("No approval request has been found");
        }

        // check for deadline
        if (request.get().getService().getLocationType().equals(LocationType.Physical)) {
            if (LocalDateTime.now().isAfter(request.get().getService().getTime().minusHours(24))) {
                throw new IllegalArgumentException("You can only approve applications for physical services until 24 hours before their time");
            }
        } else {
            if (LocalDateTime.now().isAfter(request.get().getService().getTime().minusMinutes(30))) {
                throw new IllegalArgumentException("You can only approve applications for online services until 30 mimutes before their time");
            }
        }
        var entity = request.get();
        entity.setApprovalStatus(status);
        var service = request.get().getService();
        var current = service.getAttendingUserCount();
        service.setAttendingUserCount(current + 1);


        try {
            var returnData = repository.save(entity);
            if (status == ApprovalStatus.APPROVED)
            {
                var updatedUser = badgeService.checkBadges(returnData.getUser());
                userRepository.save(updatedUser);
            }

            notificationService.sendNotification("Your request for service " + service.getHeader() + " has been " + status.name().toLowerCase(), "/service/" + entity.getId(), entity.getUser());
        } catch (DataException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
