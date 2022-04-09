package com.swe573.socialhub.dto;

import com.swe573.socialhub.enums.ApprovalStatus;

import java.io.Serializable;
import java.util.Objects;

public class UserEventApprovalDto implements Serializable {
    private final UserDto user;
    private final EventDto event;
    private final ApprovalStatus approvalStatus;

    public UserEventApprovalDto(UserDto user, EventDto event, ApprovalStatus approvalStatus) {
        this.user = user;
        this.event = event;
        this.approvalStatus = approvalStatus;
    }

    public UserDto getUser() {
        return user;
    }

    public EventDto getEvent() {
        return event;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserEventApprovalDto that = (UserEventApprovalDto) o;
        return Objects.equals(user, that.user) && Objects.equals(event, that.event) && approvalStatus == that.approvalStatus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, event, approvalStatus);
    }

    @Override
    public String toString() {
        return "UserEventApprovalDto{" +
                "user=" + user +
                ", event=" + event +
                ", approvalStatus=" + approvalStatus +
                '}';
    }
}
