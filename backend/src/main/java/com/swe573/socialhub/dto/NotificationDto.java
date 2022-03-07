package com.swe573.socialhub.dto;

import java.io.Serializable;
import java.util.Objects;

public class NotificationDto implements Serializable {
    private final Long id;
    private final String message;
    private final String messageBody;
    private final Boolean read;

    public NotificationDto(Long id, String message,String messageBody, Boolean read) {
        this.id = id;
        this.message = message;
        this.messageBody = messageBody;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationDto entity = (NotificationDto) o;
        return Objects.equals(this.id, entity.id) &&
                Objects.equals(this.message, entity.message) &&
                Objects.equals(this.read, entity.read);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, message, read);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "message = " + message + ", " +
                "read = " + read + ")";
    }
}
