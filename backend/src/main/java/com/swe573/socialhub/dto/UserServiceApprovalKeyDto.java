package com.swe573.socialhub.dto;

import java.io.Serializable;
import java.util.Objects;

public class UserServiceApprovalKeyDto implements Serializable {
    private final Long userId;
    private final Long serviceId;

    public UserServiceApprovalKeyDto(Long userId, Long serviceId) {
        this.userId = userId;
        this.serviceId = serviceId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserServiceApprovalKeyDto entity = (UserServiceApprovalKeyDto) o;
        return Objects.equals(this.userId, entity.userId) &&
                Objects.equals(this.serviceId, entity.serviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, serviceId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "userId = " + userId + ", " +
                "serviceId = " + serviceId + ")";
    }
}
