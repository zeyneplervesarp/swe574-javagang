package com.swe573.socialhub.domain;

import com.swe573.socialhub.enums.ServiceStatus;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Event {

    public Event() {

    }

    public Event(Long id, String header, String description, String location, LocalDateTime time, int minutes, int quota, int attendingUserCount, User createdUser, Double latitude, Double longitude, Set<Tag> serviceTags) {
        this.id = id;
        this.header = header;
        this.description = description;
        this.location = location;
        this.time = time;
        this.quota = quota;
        this.createdUser = createdUser;
        this.latitude = latitude;
        this.longitude = longitude;
        this.attendingUserCount = attendingUserCount;
        this.eventTags = serviceTags;
        this.status = ServiceStatus.ONGOING;
        this.minutes = minutes;
    }

    private @Id
    @GeneratedValue
    Long id;
    private String header;
    private String description;
    private String location;
    private LocalDateTime time;
    private int minutes;
    private int quota;
    private int attendingUserCount;
    private Double latitude;
    private Double longitude;
    private ServiceStatus status;
    @ManyToOne
    @JoinColumn(name = "createdUser")
    User createdUser;
    @ManyToMany(cascade = { CascadeType.MERGE })
    @JoinTable(
            name = "event_tags",
            joinColumns = { @JoinColumn(name = "event_id") },
            inverseJoinColumns = { @JoinColumn(name = "tag_id") }
    )
    Set<Tag> eventTags;
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    Set<UserEventApproval> approvalSet;

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

    public User getCreatedUser() {
        return createdUser;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void setCreatedUser(User createdUser) {
        this.createdUser = createdUser;
    }

    public int getQuota() {
        return quota;
    }

    public void setQuota(int quota) {
        this.quota = quota;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Set<Tag> getEventTags() {
        return eventTags;
    }

    public void setEventTags(Set<Tag> serviceTags) {
        this.eventTags = serviceTags;
    }

    public int getAttendingUserCount() {
        return attendingUserCount;
    }

    public void setAttendingUserCount(int attendingUserCount) {
        this.attendingUserCount = attendingUserCount;
    }

    public Set<UserEventApproval> getApprovalSet() {
        return approvalSet;
    }

    public void setApprovalSet(Set<UserEventApproval> approvalSet) {
        this.approvalSet = approvalSet;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }


    public void addTag(Tag tag) {
        if (this.eventTags == null)
        {
            this.eventTags =  new HashSet<Tag>();
        }
        this.eventTags.add(tag);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", header='" + header + '\'' +
                ", description='" + description + '\'' +
                ", location='" + location + '\'' +
                ", time=" + time +
                ", quota=" + quota +
                ", attendingUserCount=" + attendingUserCount +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", status=" + status +
                ", createdUser=" + createdUser +
                ", eventTags=" + eventTags +
                ", approvalSet=" + approvalSet +
                '}';
    }
}
