package com.swe573.socialhub.controller;

import com.swe573.socialhub.dto.StatsDto;
import com.swe573.socialhub.service.StatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping(path = "/stats")
    public ResponseEntity<StatsDto> fetchStats(Principal principal) {
        try {
            return ResponseEntity.ok(statsService.fetchStats(principal));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getLocalizedMessage());
        }
    }
}
