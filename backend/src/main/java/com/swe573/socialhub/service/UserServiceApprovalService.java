package com.swe573.socialhub.service;

import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.domain.UserServiceApproval;
import com.swe573.socialhub.domain.key.UserServiceApprovalKey;
import com.swe573.socialhub.dto.ServiceDto;
import com.swe573.socialhub.dto.SimpleApprovalDto;
import com.swe573.socialhub.dto.UserServiceApprovalDto;
import com.swe573.socialhub.enums.ApprovalStatus;
import com.swe573.socialhub.repository.ServiceRepository;
import com.swe573.socialhub.repository.UserRepository;
import com.swe573.socialhub.repository.UserServiceApprovalRepository;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
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
    UserService userService;

    @Autowired
    NotificationService notificationService;


    @Transactional
    public UserServiceApprovalDto RequestApproval(Principal principal, Long serviceId) {
        //check token => if username is null, throw an error
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");

        //validate service
        var service = serviceRepository.findById(serviceId).get();
        if (service == null)
            throw new IllegalArgumentException("Service doesn't exist.");

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
                    "/service/" + entity.getId(), service.getCreatedUser());
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
        var userDto = userService.mapUserToDTO(entity.getUser());
        var serviceDto = new ServiceDto(service.getId(), service.getHeader(), "", service.getLocation(), service.getTime(), 0, service.getQuota(), service.getAttendingUserCount(), 0L, "", 0.0, 0.0, Collections.emptyList(), service.getStatus(), 0L, null, null);
        var dto = new UserServiceApprovalDto(userDto, serviceDto, entity.getApprovalStatus());
        return dto;
    }


    public void updateRequestStatus(SimpleApprovalDto dto, ApprovalStatus status) {
        var request = repository.findUserServiceApprovalByService_IdAndUser_Id(dto.getServiceId(), dto.getUserId());
        if (!request.isPresent()) {
            throw new IllegalArgumentException("No approval request has been found");
        }
        var entity = request.get();
        entity.setApprovalStatus(status);
        var service = request.get().getService();
        var current = service.getAttendingUserCount();
        service.setAttendingUserCount(current + 1);

        try {
            var returnData = repository.save(entity);
            notificationService.sendNotification("Your request for service " + service.getHeader() + " has been " + status.name().toLowerCase(), "/service/" + entity.getId(), entity.getUser());
        } catch (DataException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
