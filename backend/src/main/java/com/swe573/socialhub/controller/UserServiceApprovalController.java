package com.swe573.socialhub.controller;

import com.swe573.socialhub.dto.SimpleApprovalDto;
import com.swe573.socialhub.dto.UserServiceApprovalDto;
import com.swe573.socialhub.enums.ApprovalStatus;
import com.swe573.socialhub.service.UserServiceApprovalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/approval")
public class UserServiceApprovalController {

    @Autowired
    private UserServiceApprovalService service;

    @GetMapping("/request/{serviceId}")
    public ResponseEntity<UserServiceApprovalDto> App(Principal principal, @PathVariable Long serviceId) {
        try {
            var dto = service.RequestApproval(principal,serviceId);
            return ResponseEntity.ok().body(dto);
        }
        catch (RuntimeException e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/userRequests")
    public ResponseEntity<List<UserServiceApprovalDto>> getApprovalListRequestByUser(Principal principal) {
        try {
            List<UserServiceApprovalDto> services = service.findServiceRequestsByUser(principal);
            return ResponseEntity.ok().body(services);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @PostMapping("/approve")

    public ResponseEntity<String> approveRequest(@RequestBody SimpleApprovalDto dto) {
        try {
             service.updateRequestStatus(dto, ApprovalStatus.APPROVED );
            return ResponseEntity.ok().body("ok");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @PostMapping("/deny")

    public ResponseEntity<String> denyRequest(@RequestBody SimpleApprovalDto dto) {
        try {
            service.updateRequestStatus(dto, ApprovalStatus.DENIED);
            return ResponseEntity.ok().body("ok");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }



}
