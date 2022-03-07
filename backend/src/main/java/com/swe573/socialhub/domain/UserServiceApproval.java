package com.swe573.socialhub.domain;

import com.swe573.socialhub.domain.key.UserServiceApprovalKey;
import com.swe573.socialhub.enums.ApprovalStatus;

import javax.persistence.*;

@Entity
public class UserServiceApproval {
    @EmbeddedId
    UserServiceApprovalKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @MapsId("serviceId")
    @JoinColumn(name = "service_id")
    Service service;

    ApprovalStatus approvalStatus;

    public UserServiceApproval(UserServiceApprovalKey id, User user, Service service, ApprovalStatus approvalStatus) {
        this.id = id;
        this.user = user;
        this.service = service;
        this.approvalStatus = approvalStatus;
    }

    public UserServiceApproval() {

    }

    // standard constructors, getters, and setters

    public UserServiceApprovalKey getId() {
        return id;
    }

    public void setId(UserServiceApprovalKey id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    @Override
    public String toString() {
        return "UserServiceApproval{" +
                "user=" + user.getUsername() +
                ", service=" + service.getHeader() +
                ", approvalStatus=" + approvalStatus +
                '}';
    }
}
