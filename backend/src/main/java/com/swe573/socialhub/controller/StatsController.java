package com.swe573.socialhub.controller;

import com.swe573.socialhub.dto.*;
import com.swe573.socialhub.service.StatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.sql.Date;
import java.time.Instant;

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

    @GetMapping(path = "/dailystats")
    @ResponseBody
    public PaginatedResponse<DailyStatsDto> fetchDailyStatus(
            Principal principal,
            @RequestParam(required = false) Long gt,
            @RequestParam(required = false) Long lt,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    )  {
        try {
            final var pagination = ControllerUtils.parseTimestampPagination(gt, lt, size, sort);
            return statsService.fetchDailyStats(principal, pagination, "dailystats");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }
}
