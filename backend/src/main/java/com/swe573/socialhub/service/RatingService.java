package com.swe573.socialhub.service;

import com.swe573.socialhub.domain.Rating;
import com.swe573.socialhub.dto.RatingSummaryDto;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class RatingService {
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
