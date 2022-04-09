package com.swe573.socialhub.controller;

import com.swe573.socialhub.dto.SimpleEventApprovalDto;
import com.swe573.socialhub.dto.UserEventApprovalDto;
import com.swe573.socialhub.enums.ApprovalStatus;
import com.swe573.socialhub.service.UserEventApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/eventApproval")
public class UserEventApprovalController {

    @Autowired
    private UserEventApprovalService service;

    @GetMapping("/request/{eventId}")
    public ResponseEntity<UserEventApprovalDto> App(Principal principal, @PathVariable Long eventId) {
        try {
            var dto = service.RequestApproval(principal, eventId);
            return ResponseEntity.ok().body(dto);
        }
        catch (RuntimeException e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/userRequests")
    public ResponseEntity<List<UserEventApprovalDto>> getApprovalListRequestByUser(Principal principal) {
        try {
            List<UserEventApprovalDto> events = service.findEventRequestsByUser(principal);
            return ResponseEntity.ok().body(events);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @PostMapping("/approve")

    public ResponseEntity<String> approveRequest(@RequestBody SimpleEventApprovalDto dto) {
        try {
            service.updateRequestStatus(dto, ApprovalStatus.APPROVED );
            return ResponseEntity.ok().body("ok");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @PostMapping("/deny")

    public ResponseEntity<String> denyRequest(@RequestBody SimpleEventApprovalDto dto) {
        try {
            service.updateRequestStatus(dto, ApprovalStatus.DENIED);
            return ResponseEntity.ok().body("ok");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }



}
