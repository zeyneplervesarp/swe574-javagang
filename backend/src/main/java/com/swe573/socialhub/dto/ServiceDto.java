package com.swe573.socialhub.dto;

import com.swe573.socialhub.enums.LocationType;
import com.swe573.socialhub.enums.ServiceStatus;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ServiceDto implements Serializable {
    private Long id;
    private String header;
    private String description;
    private LocationType locationType;
    private String location;
    private LocalDateTime time;
    private int hours;
    private int quota;
    private Long attendingUserCount;
    private Long createdUserId;
    private String createdUserName;
    private Double latitude;
    private Double longitude;
    private List<TagDto> serviceTags;
    private ServiceStatus status;
    private String timeString;
    private Boolean showServiceOverButton;
    private Long pendingUserCount;
    private String distanceToUserString;
    private Double distanceToUser;
    private List<UserDto> participantUserList;
    private RatingSummaryDto ratingSummary;
    private Long flagCount;
    private String imageUrl;
    private Boolean isFeatured;
    private Long createdTimestamp;

    public ServiceDto() {
    }

    public ServiceDto(Long id, String header, String description, LocationType locationType, String location, LocalDateTime time, int hours, int quota, long attendingUserCount, Long createdUserId, String createdUserName, Double latitude, Double longitude, List<TagDto> serviceTags, ServiceStatus status, Long pendingUserCount, Double distanceToUser, List<UserDto> participantUserList, RatingSummaryDto ratingSummary, Long flagCount,  String imageUrl, Boolean isFeatured, Date createdTimestamp) {
        this.id = id;
        this.header = header;
        this.description = description;
        this.locationType = locationType;
        this.location = location;
        this.time = time;
        this.hours = hours;
        this.quota = quota;
        this.attendingUserCount = attendingUserCount;
        this.createdUserId = createdUserId;
        this.createdUserName = createdUserName;
        this.latitude = latitude;
        this.ratingSummary = ratingSummary;
        this.longitude = longitude;
        this.serviceTags = serviceTags;
        this.status = status;
        this.pendingUserCount = pendingUserCount;

        this.distanceToUser = distanceToUser;
        this.participantUserList = participantUserList;
        this.flagCount = flagCount;
        this.imageUrl = imageUrl;
        this.isFeatured = isFeatured;
        if (this.distanceToUser != null && this.distanceToUser != 0)
        {
            DecimalFormat df = new DecimalFormat("0.00");
            distanceToUserString = df.format(this.distanceToUser) + " KM";
        }
        else
        {
            distanceToUserString = "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm");

        String formattedDateTime = time.format(formatter); // "1986-04-08 12:30"
        this.timeString = formattedDateTime;
        this.showServiceOverButton = time.isBefore(LocalDateTime.now()) ;
        if(createdTimestamp != null)
        {
            this.createdTimestamp = createdTimestamp.toInstant().toEpochMilli();

        }

    }

    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public Long getId() {
        return id;
    }

    public String getHeader() {
        return header;
    }

    public String getDescription() {
        return description;
    }

    public com.swe573.socialhub.enums.LocationType getLocationType() {
        return locationType;
    }

    public String getLocation() {
        return location;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getQuota() {
        return quota;
    }

    public Long getCreatedUserIdId() {
        return createdUserId;
    }

    public String getCreatedUserName() {
        return createdUserName;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public String getTimeString() {
        return timeString;
    }

    public Boolean getShowServiceOverButton() {
        return showServiceOverButton;
    }

    public Long getPendingUserCount() {
        return pendingUserCount;
    }


    public String getDistanceToUserString() {
        return distanceToUserString;
    }

    public Double getDistanceToUser() {
        return distanceToUser;
    }

    public Long getFlagCount() {
        return flagCount;
    }

    public void setLocationType(com.swe573.socialhub.enums.LocationType locationType) {
        this.locationType = locationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServiceDto entity = (ServiceDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.header, entity.header) &&
                Objects.equals(this.description, entity.description) &&
                Objects.equals(this.location, entity.location) &&
                Objects.equals(this.time, entity.time) &&
                Objects.equals(this.hours, entity.hours) &&
                Objects.equals(this.quota, entity.quota) &&
                Objects.equals(this.createdUserId, entity.createdUserId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, header, description, location, time, hours, quota, createdUserId);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "Header = " + header + ", " +
                "Description = " + description + ", " +
                "Location = " + location + ", " +
                "Time = " + time + ", " +
                "Minutes = " + hours + ", " +
                "Quota = " + quota + ", " +
                "CreatedUserId = " + createdUserId + ")";
    }


    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public List<TagDto> getServiceTags() {
        return serviceTags;
    }

    public Long getAttendingUserCount() {
        return attendingUserCount;
    }

    public List<UserDto> getParticipantUserList() {
        return participantUserList;
    }

    public RatingSummaryDto getRatingSummary() {
        return ratingSummary;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Boolean getFeatured() {
        return isFeatured;
    }
}
