package com.swe573.socialhub.domain;

import com.swe573.socialhub.enums.UserType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class User {
    private @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    private String username;
    private String email;
    private String bio;
    private String latitude;
    private String longitude;
    private String formattedAddress;
    @ManyToMany(cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "user_tags",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")}
    )
    private Set<Tag> userTags;
    @OneToMany(mappedBy = "createdUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Service> createdServices;

    @OneToMany(mappedBy = "createdUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Event> createdEvents;
    @OneToMany(mappedBy = "user")
    Set<UserServiceApproval> serviceApprovalSet;

    @OneToMany(mappedBy = "user")
    Set<UserEventApproval> eventApprovalSet;
    private Integer balance;
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Notification> notificationSet;
    @OneToMany(mappedBy = "followingUser", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<UserFollowing> followingUsers;
    @OneToMany(mappedBy = "followedUser", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<UserFollowing> followedBy;

    @OneToMany(mappedBy = "rater")
    private Set<Rating> ratings;
    private UserType userType;
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Badge> badges;

    private int reputationPoint;

    public User(Long id, String username, String email, String bio, Set<Tag> userTags, Integer balance, String latitude, String longitude, String formattedAddress, UserType userType, int reputationPoint) {
        this.id = id;
        this.bio = bio;
        this.username = username;
        this.email = email;
        this.latitude = latitude;
        this.longitude = longitude;
        this.formattedAddress = formattedAddress;
        this.userTags = userTags;
        this.balance = balance;
        this.userType = userType;
        this.reputationPoint = reputationPoint;
    }

    public User() {

    }

    public Set<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(Set<Rating> ratings) {
        this.ratings = ratings;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public Set<Tag> getTags() {
        if (this.userTags == null) {
            this.userTags = new HashSet<Tag>();
        }
        return userTags;
    }

    public void setTags(Set<Tag> userTags) {
        this.userTags = userTags;
    }

    public void addTag(Tag tag) {
        if (this.userTags == null) {
            this.userTags = new HashSet<Tag>();
        }
        this.userTags.add(tag);
    }

    public Set<Service> getCreatedServices() {
        if (this.createdServices == null)
        {
            this.createdServices = new HashSet<>();
        }
        return createdServices;
    }

    public void setCreatedServices(Set<Service> createdServices) {

        this.createdServices = createdServices;
    }

    public Set<Event> getCreatedEvents() {
        return createdEvents;
    }

    public void setCreatedEvents(Set<Event> createdEvents) {
        this.createdEvents = createdEvents;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Set<Tag> getUserTags() {
        return userTags;
    }

    public void setUserTags(Set<Tag> userTags) {
        this.userTags = userTags;
    }

    public Set<UserServiceApproval> getServiceApprovalSet() {
        return serviceApprovalSet;
    }

    public void setServiceApprovalSet(Set<UserServiceApproval> approvalSet) {
        this.serviceApprovalSet = approvalSet;
    }

    public Set<UserEventApproval> getEventApprovalSet() {
        return eventApprovalSet;
    }

    public void setEventApprovalSet(Set<UserEventApproval> eventApprovalSet) {
        this.eventApprovalSet = eventApprovalSet;
    }

    public Set<Notification> getNotificationSet() {
        return notificationSet;
    }

    public void setNotificationSet(Set<Notification> notificationSet) {
        if (this.notificationSet == null) {
            this.notificationSet = new HashSet<Notification>();
        }
        this.notificationSet = notificationSet;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public Set<UserFollowing> getFollowingUsers() {
        if (followingUsers == null)
            setFollowingUsers(new HashSet<>());
        return followingUsers;
    }

    public void setFollowingUsers(Set<UserFollowing> followingUsers) {
        this.followingUsers = followingUsers;
    }

    public Set<UserFollowing> getFollowedBy() {
        if (followedBy == null)
            setFollowedBy(new HashSet<>());
        return followedBy;
    }

    public void setFollowedBy(Set<UserFollowing> followedBy) {
        this.followedBy = followedBy;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public int getReputationPoint() {
        return reputationPoint;
    }

    public void setReputationPoint(int reputationPoint) {
        this.reputationPoint = reputationPoint;
    }

    public Set<Badge> getBadges() {
        return badges;
    }

    public void setBadges(Set<Badge> badges) {
        this.badges = badges;
    }

    @Override
    public String toString() {
        return "User{" + "id=" + this.id + ", username='" + this.username + '\'' + '}';
    }

    public void removeBadge(Badge badge) {

        this.badges.remove(badge);

    }

    public void addBadge(Badge badge) {
        if (this.badges == null)
        {
            this.badges =  new HashSet<>();
        }
        this.badges.add(badge);
    }
}
