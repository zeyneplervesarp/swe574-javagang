package com.swe573.socialhub.domain;

import com.swe573.socialhub.domain.key.UserEventApprovalKey;
import com.swe573.socialhub.enums.ApprovalStatus;

import javax.persistence.*;

@Entity
public class UserEventApproval {
    @EmbeddedId
    UserEventApprovalKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    Event event;

    ApprovalStatus approvalStatus;

    public UserEventApproval(UserEventApprovalKey id, User user, Event event, ApprovalStatus approvalStatus) {
        this.id = id;
        this.user = user;
        this.event = event;
        this.approvalStatus = approvalStatus;
    }

    public UserEventApproval() {

    }

    // standard constructors, getters, and setters

    public UserEventApprovalKey getId() {
        return id;
    }

    public void setId(UserEventApprovalKey id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }


    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }


    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    @Override
    public String toString() {
        return "UserEventApproval{" +
                "id=" + id +
                ", user=" + user +
                ", event=" + event +
                ", approvalStatus=" + approvalStatus +
                '}';
    }
}
