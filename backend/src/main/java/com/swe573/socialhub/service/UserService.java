package com.swe573.socialhub.service;

import com.swe573.socialhub.domain.Flag;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.domain.UserFollowing;
import com.swe573.socialhub.dto.*;
import com.swe573.socialhub.enums.ApprovalStatus;
import com.swe573.socialhub.enums.FlagStatus;
import com.swe573.socialhub.enums.FlagType;
import com.swe573.socialhub.enums.ServiceStatus;
import com.swe573.socialhub.repository.*;
import com.swe573.socialhub.config.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.sasl.AuthenticationException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private FlagRepository flagRepository;

    @Autowired
    private UserServiceApprovalRepository userServiceApprovalRepository;

    @Autowired
    private UserEventApprovalRepository userEventApprovalRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserFollowingRepository userFollowingRepository;

    @Autowired
    private RatingService ratingService;


    @Transactional
    public UserDto register(UserDto dto) {
        //validate model
        if (dto.getPassword() == "" || dto.getUsername() == "" || dto.getEmail() == "" || dto.getBio() == "")
            throw new IllegalArgumentException("Please fill all the required fields.");

        //hash password
        final String passwordHash = passwordEncoder.encode(dto.getPassword());

        //create entity and set fields
        final User userEntity = new User();
        userEntity.setBio(dto.getBio());
        userEntity.setEmail(dto.getPassword());
        userEntity.setPassword(passwordHash);
        userEntity.setUsername(dto.getUsername());
        userEntity.setBalance(5);
        userEntity.setLongitude(dto.getLongitude());
        userEntity.setLatitude(dto.getLatitude());
        userEntity.setFormattedAddress(dto.getFormattedAddress());

        //set tags
        var tags = dto.getUserTags();
        if (tags != null) {
            for (TagDto tagDto : tags) {
                var addedTag = tagRepository.findById(tagDto.getId());
                if (addedTag.isEmpty()) {
                    throw new IllegalArgumentException("There is no tag with this Id.");
                }
                userEntity.addTag(addedTag.get());
            }
        }


        try {
            //save entity
            final User createdUser = repository.save(userEntity);
            return mapUserToDTO(createdUser);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public UserDto login(LoginDto dto) {
        try {
            var userName = dto.getUsername();
            var dbResult = repository.findUserByUsername(userName);
            if (!dbResult.isPresent())
                throw new IllegalArgumentException("Invalid username");

            var user = dbResult.get();
            var passwordMatch = passwordEncoder.matches(dto.getPassword(), user.getPassword());

            if (passwordMatch) {
                return mapUserToDTO(user);

            } else {
                throw new IllegalArgumentException("Invalid password");

            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public JwtDto createAuthenticationToken(LoginDto authenticationRequest) throws AuthenticationException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new AuthenticationException();
        }


        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());

        final String jwt = jwtTokenUtil.generateToken(userDetails);

        return new JwtDto(jwt);
    }

    public UserDto getUser(String id, Principal principal) {
        final User loggedInUser = repository.findUserByUsername(principal.getName()).get();
        final Optional<User> userOption = repository.findById(Long.valueOf(id));
        if (userOption.isEmpty())
            throw new IllegalArgumentException("User doesn't exist.");
        return mapUserToDTO(userOption.get());
    }

    public List<UserDto> getAllUsers() {
        final List<User> users = repository.findAll();
        List<UserDto> list = new ArrayList<UserDto>();
        for (User user : users) {
            var dto = mapUserToDTO(user);
            list.add(dto);
        }
        return list;
    }

    public UserDto mapUserToDTO(User user) {
        var notificationList = new ArrayList<NotificationDto>();
        if (user.getNotificationSet() != null) {
            for (var notification : user.getNotificationSet()) {
                var dto = notificationService.mapNotificationToDTO(notification);
                notificationList.add(dto);
            }
        }

        var approvalList = userServiceApprovalRepository.findUserServiceApprovalByUserAndApprovalStatus(user, ApprovalStatus.PENDING);
        var balanceOnHold = approvalList.stream().mapToInt(o -> o.getService().getCredit()).sum();
        long flagCount = flagRepository.countByTypeAndFlaggedEntityAndStatus(FlagType.user, user.getId(), FlagStatus.active);

        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getBio(),
                user.getBalance(),
                notificationList,
                balanceOnHold,
                user.getLatitude(),
                user.getLongitude(),
                user.getFormattedAddress(),
                user.getFollowedBy().stream().map(u -> u.getFollowingUser().getUsername()).collect(Collectors.toUnmodifiableList()),
                user.getFollowingUsers().stream().map(u -> u.getFollowedUser().getUsername()).collect(Collectors.toUnmodifiableList()),
                user.getTags().stream().map(x-> new TagDto(x.getId(), x.getName())).collect(Collectors.toUnmodifiableList()),
                ratingService.getUserRatingSummary(user),
                user.getUserType(), flagCount);


    }

    public UserDto getUserByUsername(String userName, Principal principal) {
        final User loggedInUser = repository.findUserByUsername(principal.getName()).get();
        final Optional<User> userOption = repository.findUserByUsername(userName);
        if (userOption.isEmpty())
            throw new IllegalArgumentException("User doesn't exist.");
        return mapUserToDTO(userOption.get());
    }

    public UserDto getUserByPrincipal(Principal principal) {
        final User loggedInUser = repository.findUserByUsername(principal.getName()).get();
        var dto = mapUserToDTO(loggedInUser);
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");
        return dto;
    }

    public UserServiceDto getUserServiceDetails(Principal principal, Long serviceId) {
        final User loggedInUser = repository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");
        var serviceOptional = serviceRepository.findById(serviceId);
        if (serviceOptional == null) {
            throw new IllegalArgumentException("Service doesn't exist.");
        }
        var service = serviceOptional.get();
        var ownsService = Objects.equals(service.getCreatedUser().getId(), loggedInUser.getId());
        var userServiceApproval = userServiceApprovalRepository.findUserServiceApprovalByServiceAndUser(service, loggedInUser);
        var attendsService = userServiceApproval.isPresent() && userServiceApproval.get().getApprovalStatus().name().equals("APPROVED");
        var dto = new UserServiceDto(userServiceApproval != null && userServiceApproval.isPresent(), ownsService, attendsService);
        return dto;

    }

    public UserEventDto getUserEventDetails(Principal principal, Long eventId) {
        final User loggedInUser = repository.findUserByUsername(principal.getName()).get();
        if (loggedInUser == null)
            throw new IllegalArgumentException("User doesn't exist.");
        var eventOptional = eventRepository.findById(eventId);
        if (eventOptional == null) {
            throw new IllegalArgumentException("Event doesn't exist.");
        }
        var event = eventOptional.get();
        var ownsEvent = Objects.equals(event.getCreatedUser().getId(), loggedInUser.getId());
        var userEventApproval = userEventApprovalRepository.findUserEventApprovalByEventAndUser(event, loggedInUser);
        var attendsEvent = userEventApproval.isPresent() && userEventApproval.get().getApprovalStatus().name().equals("APPROVED");
        var dto = new UserEventDto(userEventApproval != null && userEventApproval.isPresent(), ownsEvent, attendsEvent);
        return dto;
    }

    public int getBalanceToBe(User user) {
        var currentUserCreditsInApprovalState = userServiceApprovalRepository.findUserServiceApprovalByUserAndApprovalStatus(user, ApprovalStatus.PENDING);
        var creditsToRemove = currentUserCreditsInApprovalState.stream().mapToInt(o -> o.getService().getCredit()).sum();
        var activeServices = serviceRepository.findServiceByCreatedUserAndStatus(user, ServiceStatus.ONGOING);
        var creditsToAdd = activeServices.stream().mapToInt(x -> x.getCredit()).sum();
        var currentUserBalance = user.getBalance();
        var balanceToBe = currentUserBalance + creditsToAdd + creditsToRemove;
        return balanceToBe;
    }


    public UserFollowing follow(Principal principal, Long userId) {
        //get current user and user to follow
        final User loggedInUser = repository.findUserByUsername(principal.getName()).get();
        var userToFollow = repository.findById(userId).get();

        //check if there is already a following entity to avoid duplicates
        var entityResult = userFollowingRepository.findUserFollowingByFollowingUserAndFollowedUser(loggedInUser, userToFollow);
        var entityExists = entityResult.isPresent();
        if (entityExists)
            throw new IllegalArgumentException("You are already following user " + userToFollow.getUsername());


        try {
            //create and save entity
            var entity = new UserFollowing(loggedInUser, userToFollow);
            var returnEntity = userFollowingRepository.save(entity);

            //send notification
            notificationService.sendNotification("You are being followed by " + loggedInUser.getUsername(), "/profile/" + loggedInUser.getUsername(), userToFollow);

            return returnEntity;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Boolean followControl(Principal principal, Long userId) {
        try {
            //get current user and user to follow
            final User loggedInUser = repository.findUserByUsername(principal.getName()).get();
            var userToFollow = repository.findById(userId).get();

            //check if there is already a following entity
            var entityResult = userFollowingRepository.findUserFollowingByFollowingUserAndFollowedUser(loggedInUser, userToFollow);
            var entityExists = entityResult.isPresent();
            return entityExists;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Flag flagUser(Principal principal, Long toFlagUserId) {
        // get current user and user to flag
        final User loggedInUser = repository.findUserByUsername(principal.getName()).get();
        User userToFlag = repository.findById(toFlagUserId).get();
        // check for existing flags for duplicates
        Optional<Flag> existingFlag = flagRepository.findFlagByFlaggingUserAndFlaggedEntityAndType(loggedInUser.getId(), toFlagUserId, FlagType.user);
        if (existingFlag.isPresent()) {
            throw new IllegalArgumentException("You have already flagged user " + userToFlag.getUsername());
        }
        // flag the user
        try {
            // create flag
            Flag flag = new Flag(FlagType.user, loggedInUser.getId(), toFlagUserId, FlagStatus.active);
            return flagRepository.save(flag);
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public Boolean checkExistingFlag(Principal principal, Long toFlagUserId) {
        try {
            // get current user and user to flag
            final User loggedInUser = repository.findUserByUsername(principal.getName()).get();
            // check for existing flags for duplicates
            Optional<Flag> existingFlag = flagRepository.findFlagByFlaggingUserAndFlaggedEntityAndType(loggedInUser.getId(), toFlagUserId, FlagType.user);
            return existingFlag.isPresent();
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }


    }
}