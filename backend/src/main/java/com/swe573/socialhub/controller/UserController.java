package com.swe573.socialhub.controller;

import com.swe573.socialhub.domain.Flag;
import com.swe573.socialhub.domain.UserFollowing;
import com.swe573.socialhub.dto.*;
import com.swe573.socialhub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.security.sasl.AuthenticationException;
import java.security.Principal;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/register")
    public ResponseEntity<JwtDto> signUp(@RequestBody UserDto dto) {
        try {
            service.register(dto);
            return ResponseEntity.ok(service.createAuthenticationToken(new LoginDto(dto.getPassword(),dto.getUsername())));
        } catch (IllegalArgumentException | DuplicateKeyException | AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @PostMapping("/user/setTags")
    public ResponseEntity<JwtDto> setTags(@RequestBody List<TagDto> tags) {
        return null;
    }
    @PostMapping("/login")
    public ResponseEntity<JwtDto> login(@RequestBody LoginDto dto) {
        try {
            service.login(dto);
            return ResponseEntity.ok(service.createAuthenticationToken(dto));
        } catch (IllegalArgumentException | DuplicateKeyException | AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/user")
    public ResponseEntity<UserDto> getUser(Principal principal) {
        try {
            var response = service.getUserByPrincipal(principal);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public UserDto getUser(Principal principal, @PathVariable String userId) {
        try {
            return service.getUser(userId, principal);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @DeleteMapping("/user/delete/{userId}")
    public UserDto deleteUser(Principal principal, @PathVariable(value = "userId") long userId) {
        try {
            return service.deleteUser(Long.valueOf(userId), principal);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getLocalizedMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/user/getServiceDetails/{serviceId}")
    public UserServiceDto getServiceDetails(Principal principal, @PathVariable Long serviceId) {
        try {
            return service.getUserServiceDetails(principal, serviceId);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/user/getEventDetails/{eventId}")
    public UserEventDto getEventDetails(Principal principal, @PathVariable Long eventId) {
        try {
            return service.getUserEventDetails(principal, eventId);

        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/user/getAll")
    public List<UserDto> getAllUsers(Principal principal) {
        return service.getAllUsers();
    }

    @GetMapping("/user/follow/{userId}")
    public ResponseEntity<UserFollowing> followUser(Principal principal, @PathVariable Long userId) {
        try {
            var response = service.follow(principal,userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/user/follow/control/{userId}")
    public ResponseEntity<Boolean> controlUserFollow(Principal principal, @PathVariable Long userId) {
        try {
            Boolean response = service.followControl(principal,userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @PostMapping("/user/flag/{userId}")
    public ResponseEntity<Flag> flagUser(Principal principal, @PathVariable Long userId) {
        try {
            Flag response = service.flagUser(principal, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/user/flag/control/{userId}")
    public ResponseEntity<Boolean> checkForExistingFlag(Principal principal, @PathVariable Long userId) {
        try {
            Boolean response = service.checkExistingFlag(principal, userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @PostMapping("/user/flag/dismiss/{userId}")
    public ResponseEntity<Boolean> dismissFlags(Principal principal, @PathVariable Long userId) {
        try {
            service.dismissFlags(principal, userId);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/user/flag")
    public List<UserDto> getAllFlaggedUsers(Principal principal) {
        try{
            return service.getAllFlaggedUsers();
        } catch(Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }
}