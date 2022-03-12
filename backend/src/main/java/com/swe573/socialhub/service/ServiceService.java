package com.swe573.socialhub.service;

import com.swe573.socialhub.domain.*;
import com.swe573.socialhub.dto.ServiceDto;
import com.swe573.socialhub.dto.TagDto;
import com.swe573.socialhub.enums.ApprovalStatus;
import com.swe573.socialhub.enums.ServiceFilter;
import com.swe573.socialhub.enums.ServiceSortBy;
import com.swe573.socialhub.enums.ServiceStatus;
import com.swe573.socialhub.repository.ServiceRepository;
import com.swe573.socialhub.repository.TagRepository;
import com.swe573.socialhub.repository.UserRepository;
import com.swe573.socialhub.repository.UserServiceApprovalRepository;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserServiceApprovalRepository approvalRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    public List<ServiceDto> findAllServices() {
        var entities = serviceRepository.findAll();
        entities = entities.stream().filter(x -> x.getTime().isAfter(LocalDateTime.now())).limit(3).collect(Collectors.toUnmodifiableList());

        var list = entities.stream().map(service -> mapToDto(service, Optional.empty())).collect(Collectors.toUnmodifiableList());


        return list;
    }

    public List<ServiceDto> findAllServices(Principal principal, Boolean getOngoingOnly, ServiceFilter filter, ServiceSortBy sortBy) {
        //set getongoinonly and get all service entities
        if (filter == ServiceFilter.all) getOngoingOnly = true;
        var entities = serviceRepository.findAll();
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
                var tempList = new ArrayList<Service>();

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
        var list = entities.stream().map(service -> mapToDto(service, Optional.of(loggedInUser))).collect(Collectors.toUnmodifiableList());


        if (sortBy != null)
        //if sortBy is not null, sort
        {
            switch (sortBy) {
                case distanceAsc:
                    list = list.stream()
                            .sorted(Comparator.comparing(ServiceDto::getDistanceToUser))
                            .collect(Collectors.toList());
                    break;
                case distanceDesc:
                    list = list.stream()
                            .sorted(Comparator.comparing(ServiceDto::getDistanceToUser).reversed())
                            .collect(Collectors.toList());
                    break;
                case createdDateAsc:
                    list = list.stream()
                            .sorted(Comparator.comparing(ServiceDto::getId))
                            .collect(Collectors.toList());
                    break;
                case createdDateDesc:
                    list = list.stream()
                            .sorted(Comparator.comparing(ServiceDto::getId).reversed())
                            .collect(Collectors.toList());
                    break;
                case serviceDateAsc:
                    list = list.stream()
                            .sorted(Comparator.comparing(ServiceDto::getTime))
                            .collect(Collectors.toList());
                    break;
                case serviceDateDesc:
                    list = list.stream()
                            .sorted(Comparator.comparing(ServiceDto::getTime).reversed())
                            .collect(Collectors.toList());
                    break;
            }
        }
        return list;
    }

    public Optional<ServiceDto> findById(Long id) {

        Optional<Service> service = serviceRepository.findById(id);

        if (service.isPresent()) {
            var dto = mapToDto(service.get(), Optional.empty());
            return Optional.ofNullable(dto);
        } else {
            throw new IllegalArgumentException("No services have been found");
        }
    }

    @Transactional
    public Long save(Principal principal, ServiceDto dto) {
        //check token => if username is null, throw an error
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");

        try {
            var entity = mapToEntity(dto);
            entity.setCreatedUser(loggedInUser);

            var tags = dto.getServiceTags();
            if (tags != null) {
                for (TagDto tagDto : tags) {
                    var addedTag = tagRepository.findById(tagDto.getId());
                    if (addedTag.isEmpty()) {
                        throw new IllegalArgumentException("There is no tag with this Id.");
                    }
                    entity.addTag(addedTag.get());
                }
            }
            //check pending credits and balance if the sum is above 20 => throw an error
            var currentUserBalance = userService.getBalanceToBe(loggedInUser);
            var balanceToBe = currentUserBalance + dto.getMinutes();
            if (balanceToBe >= 20)
                throw new IllegalArgumentException("You have reached the maximum limit of credits. You cannot create a service before spending your credits.");


            var savedEntity = serviceRepository.save(entity);
            return savedEntity.getId();
        } catch (DataException e) {
            throw new IllegalArgumentException("There was a problem trying to save service to db");
        }


    }


    public List<ServiceDto> findByUser(Principal principal) {
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");
        try {
            var entities = serviceRepository.findServiceByCreatedUser(loggedInUser);
            var dtoList = entities.stream().map(service -> mapToDto(service, Optional.empty())).collect(Collectors.toUnmodifiableList());
            return dtoList;
        } catch (DataException e) {
            throw new IllegalArgumentException("There was a problem trying to save service to db");
        }

    }


    @Transactional
    public void complete(Principal principal, Long serviceId) {
        //get logged in user
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");
        //get service by id
        Optional<Service> service = serviceRepository.findById(serviceId);

        //service null check
        if (service.isPresent()) {
            //set to approve
            var entity = service.get();
            entity.setStatus(ServiceStatus.COMPLETED);


            //send notification to attendees
            var approvedUserRequests = approvalRepository.findUserServiceApprovalByService_IdAndApprovalStatus(serviceId, ApprovalStatus.APPROVED);
            for (UserServiceApproval approvedUserRequest : approvedUserRequests) {
                var balance = approvedUserRequest.getUser().getBalance();
                approvedUserRequest.getUser().setBalance(balance - approvedUserRequest.getService().getCredit());
                notificationService.sendNotification(String.format("The service " + entity.getHeader() + " is complete. Your balance has been updated"),
                        "/service/" + entity.getId(), approvedUserRequest.getUser());
            }

            //send notification to creator
            var creator = service.get().getCreatedUser();
            notificationService.sendNotification(String.format("The service " + entity.getHeader() + " is complete. Your balance has been updated"),
                    "/service/" + entity.getId(), creator);

            //set balance
            var createdUser = service.get().getCreatedUser();
            var createdUserBalance = createdUser.getBalance();
            createdUser.setBalance(createdUserBalance + service.get().getCredit());


        } else {
            throw new IllegalArgumentException("No services have been found");
        }

    }

    @Transactional
    public void approve(Principal principal, Long serviceId) {
        //get logged in user
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");
        //get service by id
        Optional<Service> service = serviceRepository.findById(serviceId);

        //service null check
        if (service.isPresent()) {
            //set to approve
            var entity = service.get();
            entity.setStatus(ServiceStatus.APPROVED);

            //get all attendees
            var pendingUserRequests = approvalRepository.findUserServiceApprovalByService_IdAndApprovalStatus(serviceId, ApprovalStatus.PENDING);
            //if there are pending requests set them as denied and send notification
            for (UserServiceApproval pendingUserRequest : pendingUserRequests) {
                pendingUserRequest.setApprovalStatus(ApprovalStatus.DENIED);
                notificationService.sendNotification("Your request for service " + entity.getHeader() + " has been denied.",
                        "/service/" + entity.getId(), pendingUserRequest.getUser());
            }
            //send notification to attendees
            var approvedUserRequests = approvalRepository.findUserServiceApprovalByService_IdAndApprovalStatus(serviceId, ApprovalStatus.APPROVED);
            for (UserServiceApproval approvedUserRequest : approvedUserRequests) {
                notificationService.sendNotification(String.format("It looks like " + entity.getHeader() + " has been over. Please confirm it to complete this service."),
                        "/service/" + entity.getId(), approvedUserRequest.getUser());
            }


        } else {
            throw new IllegalArgumentException("No services have been found");
        }

    }

    private ServiceDto mapToDto(Service service, Optional<User> loggedInUser) {
        var list = new ArrayList<TagDto>();
        if (service.getServiceTags() != null) {
            for (Tag tag : service.getServiceTags()) {
                var dto = new TagDto(tag.getId(), tag.getName());
                list.add(dto);
            }
        }
        var approvals = service.getApprovalSet();
        var attendingUserList =  approvals.stream().filter(x -> x.getApprovalStatus() == ApprovalStatus.APPROVED).map(users -> userService.mapUserToDTO(users.getUser())).collect(Collectors.toList());

        Double distanceToUser;

        if (loggedInUser.isPresent()) {

            distanceToUser = getDistance(service.getLatitude(), service.getLongitude(), loggedInUser.get().getLatitude(), loggedInUser.get().getLongitude());

        } else {
            distanceToUser = null;
        }
        var attending = approvals.stream().filter(x -> x.getApprovalStatus() == ApprovalStatus.APPROVED).count();
        var pending = approvals.stream().filter(x -> x.getApprovalStatus() == ApprovalStatus.PENDING).count();

        return new ServiceDto(service.getId(), service.getHeader(), service.getDescription(), service.getLocation(), service.getTime(), service.getCredit(), service.getQuota(), attending, service.getCreatedUser().getId(), service.getCreatedUser().getUsername(), service.getLatitude(), service.getLongitude(), list, service.getStatus(), pending, distanceToUser, attendingUserList);
    }


    private Service mapToEntity(ServiceDto dto) {
        return new Service(null, dto.getHeader(), dto.getDescription(), dto.getLocation(), dto.getTime(), dto.getMinutes(), dto.getQuota(), 0, null, dto.getLatitude(), dto.getLongitude(), null);
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


}
