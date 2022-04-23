package com.swe573.socialhub.repository;

import com.swe573.socialhub.domain.Rating;
import com.swe573.socialhub.domain.Service;
import com.swe573.socialhub.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findRatingByRaterAndService(User rater, Service service);
    void deleteAllByRater(User rater);
}
