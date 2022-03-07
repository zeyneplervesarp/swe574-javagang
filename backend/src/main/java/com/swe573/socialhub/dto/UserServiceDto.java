package com.swe573.socialhub.dto;

public class UserServiceDto {
    private Boolean hasServiceRequest;
    private Boolean ownsService;
    private Boolean attendsService;

    public UserServiceDto(Boolean hasServiceRequest, Boolean ownsService, Boolean attendsService) {
        this.hasServiceRequest = hasServiceRequest;
        this.ownsService = ownsService;
        this.attendsService = attendsService;
    }

    public Boolean getHasServiceRequest() {
        return hasServiceRequest;
    }

    public Boolean getOwnsService() {
        return ownsService;
    }

    public Boolean getAttendsService() {
        return attendsService;
    }
}
