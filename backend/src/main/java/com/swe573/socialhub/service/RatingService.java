package com.swe573.socialhub.service;

import com.swe573.socialhub.domain.Rating;
import com.swe573.socialhub.domain.User;
import com.swe573.socialhub.dto.RatingSummaryDto;
import com.swe573.socialhub.enums.ApprovalStatus;
import com.swe573.socialhub.enums.ServiceStatus;
import com.swe573.socialhub.repository.RatingRepository;
import com.swe573.socialhub.repository.ServiceRepository;
import com.swe573.socialhub.repository.UserRepository;
import com.swe573.socialhub.repository.UserServiceApprovalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
public class RatingService {

    private final RatingRepository repository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final UserServiceApprovalRepository approvalRepository;

    private static final int MIN_RATING = 0;
    private static final int MAX_RATING = 5;


    public RatingService(RatingRepository repository, ServiceRepository serviceRepository, UserRepository userRepository, UserServiceApprovalRepository approvalRepository) {
        this.repository = repository;
        this.serviceRepository = serviceRepository;
        this.userRepository = userRepository;
        this.approvalRepository = approvalRepository;
    }

    @Transactional
    public Rating addOrUpdateRating(Principal principal, Long serviceId, int rating) {
        if (rating > MAX_RATING || rating < MIN_RATING) {
            throw new IllegalArgumentException("Invalid rating. Min rating is " + MIN_RATING + ", max rating is " + MAX_RATING + ".");
        }

        final User loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        final var requestedService = serviceRepository.findById(serviceId);
        if (requestedService.isEmpty()) {
            throw new IllegalArgumentException("Service doesn't exist.");
        }
        final var service = requestedService.get();
        if (service.getStatus() != ServiceStatus.COMPLETED) {
            throw new IllegalArgumentException("Can't rate a service unless it's completed.");
        }

        final var isRaterAttending = approvalRepository
                .findUserServiceApprovalByService_IdAndApprovalStatus(serviceId, ApprovalStatus.APPROVED)
                .stream()
                .map(s -> s.getUser().getId())
                .anyMatch(userId -> loggedInUser.getId().equals(userId));

        if (!isRaterAttending) {
            throw new IllegalArgumentException("You can only rate services where you are in approved attendee list.");
        }

        final var existingRatings = repository.findRatingByRaterAndService(loggedInUser, service);
        if (existingRatings.isEmpty()) {
            final var ratingEntity = new Rating(service, rating, loggedInUser);
            return repository.save(ratingEntity);
        }

        final var ratingEntity = existingRatings.get(0);
        ratingEntity.setRating(rating);
        return ratingEntity;
    }

    public RatingSummaryDto getServiceRatingSummary(Long serviceId) {
        return null;
    }

    public RatingSummaryDto getUserRatingSummary(Long userId) {
        return null;
    }
}
