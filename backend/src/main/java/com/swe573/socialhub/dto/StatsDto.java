package com.swe573.socialhub.dto;

public class StatsDto {
    private final StatContainerDto createdServices;
    private final StatContainerDto serviceApplications;
    private final StatContainerDto approvedServiceApplications;
    private final StatContainerDto registeredUsers;

    public StatsDto(StatContainerDto createdServices, StatContainerDto serviceApplications, StatContainerDto approvedServiceApplications, StatContainerDto registeredUsers) {
        this.createdServices = createdServices;
        this.serviceApplications = serviceApplications;
        this.approvedServiceApplications = approvedServiceApplications;
        this.registeredUsers = registeredUsers;
    }

    public StatContainerDto getCreatedServices() {
        return createdServices;
    }

    public StatContainerDto getServiceApplications() {
        return serviceApplications;
    }

    public StatContainerDto getApprovedServiceApplications() {
        return approvedServiceApplications;
    }

    public StatContainerDto getRegisteredUsers() {
        return registeredUsers;
    }
}
