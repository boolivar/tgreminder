package org.bool.tgreminder.dto;

import java.time.OffsetDateTime;

public class ReminderDto {

    private OffsetDateTime time;
    
    private String message;
    
    public ReminderDto() {
    }
    
    public ReminderDto(OffsetDateTime time, String message) {
        this.time = time;
        this.message = message;
    }

    public OffsetDateTime getTime() {
        return time;
    }

    public void setTime(OffsetDateTime time) {
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
