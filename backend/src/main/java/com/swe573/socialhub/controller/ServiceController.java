package com.swe573.socialhub.controller;

import com.swe573.socialhub.domain.Flag;
import com.swe573.socialhub.dto.ServiceDto;
import com.swe573.socialhub.enums.ServiceFilter;
import com.swe573.socialhub.enums.ServiceSortBy;
import com.swe573.socialhub.service.ServiceService;
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
@RequestMapping("/service")

public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    @GetMapping("/{getOngoingOnly}/{filter}")
    @ResponseBody
    public List<ServiceDto> findAllServices(@RequestParam (required = false) ServiceSortBy sortBy, Principal principal, @PathVariable Boolean getOngoingOnly, @PathVariable(value = "filter") ServiceFilter filter) {

        try {
            return serviceService.findAllServices(principal,getOngoingOnly,filter,sortBy);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getLocalizedMessage());
        }
    }

    @GetMapping
    @ResponseBody
    public List<ServiceDto> findAllServices(@RequestParam (required = false) String sortBy)  {
        try {
            var foo = sortBy;
            return serviceService.findAllServices();
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

    @DeleteMapping("/{id}")
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
