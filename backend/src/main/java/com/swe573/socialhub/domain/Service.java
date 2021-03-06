package com.swe573.socialhub.domain;

import com.swe573.socialhub.enums.LocationType;
import com.swe573.socialhub.enums.ServiceStatus;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(indexes = {
        @Index(columnList = "created")
})
public class Service {

    public Service() {

    }
    // constructor for physical services
    private Service(Long id, String header, String description, String location, LocalDateTime time, int hours, int quota, int attendingUserCount, User createdUser, Double latitude, Double longitude, String imageUrl, Set<Tag> serviceTags) {
        this.id = id;
        this.header = header;
        this.description = description;
        this.imageUrl = imageUrl;
        this.locationType = LocationType.Physical;
        this.location = location;
        this.time = time;
        this.credit = hours;
        this.quota = quota;
        this.createdUser = createdUser;
        this.latitude = latitude;
        this.longitude = longitude;
        this.attendingUserCount = attendingUserCount;
        this.serviceTags = serviceTags;
        this.status = ServiceStatus.ONGOING;
        this.isFeatured = false;
    }

    // constructor for online services
    private Service(Long id, String header, String description, String location, LocalDateTime time, int hours, int quota, int attendingUserCount, String imageUrl, User createdUser,  Set<Tag> serviceTags) {
        this.id = id;
        this.header = header;
        this.description = description;
        this.imageUrl = imageUrl;
        this.locationType = LocationType.Online;
        this.location = location;
        this.time = time;
        this.credit = hours;
        this.quota = quota;
        this.createdUser = createdUser;
        this.attendingUserCount = attendingUserCount;
        this.serviceTags = serviceTags;
        this.status = ServiceStatus.ONGOING;
        this.isFeatured = false;
    }

    private @Id
    @GeneratedValue
    Long id;
    private String header;
    @Column(columnDefinition = "TEXT")
    private String description;
    private LocationType locationType;
    private String location;
    private LocalDateTime time;
    private int credit;
    private int quota;
    private int attendingUserCount;
    private Double latitude;
    private Double longitude;
    private ServiceStatus status;
    private boolean isFeatured;
    private String imageUrl;

    @OneToMany(mappedBy="service", cascade = CascadeType.ALL)
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
    @OneToMany(mappedBy = "service", cascade = CascadeType.ALL)
    Set<UserServiceApproval> approvalSet;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    private Date created;


    public static Service createOnline(Long id, String header, String description, String location, LocalDateTime time, int hours, int quota, int attendingUserCount, User createdUser, String imageUrl, Set<Tag> serviceTags) {
        return new Service(id, header, description, location, time, hours, quota, attendingUserCount, imageUrl, createdUser, serviceTags);
    }

    public static Service createPhysical(Long id, String header, String description, String location, LocalDateTime time, int hours, int quota, int attendingUserCount, User createdUser, Double latitude, Double longitude, Set<Tag> serviceTags, String imageUrl) {
        return new Service(id, header, description, location, time, hours, quota, attendingUserCount, createdUser, latitude, longitude, imageUrl, serviceTags);
    }

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

    public LocationType getLocationType() {
        return locationType;
    }

    public void setLocationType(LocationType locationType) {
        this.locationType = locationType;
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
        if(this.serviceTags == null)
            this.serviceTags = serviceTags;
        else {
            serviceTags.retainAll(serviceTags);
            this.serviceTags.addAll(serviceTags);
        }
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
        if (this.approvalSet == null)
            this.approvalSet = approvalSet;
        else {
            this.approvalSet.retainAll(approvalSet);
            this.approvalSet.addAll(approvalSet);
        }
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
        if(this.ratings == null) {
            this.ratings = ratings;
        } else {
            ratings.clear();
            this.ratings.addAll(ratings);
        }
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setFeatured(boolean featured) {
        isFeatured = featured;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}