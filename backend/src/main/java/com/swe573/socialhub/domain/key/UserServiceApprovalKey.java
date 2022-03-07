package com.swe573.socialhub.domain.key;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class UserServiceApprovalKey implements Serializable {

    @Column(name = "user_id")
    Long userId;

    @Column(name = "service_id")
    Long serviceId;

    // standard constructors, getters, and setters
    // hashcode and equals implementation

    public UserServiceApprovalKey(Long userId, Long serviceId) {
        this.userId = userId;
        this.serviceId = serviceId;
    }

    public UserServiceApprovalKey() {

    }
}