package com.swe573.socialhub.dto;

import java.util.Map;

public class DailyStatsDto {
    private final Map<String, Long> createdServices;
    private final Map<String, Long> serviceApplications;
    private final Map<String, Long> approvedServiceApplications;
    private final Map<String, Long> registeredUsers;

    public DailyStatsDto(Map<String, Long> createdServices, Map<String, Long> serviceApplications, Map<String, Long> approvedServiceApplications, Map<String, Long> registeredUsers) {
        this.createdServices = createdServices;
        this.serviceApplications = serviceApplications;
        this.approvedServiceApplications = approvedServiceApplications;
        this.registeredUsers = registeredUsers;
    }

    public Map<String, Long> getCreatedServices() {
        return createdServices;
    }

    public Map<String, Long> getServiceApplications() {
        return serviceApplications;
    }

    public Map<String, Long> getApprovedServiceApplications() {
        return approvedServiceApplications;
    }

    public Map<String, Long> getRegisteredUsers() {
        return registeredUsers;
    }
}
