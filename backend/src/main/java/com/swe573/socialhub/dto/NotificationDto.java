package com.swe573.socialhub.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class NotificationDto implements Serializable, Comparable<NotificationDto> {
    private final Long id;
    private final String message;
    private final String messageBody;
    private final LocalDateTime sentDate;
    private final Boolean read;

    public NotificationDto(Long id, String message,String messageBody, Boolean read, LocalDateTime sentDate) {
        this.id = id;
        this.message = message;
        this.messageBody = messageBody;
        this.sentDate = sentDate;
        this.read = read;
    }

    public Long getId() {
        return id;
    }

    public Boolean getRead() {
        return read;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public LocalDateTime getSentDate() {
        return sentDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationDto entity = (NotificationDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.message, entity.message) &&
                Objects.equals(this.read, entity.read) &&
                Objects.equals(this.sentDate, entity.sentDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, read, sentDate);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "message = " + message + ", " +
                "read = " + read + ", " +
                "sentDate = " + sentDate + ")";
    }

    @Override
    public int compareTo(NotificationDto notificationDto) {
        if(notificationDto.getSentDate().isBefore(this.sentDate)) {
            return -1;
        } else if(notificationDto.getSentDate().isAfter(this.sentDate)) {
            return 1;
        } else {
            return 0;
        }
    }
}
