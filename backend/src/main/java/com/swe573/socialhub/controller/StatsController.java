package com.swe573.socialhub.controller;

import com.swe573.socialhub.dto.StatsDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class StatsController {

    @GetMapping(path = "/stats")
    public ResponseEntity<StatsDto> fetchStats(Principal principal) {
        return null; // TODO impl
    }
}
