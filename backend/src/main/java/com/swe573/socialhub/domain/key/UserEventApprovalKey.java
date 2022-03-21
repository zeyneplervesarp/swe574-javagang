package com.swe573.socialhub.domain.key;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserEventApprovalKey implements Serializable {

    @Column(name = "user_id")
    Long userId;

    @Column(name = "event_id")
    Long eventId;

    // standard constructors, getters, and setters
    // hashcode and equals implementation

    public UserEventApprovalKey(Long userId, Long eventId) {
        this.userId = userId;
        this.eventId = eventId;
    }

    public UserEventApprovalKey() {

    }
}
