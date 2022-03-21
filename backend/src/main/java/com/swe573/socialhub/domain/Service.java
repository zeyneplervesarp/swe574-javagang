package com.swe573.socialhub.domain;

import com.swe573.socialhub.enums.ServiceStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Service {

    public Service() {

    }

    public Service(Long id, String header, String description, String location, LocalDateTime time, int minutes, int quota, int attendingUserCount, User createdUser, Double latitude, Double longitude, Set<Tag> serviceTags) {
        this.id = id;
        this.header = header;
        this.description = description;
        this.location = location;
        this.time = time;
        credit = minutes;
        this.quota = quota;
        this.createdUser = createdUser;
        this.latitude = latitude;
        this.longitude = longitude;
        this.attendingUserCount = attendingUserCount;
        this.serviceTags = serviceTags;
        this.status = ServiceStatus.ONGOING;
    }

    private @Id
    @GeneratedValue
    Long id;
    private String header;
    private String description;
    private String location;
    private LocalDateTime time;
    private int credit;
    private int quota;
    private int attendingUserCount;
    private Double latitude;
    private Double longitude;
    private ServiceStatus status;

    @OneToMany(mappedBy="service")
    private Set<Rating> ratings;


    @ManyToOne
    @JoinColumn(name = "createdUser")
    User createdUser;
    @ManyToMany(cascade = { CascadeType.MERGE })
    @JoinTable(
            name = "service_tags",
            joinColumns = { @JoinColumn(name = "service_id") },
            inverseJoinColumns = { @JoinColumn(name = "tag_id") }
    )
    Set<Tag> serviceTags;
    @OneToMany(mappedBy = "service")
    Set<UserServiceApproval> approvalSet;




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

    public int getCredit() {
        return this.credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
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

    public Set<Tag> getServiceTags() {
        return serviceTags;
    }

    public void setServiceTags(Set<Tag> serviceTags) {
        this.serviceTags = serviceTags;
    }

    public int getAttendingUserCount() {
        return attendingUserCount;
    }

    public void setAttendingUserCount(int attendingUserCount) {
        this.attendingUserCount = attendingUserCount;
    }

    public Set<UserServiceApproval> getApprovalSet() {
        return approvalSet;
    }

    public void setApprovalSet(Set<UserServiceApproval> approvalSet) {
        this.approvalSet = approvalSet;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public Set<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(Set<Rating> ratings) {
        this.ratings = ratings;
    }

    @Override
    public String toString() {
        return "Service{" + "id=" + this.id + ", header='" + this.header + '\'' + '}';
    }

    public void addTag(Tag tag) {
        if (this.serviceTags == null)
        {
            this.serviceTags =  new HashSet<Tag>();
        }
        this.serviceTags.add(tag);
    }
}
