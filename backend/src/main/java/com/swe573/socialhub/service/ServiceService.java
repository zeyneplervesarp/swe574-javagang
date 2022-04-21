package com.swe573.socialhub.service;

import com.swe573.socialhub.domain.*;
import com.swe573.socialhub.dto.ServiceDto;
import com.swe573.socialhub.dto.TagDto;
import com.swe573.socialhub.enums.*;
import com.swe573.socialhub.repository.*;
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
import java.util.stream.Stream;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private FlagRepository flagRepository;

    @Autowired
    private UserServiceApprovalRepository approvalRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private RatingService ratingService;

    public List<ServiceDto> findAllServices() {
        var entities = serviceRepository.findAll();
        entities = entities.stream().filter(x -> x.getTime().isAfter(LocalDateTime.now())).limit(3).collect(Collectors.toUnmodifiableList());

        var list = entities.stream().map(service -> mapToDto(service, Optional.empty())).collect(Collectors.toUnmodifiableList());


        return list;
    }

    public List<ServiceDto> findAllServices(Principal principal, Boolean getOngoingOnly, ServiceFilter filter, ServiceSortBy sortBy) {
        var entities = serviceRepository.findAll();
        //get logged in user
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();

        final var filteredEntityStream = filterStream(
                entities.stream(),
                sortBy,
                getOngoingOnly || filter == ServiceFilter.all,
                filter,
                loggedInUser
        );

        final var dtoStream = filteredEntityStream
                .map(service -> mapToDto(service, Optional.of(loggedInUser)));

        final var sortedDtoStream = sortStream(dtoStream, sortBy);

        return sortedDtoStream.collect(Collectors.toUnmodifiableList());
    }

    private Stream<Service> filterStream(Stream<Service> stream, ServiceSortBy sortBy, Boolean getOngoingOnly, ServiceFilter filter, User loggedInUser) {
        var dtoStream = stream;
        //filter if getongoingonly
        if (getOngoingOnly) {
            dtoStream = dtoStream.filter(x -> x.getTime().isAfter(LocalDateTime.now()));
        }

        final var pickedDistanceFilter = sortBy != null && (sortBy.equals(ServiceSortBy.distanceAsc) || sortBy.equals(ServiceSortBy.distanceDesc));
        if (pickedDistanceFilter) {
            dtoStream = dtoStream.filter(service -> service.getLocationType().equals(LocationType.Physical));
        }
        if (filter == null) return dtoStream;

        //filter by filter
        switch (filter) {
            case createdByUser:
                return dtoStream.filter(x -> x.getCreatedUser() == loggedInUser);
            case first3:
                return dtoStream.limit(3);
            case attending:
                return dtoStream.filter(x -> x.getApprovalSet().stream().anyMatch(y -> y.getUser() == loggedInUser && y.getApprovalStatus() == ApprovalStatus.APPROVED));
            case followingUser:
                var followingUsers = loggedInUser.getFollowingUsers();
                var followedUserIds = followingUsers.stream()
                        .map(UserFollowing::getFollowedUser)
                        .map(User::getId)
                        .collect(Collectors.toUnmodifiableSet());

                return dtoStream.filter(service -> followedUserIds.contains(service.getCreatedUser().getId()));
            case all:
                return dtoStream;
        }
        return dtoStream;
    }

    private Stream<ServiceDto> sortStream(Stream<ServiceDto> stream, ServiceSortBy sortBy) {
        if (sortBy == null) return stream;
        {
            switch (sortBy) {
                case distanceAsc:
                    return stream
                            .sorted(Comparator.comparing(ServiceDto::getDistanceToUser));
                case distanceDesc:
                    return stream
                            .sorted(Comparator.comparing(ServiceDto::getDistanceToUser).reversed());
                case createdDateAsc:
                    return stream
                            .sorted(Comparator.comparing(ServiceDto::getId));
                case createdDateDesc:
                    return stream
                            .sorted(Comparator.comparing(ServiceDto::getId).reversed());
                case serviceDateAsc:
                    return stream
                            .sorted(Comparator.comparing(ServiceDto::getTime));
                case serviceDateDesc:
                    return stream
                            .sorted(Comparator.comparing(ServiceDto::getTime).reversed());
            }
        }
        return stream;
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
    public Long upsert(Principal principal, ServiceDto dto) {
        //check token => if username is null, throw an error
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");

        try {
            var entityExists = false;
            if (dto.getId() != null)
                entityExists = serviceRepository.findById(dto.getId()).isPresent();
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


            if (entityExists)
            {
                entity.setId(dto.getId());
            }
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

    @Transactional
    public ServiceDto deleteService(Long serviceId, Principal principal) {
        final var loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (!loggedInUser.getUserType().equals(UserType.ADMIN)) {
            throw new IllegalArgumentException("You need to be admin to perform this action.");
        }
        final var serviceToDelete = serviceRepository.findById(serviceId);
        if (serviceToDelete.isEmpty()) {
            throw new IllegalArgumentException("Service does not exist.");
        }
        final var service = serviceToDelete.get();
        final var dto = mapToDto(service, Optional.of(loggedInUser));
        serviceRepository.delete(service);
        return dto;
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

        if (loggedInUser.isPresent() && service.getLocationType().equals(LocationType.Physical)) {

            distanceToUser = getDistance(service.getLatitude(), service.getLongitude(), loggedInUser.get().getLatitude(), loggedInUser.get().getLongitude());

        } else {
            distanceToUser = null;
        }
        var attending = approvals.stream().filter(x -> x.getApprovalStatus() == ApprovalStatus.APPROVED).count();
        var pending = approvals.stream().filter(x -> x.getApprovalStatus() == ApprovalStatus.PENDING).count();
        long flagCount = flagRepository.countByTypeAndFlaggedEntityAndStatus(FlagType.service, service.getId(), FlagStatus.active);
        return new ServiceDto(service.getId(), service.getHeader(), service.getDescription(), service.getLocationType(), service.getLocation(), service.getTime(), service.getCredit(), service.getQuota(), attending, service.getCreatedUser().getId(), service.getCreatedUser().getUsername(), service.getLatitude(), service.getLongitude(), list, service.getStatus(), pending, distanceToUser, attendingUserList, ratingService.getServiceRatingSummary(service), flagCount, service.getImageUrl());
    }


    private Service mapToEntity(ServiceDto dto) {
        if(dto.getLocationType().equals(LocationType.Online))
            return Service.createOnline(null, dto.getHeader(), dto.getDescription(), dto.getLocation(), dto.getTime(), dto.getMinutes(), dto.getQuota(), 0, null, null, dto.getImageUrl());
        else
            return Service.createPhysical(null, dto.getHeader(), dto.getDescription(), dto.getLocation(), dto.getTime(), dto.getMinutes(), dto.getQuota(), 0, null, dto.getLatitude(), dto.getLongitude(), null, dto.getImageUrl());
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

    public Flag flagService(Principal principal, Long serviceId) {
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        Service serviceToFlag = serviceRepository.getById(serviceId);
        // check for existing flag for duplicate
        Optional<Flag> existingFlag = flagRepository.findFlagByFlaggingUserAndFlaggedEntityAndType(loggedInUser.getId(), serviceId, FlagType.service);
        if (existingFlag.isPresent()) {
            throw new IllegalArgumentException("You have already flagged service " + serviceToFlag.getHeader());
        }
        // flag the service
        try {
            Flag flag = new Flag(FlagType.service, loggedInUser.getId(), serviceId, FlagStatus.active);
            return flagRepository.save(flag);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Transactional
    public void dismissFlags(Principal principal, Long serviceId) {
        try {
            final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
            if (!loggedInUser.getUserType().equals(UserType.ADMIN)) {
                throw new IllegalArgumentException("You need to be admin to perform this action");
            }
            flagRepository.dismissFlags(FlagStatus.inactive, FlagType.service, serviceId);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Boolean checkForExistingFlag(Principal principal, Long serviceId) {
        try {
            final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
            Optional<Flag> existingFlag = flagRepository.findFlagByFlaggingUserAndFlaggedEntityAndType(loggedInUser.getId(), serviceId, FlagType.service);
            return existingFlag.isPresent();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private User validateAdmin(Principal principal) {
        final var loggedInUser = userRepository.findUserByUsername(principal.getName());
        if (loggedInUser.isEmpty())
            throw new IllegalArgumentException("User doesn't exist.");
        if (loggedInUser.get().getUserType() != UserType.ADMIN)
            throw new IllegalArgumentException("You need to be admin to perform this action.");
        return loggedInUser.get();
    }

    private static int MAX_FEATURED_SERVICE_COUNT = 3;

    public ServiceDto featureService(Long serviceId, Principal principal) {
        final var admin = validateAdmin(principal);

        final var previouslyFeatured = serviceRepository.findFeatured();
        if (previouslyFeatured.size() >= MAX_FEATURED_SERVICE_COUNT)
            throw new IllegalArgumentException("Already have " + MAX_FEATURED_SERVICE_COUNT + " services featured. Unfeature one before proceeding.");


        final var svc = serviceRepository.findById(serviceId);
        if (svc.isEmpty())
            throw new IllegalArgumentException("Service doesn't exist.");

        svc.get().setFeatured(true);

        return mapToDto(svc.get(), Optional.of(admin));
    }

    public List<ServiceDto> getAllFeaturedServices(Principal principal) {
        final Optional<User> loggedInUser;
        if (principal == null)
            loggedInUser = Optional.empty();
        else
            loggedInUser = userRepository.findUserByUsername(principal.getName());
        return serviceRepository
                .findFeatured()
                .stream()
                .map(s -> mapToDto(s, loggedInUser))
                .collect(Collectors.toUnmodifiableList());
    }

    public ServiceDto removeFromFeaturedServices(Long serviceId, Principal principal) {
        final var admin = validateAdmin(principal);

        final var svc = serviceRepository.findById(serviceId);
        if (svc.isEmpty())
            throw new IllegalArgumentException("Service doesn't exist.");

        svc.get().setFeatured(false);

        return mapToDto(svc.get(), Optional.of(admin));
    }
}
