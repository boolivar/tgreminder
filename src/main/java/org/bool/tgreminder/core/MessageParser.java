package org.bool.tgreminder.core;

import org.apache.commons.lang3.StringUtils;
import org.bool.tgreminder.dto.ReminderDto;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class MessageParser {

    public ReminderDto parse(String text) {
        String[] parts = StringUtils.splitByWholeSeparator(text, " ", 3);
        if (parts == null || parts.length < 1) {
            return instantMessage("Empty request");
        }
        
        if ("/start".equals(parts[0])) {
            return instantMessage("Hello!");
        }
        
        if ("/remind".equals(parts[0])) {
            if (parts.length != 3) {
                return instantMessage("Invalid request");
            }
            
            try {
                return new ReminderDto(OffsetDateTime.parse(parts[1]), parts[2]);
            } catch (RuntimeException e) {
                return instantMessage("Invalid request param");
            }
        }
        
        return instantMessage("Unknown command: " + parts[0]);
    }
    
    private ReminderDto instantMessage(String text) {
        return new ReminderDto(OffsetDateTime.now(), text);
    }
}
