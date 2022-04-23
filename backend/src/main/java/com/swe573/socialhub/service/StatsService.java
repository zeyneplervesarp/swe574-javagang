package com.swe573.socialhub.service;

import com.swe573.socialhub.dto.StatsDto;
import com.swe573.socialhub.enums.UserType;
import com.swe573.socialhub.repository.UserRepository;

import java.security.Principal;

public class StatsService {

    private final UserRepository userRepository;

    public StatsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public StatsDto fetchStats(Principal principal) {
        final var loggedInUser = userRepository.findUserByUsername(principal.getName()).get();
        if (!loggedInUser.getUserType().equals(UserType.ADMIN)) {
            throw new IllegalArgumentException("You need to be admin to perform this action.");
        }
        return null; // TODO: impl
    }
}
