package com.swe573.socialhub.service;

import com.swe573.socialhub.domain.Rating;
import com.swe573.socialhub.dto.RatingSummaryDto;
import com.swe573.socialhub.repository.RatingRepository;
import com.swe573.socialhub.repository.ServiceRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class RatingService {

    private final RatingRepository repository;
    private final ServiceRepository serviceRepository;

    public RatingService(RatingRepository repository, ServiceRepository serviceRepository) {
        this.repository = repository;
        this.serviceRepository = serviceRepository;
    }

    public Rating addOrUpdateRating(Principal principal, Long serviceId, int rating) {
        return null;
    }

    public RatingSummaryDto getServiceRatingSummary(Long serviceId) {
        return null;
    }

    public RatingSummaryDto getUserRatingSummary(Long userId) {
        return null;
    }
}
