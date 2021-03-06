package com.swe573.socialhub.service;

import com.swe573.socialhub.domain.*;
import com.swe573.socialhub.dto.*;
import com.swe573.socialhub.enums.*;
import com.swe573.socialhub.repository.*;
import org.hibernate.exception.DataException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
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
    private FlagRepository flagRepository;

    @Autowired
    private UserServiceApprovalRepository approvalRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private RatingService ratingService;

    public List<ServiceDto> findPaginatedOngoing(TimestampBasedPagination pagination) {
        return serviceRepository
                .findAllByDateBetweenOngoing(pagination.getGreaterThan(), pagination.getLowerThan(), pagination.toPageable())
                .stream()
                .map(s -> mapToDto(s, Optional.empty()))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<ServiceDto> findPaginatedOngoing(
            Principal principal,
            Boolean getOngoingOnly,
            ServiceFilter filter,
            Pagination pagination,
            ServiceSortBy sortBy,
            String lat,
            String lon
    ) {
        User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if((lat != null) && (lon!=null) ) {
            loggedInUser.setLatitude(lat);
            loggedInUser.setLongitude(lon);
        }
        final var entityList = new ArrayList<Service>();

        String sortByField = "created";
        switch (sortBy) {
            case serviceDateDesc:
            case serviceDateAsc:
                sortByField = "time";
                break;
            default:
                break;
        }

        switch (filter) {
            case createdByUser:
                if (pagination instanceof TimestampBasedPagination) {
                    final var tsPagination = (TimestampBasedPagination) pagination;
                    entityList.addAll(
                            getOngoingOnly ? serviceRepository
                                    .findAllByDateBetweenCreatedByUserOngoing(
                                            tsPagination.getGreaterThan(),
                                            tsPagination.getLowerThan(),
                                            tsPagination.toPageable(sortByField),
                                            loggedInUser.getId()
                                    ) :
                            serviceRepository
                                    .findAllByDateBetweenCreatedByUser(
                                            tsPagination.getGreaterThan(),
                                            tsPagination.getLowerThan(),
                                            tsPagination.toPageable(sortByField),
                                            loggedInUser.getId()
                                    )
                    );
                } else if (pagination instanceof DistanceBasedPagination) {
                    final var distPagination = (DistanceBasedPagination) pagination;
                    entityList.addAll(
                            getOngoingOnly ? serviceRepository
                                    .findAllByDistanceBetweenCreatedByUserOngoing(
                                            distPagination.getGreaterThan(),
                                            distPagination.getLowerThan(),
                                            Pageable.ofSize(distPagination.getSize()),
                                            loggedInUser.getId(),
                                            loggedInUser.getLatitude(),
                                            loggedInUser.getLongitude()
                                    )
                                    : serviceRepository
                                    .findAllByDistanceBetweenCreatedByUser(
                                            distPagination.getGreaterThan(),
                                            distPagination.getLowerThan(),
                                            Pageable.ofSize(distPagination.getSize()),
                                            loggedInUser.getId(),
                                            loggedInUser.getLatitude(),
                                            loggedInUser.getLongitude()
                                    )
                    );
                }
                break;
            case attending:
                if (pagination instanceof TimestampBasedPagination) {
                    final var tsPagination = (TimestampBasedPagination) pagination;
                    entityList.addAll(
                            getOngoingOnly ?
                            serviceRepository
                                    .findAllByDateBetweenAttendingByUserOngoing(
                                            tsPagination.getGreaterThan(),
                                            tsPagination.getLowerThan(),
                                            tsPagination.toPageable(sortByField),
                                            loggedInUser.getId()
                                    ) :
                            serviceRepository.findAllByDateBetweenAttendingByUser(
                                tsPagination.getGreaterThan(),
                                tsPagination.getLowerThan(),
                                tsPagination.toPageable(sortByField),
                                loggedInUser.getId()
                            )
                    );
                } else if (pagination instanceof DistanceBasedPagination) {
                    final var distPagination = (DistanceBasedPagination) pagination;
                    entityList.addAll(
                            getOngoingOnly ? serviceRepository
                                    .findAllByDistanceBetweenAttendingByUserOngoing(
                                            distPagination.getGreaterThan(),
                                            distPagination.getLowerThan(),
                                            Pageable.ofSize(distPagination.getSize()),
                                            loggedInUser.getId(),
                                            loggedInUser.getLatitude(),
                                            loggedInUser.getLongitude()
                                    ) : serviceRepository
                                    .findAllByDistanceBetweenAttendingByUser(
                                            distPagination.getGreaterThan(),
                                            distPagination.getLowerThan(),
                                            Pageable.ofSize(distPagination.getSize()),
                                            loggedInUser.getId(),
                                            loggedInUser.getLatitude(),
                                            loggedInUser.getLongitude()
                                    )
                    );
                }
                break;
            case followingUser:
                var followingUsers = loggedInUser.getFollowingUsers();
                var followedUserIds = followingUsers.stream()
                        .map(UserFollowing::getFollowedUser)
                        .map(User::getId)
                        .collect(Collectors.toUnmodifiableList());
                if (pagination instanceof TimestampBasedPagination) {
                    final var tsPagination = (TimestampBasedPagination) pagination;
                    entityList.addAll(
                            getOngoingOnly ?
                            serviceRepository
                                    .findAllByDateBetweenCreatedByUsersOngoing(
                                            tsPagination.getGreaterThan(),
                                            tsPagination.getLowerThan(),
                                            tsPagination.toPageable(sortByField),
                                            followedUserIds
                                    ) : serviceRepository
                                    .findAllByDateBetweenCreatedByUsers(
                                            tsPagination.getGreaterThan(),
                                            tsPagination.getLowerThan(),
                                            tsPagination.toPageable(sortByField),
                                            followedUserIds
                                    )
                    );
                } else if (pagination instanceof DistanceBasedPagination) {
                    final var distPagination = (DistanceBasedPagination) pagination;
                    entityList.addAll(
                            getOngoingOnly ?
                            serviceRepository
                                    .findAllByDistanceBetweenCreatedByUsersOngoing(
                                            distPagination.getGreaterThan(),
                                            distPagination.getLowerThan(),
                                            Pageable.ofSize(distPagination.getSize()),
                                            followedUserIds,
                                            loggedInUser.getLatitude(),
                                            loggedInUser.getLongitude()
                                    ) : serviceRepository
                                    .findAllByDistanceBetweenCreatedByUsers(
                                            distPagination.getGreaterThan(),
                                            distPagination.getLowerThan(),
                                            Pageable.ofSize(distPagination.getSize()),
                                            followedUserIds,
                                            loggedInUser.getLatitude(),
                                            loggedInUser.getLongitude()
                                    )
                    );
                }
                break;

            case all:
                if (pagination instanceof TimestampBasedPagination) {
                    final var tsPagination = (TimestampBasedPagination) pagination;
                    entityList.addAll(
                            getOngoingOnly ?
                                    serviceRepository
                                            .findAllByDateBetweenOngoing(
                                                    tsPagination.getGreaterThan(),
                                                    tsPagination.getLowerThan(),
                                                    tsPagination.toPageable(sortByField)
                                            ) : serviceRepository
                                    .findAllByDateBetween(
                                            tsPagination.getGreaterThan(),
                                            tsPagination.getLowerThan(),
                                            tsPagination.toPageable(sortByField)
                                    )
                    );
                } else if (pagination instanceof DistanceBasedPagination) {
                    final var distPagination = (DistanceBasedPagination) pagination;
                    entityList.addAll(
                            getOngoingOnly ?
                                    serviceRepository
                                            .findAllByDistanceBetweenOngoing(
                                                    distPagination.getGreaterThan(),
                                                    distPagination.getLowerThan(),
                                                    Pageable.ofSize(distPagination.getSize()),
                                                    loggedInUser.getLatitude(),
                                                    loggedInUser.getLongitude()
                                            ) : serviceRepository
                                    .findAllByDistanceBetween(
                                            distPagination.getGreaterThan(),
                                            distPagination.getLowerThan(),
                                            Pageable.ofSize(distPagination.getSize()),
                                            loggedInUser.getLatitude(),
                                            loggedInUser.getLongitude()
                                    )
                    );
                }
                break;
        }


        return entityList.stream()
                .map(service -> mapToDto(service, Optional.of(loggedInUser)))
                .collect(Collectors.toUnmodifiableList());
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

    private static final int MAX_CREDIT_LIMIT = 30;

    @Transactional
    public Long upsert(Principal principal, ServiceDto dto) {
        //check token => if username is null, throw an error
        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");

        try {
            var entityExists = false;
            Optional<Service> existingService = Optional.empty();
            if (dto.getId() != null) {
                final var entityQuery = serviceRepository.findById(dto.getId());
                if (entityQuery.isPresent()) {
                    existingService = entityQuery;
                    entityExists = true;
                    dto.setLocationType(entityQuery.get().getLocationType());
                }
            }
            var entity = new Service();


            if (entityExists)
            {
                entity = existingService.get();
            }
            else
            {
                entity = mapToEntity(dto);
            }

            // check for editing deadline
            if (entityExists) {
                if (entity.getLocationType().equals(LocationType.Physical)) {
                    if (LocalDateTime.now().isAfter(existingService.get().getTime().minusHours(24))) {
                        throw new IllegalArgumentException("You can only edit physical services until 24 hours before their time");
                    }
                    if (dto.getLatitude() != null)
                    {
                        entity.setLatitude(dto.getLatitude());
                    }
                    if (dto.getLongitude() != null)
                    {
                        entity.setLongitude(dto.getLongitude());
                    }
                } else {
                    if (LocalDateTime.now().isAfter(existingService.get().getTime().minusMinutes(30))) {
                        throw new IllegalArgumentException("You can only edit online services until 30 mimutes before their time");
                    }
                }
                if (dto.getLocation() != null)
                {
                    entity.setLocation(dto.getLocation());
                }
                entity.setHeader(dto.getHeader());
                entity.setDescription(dto.getDescription());
                entity.setTime(dto.getTime());
                entity.setQuota(dto.getQuota());
                entity.setCredit(dto.getHours());
                entity.setImageUrl(dto.getImageUrl());
            }

            entity.setCreatedUser(loggedInUser);

            var tags = dto.getServiceTags();
            if (entityExists)
                entity.setServiceTags(new HashSet<>());
            if (tags != null) {
                for (TagDto tagDto : tags) {
                    var addedTag = tagRepository.findById(tagDto.getId());
                    if (addedTag.isEmpty()) {
                        throw new IllegalArgumentException("There is no tag with this Id.");

                    }
                    entity.addTag(addedTag.get());
                }
            }

            if (!entityExists) {
                //check pending credits and balance if the sum is above 20 => throw an error
                var currentUserBalance = userService.getBalanceToBe(loggedInUser);
                var balanceToBe = currentUserBalance + dto.getHours();
                if (balanceToBe >= MAX_CREDIT_LIMIT)
                    throw new IllegalArgumentException("You have reached the maximum limit of credits. You cannot create a service before spending your credits.");
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

    public ServiceDto mapToDto(Service service, Optional<User> loggedInUser) {
        var list = new ArrayList<TagDto>();
        if (service.getServiceTags() != null) {
            for (Tag tag : service.getServiceTags()) {
                var dto = new TagDto(tag.getId(), tag.getName());
                list.add(dto);
            }
        }
        var approvals = service.getApprovalSet();
        var attendingUserList =  approvals.stream().filter(x -> x.getApprovalStatus() == ApprovalStatus.APPROVED).map(users -> userService.mapUserToDTO(users.getUser(), false)).collect(Collectors.toList());

        Double distanceToUser;

        if (loggedInUser.isPresent() && service.getLocationType().equals(LocationType.Physical)) {

            distanceToUser = getDistance(service.getLatitude(), service.getLongitude(), loggedInUser.get().getLatitude(), loggedInUser.get().getLongitude());

        } else {
            distanceToUser = null;
        }
        var attending = approvals.stream().filter(x -> x.getApprovalStatus() == ApprovalStatus.APPROVED).count();
        var pending = approvals.stream().filter(x -> x.getApprovalStatus() == ApprovalStatus.PENDING).count();
        long flagCount = flagRepository.countByTypeAndFlaggedEntityAndStatus(FlagType.service, service.getId(), FlagStatus.active);
        return new ServiceDto(service.getId(), service.getHeader(), service.getDescription(), service.getLocationType(), service.getLocation(), service.getTime(), service.getCredit(), service.getQuota(), attending, service.getCreatedUser().getId(), service.getCreatedUser().getUsername(), service.getLatitude(), service.getLongitude(), list, service.getStatus(), pending, distanceToUser, attendingUserList, ratingService.getServiceRatingSummary(service), flagCount, service.getImageUrl(), service.isFeatured(), service.getCreated());

    }


    private Service mapToEntity(ServiceDto dto) {
        if(dto.getLocationType().equals(LocationType.Online))
            return Service.createOnline(null, dto.getHeader(), dto.getDescription(), dto.getLocation(), dto.getTime(), dto.getHours(), dto.getQuota(), 0, null,  dto.getImageUrl(),null);
        else
            return Service.createPhysical(null, dto.getHeader(), dto.getDescription(), dto.getLocation(), dto.getTime(), dto.getHours(), dto.getQuota(), 0, null, dto.getLatitude(), dto.getLongitude(), null, dto.getImageUrl());
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

    @Transactional
    public List<ServiceDto> getAllFlaggedServices(Principal principal) {
        try {
            final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
            List<Flag> serviceFlags = flagRepository.findAllByTypeAndStatus(FlagType.service, FlagStatus.active);
            // remove duplicates
            List<ServiceDto> flaggedServices = new ArrayList<>();
            List<Long> ids = new ArrayList<>();
            for (Flag flag : serviceFlags) {
                Service service = serviceRepository.getById(flag.getFlaggedEntity());
                if(ids.contains(service.getId())) {
                    continue;
                }
                flaggedServices.add(mapToDto(service, Optional.of(loggedInUser)));
                ids.add(service.getId());
            }
            Collections.sort(flaggedServices, (o1, o2) -> o2.getFlagCount().intValue() - o1.getFlagCount().intValue());
            return flaggedServices;
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

    @Transactional
    public ServiceDto cancelService(Long serviceId, Principal principal) {
        Optional<User> loggedInUser = userRepository.findUserByUsername(principal.getName());
        Optional<Service> service = serviceRepository.findById(serviceId);
        //
        if (loggedInUser.isEmpty())
            throw new IllegalArgumentException("User doesn't exist");
        if (service.isEmpty())
            throw new IllegalArgumentException("Service doesn't exist");
        if (!loggedInUser.get().getCreatedServices().contains(service.get()))
            throw new IllegalArgumentException("You cannot cancel a service of another user.");
        //
        Service serviceToCancel = service.get();
        serviceToCancel.setStatus(ServiceStatus.CANCELLED);
        serviceToCancel = serviceRepository.save(serviceToCancel);
        // check for cancellation deadline
        User owner = loggedInUser.get();
        if ((serviceToCancel.getLocationType().equals(LocationType.Online) && serviceToCancel.getTime().minusMinutes(30).isBefore(LocalDateTime.now()))
                || (serviceToCancel.getLocationType().equals(LocationType.Physical) && serviceToCancel.getTime().minusHours(24).isBefore(LocalDateTime.now()))) {
            owner.setReputationPoint(owner.getReputationPoint() - 5);
            userRepository.save(owner);
        }
        ServiceDto serviceToCancelDto =  mapToDto(serviceToCancel, Optional.empty());
        // send notifications to participating users
        String text = new StringBuilder().append(owner.getUsername()).append(" cancelled their service ").append(serviceToCancel.getHeader()).append(".").toString();
        String url  = new StringBuilder().append("/service/").append(serviceToCancel.getId()).toString();
        for (UserDto participant : serviceToCancelDto.getParticipantUserList()) {
            User user = userRepository.getById(participant.getId());
            notificationService.sendNotification(text, url, user);
        }
        return serviceToCancelDto;
    }

    public List<SimpleServiceDto> getAllForAdminDashboard(Principal principal) {
        final var loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (!loggedInUser.getUserType().equals(UserType.ADMIN)) {
            throw new IllegalArgumentException("You need to be admin to perform this action.");
        }
        var services = serviceRepository.findAll().stream().filter(x-> x.getLocationType() == LocationType.Physical).map(x-> new SimpleServiceDto(x.getId(),x.getHeader(), x.getLocationType(), x.getLocation(), x.getLatitude(), x.getLongitude()
        )).collect(Collectors.toList());
        return services;
    }
}
