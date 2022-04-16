package com.swe573.socialhub.domain;

import com.swe573.socialhub.enums.LoginAttemptType;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
public class LoginAttempt {
    @Id
    @GeneratedValue
    private Long id;

    private String username;

    private LoginAttemptType attemptType;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    private Date created;

    public LoginAttempt(Long id, String username, LoginAttemptType attemptType, Date created) {
        this.id = id;
        this.username = username;
        this.attemptType = attemptType;
        this.created = created;
    }

    public LoginAttempt() {

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

    public LoginAttemptType getAttemptType() {
        return attemptType;
    }

    public void setAttemptType(LoginAttemptType attemptType) {
        this.attemptType = attemptType;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
