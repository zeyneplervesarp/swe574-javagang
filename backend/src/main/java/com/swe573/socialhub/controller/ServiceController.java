package com.swe573.socialhub.controller;

import com.swe573.socialhub.domain.Flag;
import com.swe573.socialhub.dto.*;
import com.swe573.socialhub.enums.ServiceFilter;
import com.swe573.socialhub.enums.ServiceSortBy;
import com.swe573.socialhub.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.sql.Date;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/service")

public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping("/{getOngoingOnly}/{filter}")
    @ResponseBody
    public PaginatedResponse<ServiceDto> findAllServices(
            Principal principal,
            @RequestParam (required = false, defaultValue = "createdDateDesc") ServiceSortBy sortBy,
            @RequestParam(required = false) String gt,
            @RequestParam(required = false) String lt,
            @RequestParam(required = false) Integer size,
            @PathVariable Boolean getOngoingOnly,
            @PathVariable(value = "filter") ServiceFilter filter
    ) {
        final var started = Instant.now();
        try {


            final var urlPrefix = "service/" + getOngoingOnly.toString() + "/" + filter.toString();
            final var sortBySuffix = "&sortBy=" + sortBy.toString();
            gt = gt != null ? gt : String.valueOf(TimestampBasedPagination.DEFAULT_GT.toInstant().toEpochMilli());
            lt = lt != null ? lt : String.valueOf(TimestampBasedPagination.DEFAULT_LT.toInstant().toEpochMilli());
            switch (sortBy) {
                case distanceAsc:
                    var dsPagination = ControllerUtils.parseDistancePagination(Double.valueOf(gt), Double.valueOf(lt), size);
                    var items = serviceService.findPaginatedOngoing(principal, getOngoingOnly, filter, dsPagination, sortBy);
                    return new PaginatedResponse<>(items, urlPrefix, sortBySuffix, dsPagination, ServiceDto::getDistanceToUser);
                case serviceDateDesc:
                    var tsPagination = ControllerUtils.parseTimestampPagination(Long.valueOf(gt), Long.valueOf(lt), size, "desc");
                    items = serviceService.findPaginatedOngoing(principal, getOngoingOnly, filter, tsPagination, sortBy);
                    return new PaginatedResponse<>(items, urlPrefix,sortBySuffix, tsPagination, item -> ControllerUtils.localDateTimeToDate(item.getTime()));
                case createdDateDesc:
                    tsPagination = ControllerUtils.parseTimestampPagination(Long.valueOf(gt), Long.valueOf(lt), size, "desc");
                    items = serviceService.findPaginatedOngoing(principal, getOngoingOnly, filter, tsPagination, sortBy);
                    return new PaginatedResponse<>(items, urlPrefix,sortBySuffix, tsPagination, item -> Date.from(Instant.ofEpochMilli(item.getCreatedTimestamp())) );
                case serviceDateAsc:
                    tsPagination = ControllerUtils.parseTimestampPagination(Long.valueOf(gt), Long.valueOf(lt), size, "asc");
                    items = serviceService.findPaginatedOngoing(principal, getOngoingOnly, filter, tsPagination, sortBy);
                    return new PaginatedResponse<>(items, urlPrefix,sortBySuffix, tsPagination, item -> ControllerUtils.localDateTimeToDate(item.getTime()));
                case createdDateAsc:
                    tsPagination = ControllerUtils.parseTimestampPagination(Long.valueOf(gt), Long.valueOf(lt), size, "asc");
                    items = serviceService.findPaginatedOngoing(principal, getOngoingOnly, filter, tsPagination, sortBy);
                    return new PaginatedResponse<>(items, urlPrefix,sortBySuffix, tsPagination, item -> Date.from(Instant.ofEpochMilli(item.getCreatedTimestamp())) );
            }

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request.");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        } finally {
            System.out.println("Service loading took " + (Instant.now().toEpochMilli() - started.toEpochMilli()) + " milliseconds.");
        }
    }

    @GetMapping
    @ResponseBody
    public PaginatedResponse<ServiceDto> findAllServices(
            @RequestParam(required = false) Long gt,
            @RequestParam(required = false) Long lt,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort
    )  {
        final var urlPrefix = "service";
        try {
            final var pagination = ControllerUtils.parseTimestampPagination(gt, lt, size, sort);
            final var items =  serviceService.findPaginatedOngoing(pagination);
            return new PaginatedResponse<>(items, urlPrefix, "", pagination, item -> Date.from(Instant.ofEpochMilli(item.getCreatedTimestamp())) );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceDto> findServiceById(@PathVariable(value = "id") long id) {
        try {
            Optional<ServiceDto> service = serviceService.findById(id);

            if (service.isPresent()) {
                return ResponseEntity.ok().body(service.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ServiceDto> deleteService(@PathVariable(value = "id") long id, Principal principal) {
        try {
            return ResponseEntity.ok(serviceService.deleteService(id, principal));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/userService")
    public ResponseEntity<List<ServiceDto>> getListByUser(Principal principal) {
        try {
            List<ServiceDto> services = serviceService.findByUser(principal);
            return ResponseEntity.ok().body(services);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @PostMapping
    public ResponseEntity<Long> upsertService(Principal principal, @RequestBody ServiceDto service) {
        try {
            var result = serviceService.upsert(principal, service);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/approve/{serviceId}")
    public void App(Principal principal, @PathVariable Long serviceId) {
        try {
            serviceService.approve(principal,serviceId);
        }
        catch (RuntimeException e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @PostMapping("/feature/{serviceId}")
    public ServiceDto featureService(Principal principal, @PathVariable Long serviceId) {
        try {
            return serviceService.featureService(serviceId, principal);
        }
        catch (RuntimeException e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @DeleteMapping("/feature/{serviceId}")
    public ServiceDto unfeatureService(Principal principal, @PathVariable Long serviceId) {
        try {
            return serviceService.removeFromFeaturedServices(serviceId, principal);
        }
        catch (RuntimeException e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/feature")
    public List<ServiceDto> getFeaturedServices(Principal principal) {
        try {
            return serviceService.getAllFeaturedServices(principal);
        }
        catch (RuntimeException e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/complete/{serviceId}")
    public void Complete(Principal principal, @PathVariable Long serviceId) {
        try {
            serviceService.complete(principal,serviceId);
        }
        catch (RuntimeException e)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @PostMapping("/flag/{serviceId}")
    public ResponseEntity<Flag> flagService(Principal principal, @PathVariable Long serviceId) {
        try {
            Flag response = serviceService.flagService(principal, serviceId);
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/flag/control/{serviceId}")
    public ResponseEntity<Boolean> checkForExistingFlag(Principal principal, @PathVariable Long serviceId) {
        try {
            Boolean response = serviceService.checkForExistingFlag(principal, serviceId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @PostMapping("/flag/dismiss/{serviceId}")
    public ResponseEntity<Boolean> dismissFlags(Principal principal, @PathVariable Long serviceId) {
        try {
            serviceService.dismissFlags(principal, serviceId);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping("/flag")
    public List<ServiceDto> getAllFlaggedServices(Principal principal) {
        try {
            return serviceService.getAllFlaggedServices(principal);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }
  
    @PostMapping("/cancel/{serviceId}")
    public ResponseEntity<ServiceDto> cancelService(Principal principal, @PathVariable Long serviceId) {
        try {
            ServiceDto serviceToCancel = serviceService.cancelService(serviceId, principal);
            return ResponseEntity.ok(serviceToCancel);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }
}
