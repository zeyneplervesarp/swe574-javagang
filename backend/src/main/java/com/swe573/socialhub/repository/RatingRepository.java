package com.swe573.socialhub.repository;

import com.swe573.socialhub.domain.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, Long> {
}
