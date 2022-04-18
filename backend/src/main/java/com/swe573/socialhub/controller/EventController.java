package com.swe573.socialhub.controller;

import com.swe573.socialhub.domain.Flag;
import com.swe573.socialhub.dto.EventDto;
import com.swe573.socialhub.dto.ServiceDto;
import com.swe573.socialhub.enums.ServiceFilter;
import com.swe573.socialhub.enums.ServiceSortBy;
import com.swe573.socialhub.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/event")

public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping("/{getOngoingOnly}/{filter}")
    @ResponseBody
    public List<EventDto> findAllEvents(@RequestParam(required = false) ServiceSortBy sortBy, Principal principal, @PathVariable Boolean getOngoingOnly, @PathVariable(value = "filter") ServiceFilter filter) {

        try {
            return eventService.findAllEvents(principal,getOngoingOnly,filter,sortBy);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping
    @ResponseBody
    public List<EventDto> findAllEvents(@RequestParam (required = false) String sortBy)  {
        try {
            return eventService.findAllEvents();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<EventDto> deleteEvent(@PathVariable(value = "id") long id, Principal principal) {
        try {
            return ResponseEntity.ok(eventService.deleteEvent(id, principal));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDto> findEventById(@PathVariable(value = "id") long id) {
        try {
            Optional<EventDto> event = eventService.findById(id);

            if (event.isPresent()) {
                return ResponseEntity.ok().body(event.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/userEvent")
    public ResponseEntity<List<EventDto>> getListByUser(Principal principal) {
        try {
            var events = eventService.findByUser(principal);
            return ResponseEntity.ok().body(events);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }
    @PostMapping
    public ResponseEntity<Long> saveEvent(Principal principal, @Validated @RequestBody EventDto event) {
        try {
            var result = eventService.save(principal, event);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }


    @GetMapping("/approve/{eventId}")
    public void App(Principal principal, @PathVariable Long eventId) {
        try {
            eventService.approve(principal, eventId);
        }
        catch (RuntimeException e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/complete/{eventId}")
    public void Complete(Principal principal, @PathVariable Long eventId) {
        try {
            eventService.complete(principal, eventId);
        }
        catch (RuntimeException e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @PostMapping("/flag/{eventId}")
    public ResponseEntity<Flag> flagEvent(Principal principal, @PathVariable Long eventId) {
        try {
            Flag response = eventService.flagEvent(principal, eventId);
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/flag/control/{eventId}")
    public ResponseEntity<Boolean> checkForExistingFlag(Principal principal, @PathVariable Long eventId) {
        try {
            Boolean response = eventService.checkForExistingFlag(principal, eventId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }
}