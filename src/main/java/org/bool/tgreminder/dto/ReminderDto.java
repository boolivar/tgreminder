package org.bool.tgreminder.dto;

import java.time.OffsetDateTime;

public class ReminderDto {
    
    private Integer chatIndex;
    
    private OffsetDateTime time;
    
    private String message;
    
    public ReminderDto() {
    }
    
    public ReminderDto(Integer chatIndex, OffsetDateTime time, String message) {
        this.chatIndex = chatIndex;
        this.time = time;
        this.message = message;
    }
    
    public Integer getChatIndex() {
        return chatIndex;
    }

    public void setChatIndex(Integer chatIndex) {
        this.chatIndex = chatIndex;
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
