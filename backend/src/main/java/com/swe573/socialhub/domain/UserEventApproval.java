package com.swe573.socialhub.domain;

import com.swe573.socialhub.domain.key.UserEventApprovalKey;
import com.swe573.socialhub.enums.ApprovalStatus;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "approved")
    private Date approvedDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "denied")
    private Date deniedDate;

    public Date getApprovedDate() {
        return approvedDate;
    }

    public Date getDeniedDate() {
        return deniedDate;
    }

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    private Date created;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public UserEventApproval(UserEventApprovalKey id, User user, Event event, ApprovalStatus approvalStatus) {
        this.id = id;
        this.user = user;
        this.event = event;
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
        synchronizeApprovalStatus();
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
