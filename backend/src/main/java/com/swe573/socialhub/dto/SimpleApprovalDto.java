package com.swe573.socialhub.dto;

public class SimpleApprovalDto {
    private Long userId;
    private Long serviceId;

    public SimpleApprovalDto(Long userId, Long serviceId) {
        this.userId = userId;
        this.serviceId = serviceId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
}
