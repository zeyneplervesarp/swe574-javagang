package com.swe573.socialhub.dto;

import com.swe573.socialhub.enums.ServiceStatus;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class EventDto implements Serializable {
    private final Long id;
    private final String Header;
    private final String Description;
    private final String Location;
    private final LocalDateTime Time;
    private final int Minutes;
    private final int quota;
    private final Long AttendingUserCount;
    private final Long CreatedUserIdId;
    private final String CreatedUserName;
    private final Double Latitude;
    private final Double Longitude;
    private final List<TagDto> EventTags;
    private final ServiceStatus Status;
    private final String TimeString;
    private final Boolean ShowEventOverButton;
    private final Long PendingUserCount;
    private final String DistanceToUserString;
    private final Double DistanceToUser;
    private final List<UserDto> ParticipantUserList;
    private final long flagCount;

    public EventDto(Long id, String header, String description, String location, LocalDateTime time, int minutes, int quota, long attendingUserCount, Long createdUserIdId, String createdUserName, Double latitude, Double longitude, List<TagDto> eventTags, ServiceStatus status, Long pendingUserCount, Double distanceToUser, List<UserDto> participantUserList, long flagCount) {
        this.id = id;
        this.quota = quota;
        Header = header;
        Description = description;
        Location = location;
        Time = time;
        Minutes = minutes;
        AttendingUserCount = attendingUserCount;
        CreatedUserIdId = createdUserIdId;
        CreatedUserName = createdUserName;
        Latitude = latitude;
        Longitude = longitude;
        EventTags = eventTags;
        Status = status;
        PendingUserCount = pendingUserCount;

        DistanceToUser = distanceToUser;
        ParticipantUserList = participantUserList;
        this.flagCount = flagCount;
        if (DistanceToUser != null && DistanceToUser != 0)
        {
            DecimalFormat df = new DecimalFormat("0.00");
            DistanceToUserString = df.format(DistanceToUser) + " KM";
        }
        else
        {
            DistanceToUserString = "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm");

        TimeString = time.format(formatter);
        ShowEventOverButton = time.isBefore(LocalDateTime.now()) ;
    }

    public int getQuota() {
        return quota;
    }

    public Long getId() {
        return id;
    }

    public String getHeader() {
        return Header;
    }

    public String getDescription() {
        return Description;
    }

    public String getLocation() {
        return Location;
    }

    public LocalDateTime getTime() {
        return Time;
    }

    public int getMinutes() {
        return Minutes;
    }

    public Long getCreatedUserIdId() {
        return CreatedUserIdId;
    }

    public String getCreatedUserName() {
        return CreatedUserName;
    }

    public ServiceStatus getStatus() {
        return Status;
    }

    public String getTimeString() {
        return TimeString;
    }

    public Long getPendingUserCount() {
        return PendingUserCount;
    }

    public String getDistanceToUserString() {
        return DistanceToUserString;
    }

    public Double getDistanceToUser() {
        return DistanceToUser;
    }

    public Double getLatitude() {
        return Latitude;
    }

    public Double getLongitude() {
        return Longitude;
    }

    public Long getAttendingUserCount() {
        return AttendingUserCount;
    }

    public List<UserDto> getParticipantUserList() {
        return ParticipantUserList;
    }

    public List<TagDto> getEventTags() {
        return EventTags;
    }

    public Boolean getShowEventOverButton() {
        return ShowEventOverButton;
    }

    public long getFlagCount() {
        return flagCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventDto eventDto = (EventDto) o;
        return Minutes == eventDto.Minutes && Objects.equals(id, eventDto.id) && Objects.equals(Header, eventDto.Header) && Objects.equals(Description, eventDto.Description) && Objects.equals(Location, eventDto.Location) && Objects.equals(Time, eventDto.Time) && Objects.equals(AttendingUserCount, eventDto.AttendingUserCount) && Objects.equals(CreatedUserIdId, eventDto.CreatedUserIdId) && Objects.equals(CreatedUserName, eventDto.CreatedUserName) && Objects.equals(Latitude, eventDto.Latitude) && Objects.equals(Longitude, eventDto.Longitude) && Objects.equals(EventTags, eventDto.EventTags) && Status == eventDto.Status && Objects.equals(TimeString, eventDto.TimeString) && Objects.equals(ShowEventOverButton, eventDto.ShowEventOverButton) && Objects.equals(PendingUserCount, eventDto.PendingUserCount) && Objects.equals(DistanceToUserString, eventDto.DistanceToUserString) && Objects.equals(DistanceToUser, eventDto.DistanceToUser) && Objects.equals(ParticipantUserList, eventDto.ParticipantUserList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, Header, Description, Location, Time, Minutes, AttendingUserCount, CreatedUserIdId, CreatedUserName, Latitude, Longitude, EventTags, Status, TimeString, ShowEventOverButton, PendingUserCount, DistanceToUserString, DistanceToUser, ParticipantUserList);
    }

    @Override
    public String toString() {
        return "EventDto{" +
                "id=" + id +
                ", Header='" + Header + '\'' +
                ", Description='" + Description + '\'' +
                ", Location='" + Location + '\'' +
                ", Time=" + Time +
                ", Minutes=" + Minutes +
                ", AttendingUserCount=" + AttendingUserCount +
                ", CreatedUserIdId=" + CreatedUserIdId +
                ", CreatedUserName='" + CreatedUserName + '\'' +
                ", Latitude=" + Latitude +
                ", Longitude=" + Longitude +
                ", EventTags=" + EventTags +
                ", Status=" + Status +
                ", TimeString='" + TimeString + '\'' +
                ", ShowEventOverButton=" + ShowEventOverButton +
                ", PendingUserCount=" + PendingUserCount +
                ", DistanceToUserString='" + DistanceToUserString + '\'' +
                ", DistanceToUser=" + DistanceToUser +
                ", ParticipantUserList=" + ParticipantUserList +
                '}';
    }
}
