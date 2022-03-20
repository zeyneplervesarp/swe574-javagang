package com.swe573.socialhub.dto;

public class UserEventDto {

    private Boolean hasEventRequest;
    private Boolean ownsEvent;
    private Boolean attendsEvent;

    public UserEventDto(Boolean hasEventRequest, Boolean ownsEvent, Boolean attendsEvent) {
        this.hasEventRequest = hasEventRequest;
        this.ownsEvent = ownsEvent;
        this.attendsEvent = attendsEvent;
    }

    public Boolean getHasEventRequest() {
        return hasEventRequest;
    }

    public Boolean getOwnsEvent() {
        return ownsEvent;
    }

    public Boolean getAttendsEvent() {
        return attendsEvent;
    }
}
