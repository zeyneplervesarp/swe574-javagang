package com.swe573.socialhub.controller;

import com.swe573.socialhub.dto.DistanceBasedPagination;
import com.swe573.socialhub.dto.PaginatedListResponse;
import com.swe573.socialhub.dto.ServiceDto;
import com.swe573.socialhub.service.RecommendationsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class RecommendationsController {

    private final RecommendationsService service;

    public RecommendationsController(RecommendationsService service) {
        this.service = service;
    }

    @GetMapping("/getRecommendedServices")
    public PaginatedListResponse<ServiceDto> getRecommendedServices(
            @RequestParam(required = false) String gt,
            @RequestParam(required = false) String lt,
            @RequestParam(required = false) Integer size,
            Principal principal
    ) {
        gt = gt != null ? gt : String.valueOf(DistanceBasedPagination.DEFAULT_GT);
        lt = lt != null ? lt : String.valueOf(DistanceBasedPagination.DEFAULT_LT);
        var dsPagination = ControllerUtils.parseDistancePagination(Double.valueOf(gt), Double.valueOf(lt), size);
        var items = service.getRecommendedServices(principal, dsPagination);
        return new PaginatedListResponse<>(items, "getRecommendedServices", "", dsPagination, ServiceDto::getDistanceToUser);
    }

}
