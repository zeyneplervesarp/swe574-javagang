package com.swe573.socialhub.dto;

import com.swe573.socialhub.domain.Badge;
import com.swe573.socialhub.enums.UserType;

import java.util.Date;
import java.util.List;

public class UserDto {
    Long id;
    private String username;
    private String email;
    private String bio;
    private String password;
    private List<TagDto> userTags;
    private Integer balance;
    private List<NotificationDto> notifications;
    private int balanceOnHold;
    private  String latitude;
    private  String longitude;
    private String formattedAddress;
    private List<String> followedBy;
    private List<String>  following;
    private List<TagDto> tags;
    private RatingSummaryDto ratingSummary;
    private UserType userType;
    private long flagCount;
    private int reputationPoint;
    private List<BadgeDto> badges;
    private Long createdTimestamp;

    public Long getCreatedTimestamp() {
        return createdTimestamp;
    }

    public UserDto(Long id, String username, String email, String bio, Integer balance, List<NotificationDto> notifications, int balanceOnHold, String latitude, String longitude, String formattedAddress, List<String> followedBy, List<String> following, List<TagDto> tags, RatingSummaryDto ratingSummary, UserType userType, long flagCount, int reputationPoint, List<BadgeDto> badges, Date created) {
        this.createdTimestamp = created.toInstant().toEpochMilli();
        this.id = id;
        this.ratingSummary = ratingSummary;
        this.username = username;
        this.email = email;
        this.bio = bio;
        this.balance = balance;
        this.notifications = notifications;
        this.balanceOnHold = balanceOnHold;
        this.latitude = latitude;
        this.longitude = longitude;
        this.formattedAddress = formattedAddress;
        this.followedBy = followedBy;
        this.following = following;
        this.tags = tags;
        this.userType = userType;
        this.flagCount = flagCount;
        this.reputationPoint = reputationPoint;
        this.badges = badges;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<TagDto> getUserTags() {
        return userTags;
    }

    public void setUserTags(List<TagDto> userTags) {
        this.userTags = userTags;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public List<NotificationDto> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<NotificationDto> notifications) {
        this.notifications = notifications;
    }

    public int getBalanceOnHold() {
        return balanceOnHold;
    }

    public void setBalanceOnHold(int balanceOnHold) {
        this.balanceOnHold = balanceOnHold;
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

    public List<String> getFollowedBy() {
        return followedBy;
    }

    public void setFollowedBy(List<String> followedBy) {
        this.followedBy = followedBy;
    }

    public List<String> getFollowing() {
        return following;
    }

    public void setFollowing(List<String> following) {
        this.following = following;
    }

    public List<TagDto> getTags() {
        return tags;
    }

    public void setTags(List<TagDto> tags) {
        this.tags = tags;
    }

    public RatingSummaryDto getRatingSummary() {
        return ratingSummary;
    }

    public void setRatingSummary(RatingSummaryDto ratingSummary) {
        this.ratingSummary = ratingSummary;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public long getFlagCount() {
        return flagCount;
    }

    public void setFlagCount(long flagCount) {
        this.flagCount = flagCount;
    }

    public int getReputationPoint() {
        return reputationPoint;
    }

    public void setReputationPoint(int reputationPoint) {
        this.reputationPoint = reputationPoint;
    }

    public List<BadgeDto> getBadges() {
        return badges;
    }

    public void setBadges(List<BadgeDto> badges) {
        this.badges = badges;
    }
}
