package com.swe573.socialhub.domain;


import javax.persistence.*;

@Entity
public class Notification {
    private @Id
    @GeneratedValue
    Long id;
    private String message;
    private Boolean readByUser;
    private String messageUrl;
    @ManyToOne
    @JoinColumn(name = "receiver")
    User receiver;

    public User getUser() {
        return receiver;
    }

    public void setUser(User user) {
        this.receiver = user;
    }

    public Notification(Long id, String message, String messageUrl, Boolean read, User user) {
        this.id = id;
        this.message = message;
        this.readByUser = read;
        this.messageUrl = messageUrl;
        this.receiver = user;
    }

    public Notification() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getRead() {
        return readByUser;
    }

    public void setRead(Boolean read) {
        this.readByUser = read;
    }

    public String getMessageUrl() {
        return messageUrl;
    }

    public void setMessageUrl(String messageUrl) {
        this.messageUrl = messageUrl;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", message='" + message + '\'' +
                '}';
    }
}
