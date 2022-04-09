package com.swe573.socialhub.controller;

import com.swe573.socialhub.domain.Flag;
import com.swe573.socialhub.service.RatingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
@RequestMapping("/rating")
public class RatingController {

    private final RatingService service;

    public RatingController(RatingService service) {
        this.service = service;
    }

    @PostMapping("/service/{serviceId}/{rating}")
    public ResponseEntity<Void> rateService(Principal principal, @PathVariable Long serviceId, @PathVariable int rating) {
        try {
            service.addOrUpdateRating(principal, serviceId, rating);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }
}
