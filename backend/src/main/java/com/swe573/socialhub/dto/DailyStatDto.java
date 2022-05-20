package com.swe573.socialhub.dto;

import java.util.Map;

public class DailyStatDto {
    private final Map<String, Integer> createdServices;
    private final Map<String, Integer> serviceApplications;
    private final Map<String, Integer> approvedServiceApplications;
    private final Map<String, Integer> registeredUsers;

    public DailyStatDto(Map<String, Integer> createdServices, Map<String, Integer> serviceApplications, Map<String, Integer> approvedServiceApplications, Map<String, Integer> registeredUsers) {
        this.createdServices = createdServices;
        this.serviceApplications = serviceApplications;
        this.approvedServiceApplications = approvedServiceApplications;
        this.registeredUsers = registeredUsers;
    }

    public Map<String, Integer> getCreatedServices() {
        return createdServices;
    }

    public Map<String, Integer> getServiceApplications() {
        return serviceApplications;
    }

    public Map<String, Integer> getApprovedServiceApplications() {
        return approvedServiceApplications;
    }

    public Map<String, Integer> getRegisteredUsers() {
        return registeredUsers;
    }
}
