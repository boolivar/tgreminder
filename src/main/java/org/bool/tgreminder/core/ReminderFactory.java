package org.bool.tgreminder.core;

import org.bool.tgreminder.dto.ReminderDto;
import org.bool.tgreminder.i18n.Messages;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.OffsetDateTime;

@Component
public class ReminderFactory {
    
    private final MessageSource messageSource;
    
    private final Clock clock;
    
    public ReminderFactory(MessageSource messageSource, Clock clock) {
        this.messageSource = messageSource;
        this.clock = clock;
    }
    
    public ReminderDto instantMessage(Messages message, String... args) {
        String text = messageSource.getMessage(message.name(), args, null);
        return instantMessage(text);
    }
    
    public ReminderDto instantMessage(String text) {
        return message(OffsetDateTime.now(clock), text);
    }
    
    public ReminderDto message(OffsetDateTime time, String message) {
        return new ReminderDto(null, time, message);
    }
}
