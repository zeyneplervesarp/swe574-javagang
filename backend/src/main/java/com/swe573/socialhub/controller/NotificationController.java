package com.swe573.socialhub.controller;

import com.swe573.socialhub.dto.NotificationDto;
import com.swe573.socialhub.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;



@RestController
@RequestMapping("/notification")
public class NotificationController {
    @Autowired
    private NotificationService service;

    @GetMapping("/getByUser")
    public ResponseEntity<List<NotificationDto>> getNotificationByUser(Principal principal) {
        try {
            var list = service.getListByUser(principal);
            return ResponseEntity.ok(list);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/readAllByUser")
    public ResponseEntity<Boolean> readAllByUser(Principal principal) {
        try {
            var result = service.setStatusToRead(principal);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }
}

