package com.swe573.socialhub.dto;

import com.swe573.socialhub.enums.ApprovalStatus;

import java.io.Serializable;
import java.util.Objects;

public class UserServiceApprovalDto implements Serializable {
    private final UserDto user;
    private final ServiceDto service;
    private final ApprovalStatus approvalStatus;

    public UserServiceApprovalDto(UserDto user, ServiceDto service, ApprovalStatus approvalStatus) {
        this.user = user;
        this.service = service;
        this.approvalStatus = approvalStatus;
    }

    public UserDto getUser() {
        return user;
    }

    public ServiceDto getService() {
        return service;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserServiceApprovalDto entity = (UserServiceApprovalDto) o;
        return Objects.equals(this.user, entity.user) &&
                Objects.equals(this.service, entity.service) &&
                Objects.equals(this.approvalStatus, entity.approvalStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, service, approvalStatus);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "user = " + user + ", " +
                "service = " + service + ", " +
                "approvalStatus = " + approvalStatus + ")";
    }
}
