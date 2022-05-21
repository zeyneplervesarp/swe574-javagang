package com.swe573.socialhub.domain;

import com.swe573.socialhub.domain.key.UserServiceApprovalKey;
import com.swe573.socialhub.enums.ApprovalStatus;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(indexes = {
        @Index(columnList = "created"),
        @Index(columnList = "approved"),
        @Index(columnList = "denied"),
})
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "approved")
    private Date approvedDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "denied")
    private Date deniedDate;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    private Date created;

    public Date getCreated() {
        return created;
    }

    public Date getApprovedDate() {
        return approvedDate;
    }

    public Date getDeniedDate() {
        return deniedDate;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setApprovedDate(Date approvedDate) {
        this.approvedDate = approvedDate;
    }

    public void setDeniedDate(Date deniedDate) {
        this.deniedDate = deniedDate;
    }

    public UserServiceApproval(UserServiceApprovalKey id, User user, Service service, ApprovalStatus approvalStatus) {
        this.id = id;
        this.user = user;
        this.service = service;
        this.approvalStatus = approvalStatus;
        synchronizeApprovalStatus();
    }

    private void synchronizeApprovalStatus() {
        if (approvalStatus == null) return;
        switch (approvalStatus) {
            case PENDING:
                break;
            case APPROVED:
                this.approvedDate = new Date();
                break;
            case DENIED:
                this.deniedDate = new Date();
                break;
        }
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
        synchronizeApprovalStatus();
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
