package org.bool.tgreminder.core;

import org.apache.commons.lang3.StringUtils;
import org.bool.tgreminder.dto.ReminderDto;
import org.bool.tgreminder.i18n.MessageResolver;
import org.bool.tgreminder.i18n.Messages;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.OffsetDateTime;

@Component
public class MessageParser {
    
	private final DateTimeParser dateTimeParser;
    
    private final MessageResolver messageResolver;
    
    private final Clock clock;
    
    public MessageParser(DateTimeParser dateTimeParser, MessageResolver messageResolver, Clock clock) {
        this.dateTimeParser = dateTimeParser;
        this.messageResolver = messageResolver;
        this.clock = clock;
    }
    
    public ReminderDto parse(String text) {
        String[] parts = StringUtils.splitByWholeSeparator(text, " ", 3);
        if (parts == null || parts.length < 1) {
            return instantMessage(Messages.EMPTY_REQUEST);
        }
        
        if ("/start".equals(parts[0])) {
            return instantMessage(Messages.HELLO);
        }
        
        if ("/remind".equals(parts[0])) {
            if (parts.length != 3) {
                return instantMessage(Messages.INVALID_REQUEST);
            }
            
            try {
                return new ReminderDto(dateTimeParser.parse(parts[1]), parts[2]);
            } catch (RuntimeException e) {
                return instantMessage(Messages.INVALID_TIME_FORMAT);
            }
        }
        
        return instantMessage(Messages.UNKNOWN_COMMAND);
    }
    
    private ReminderDto instantMessage(Messages message, String... args) {
        String text = messageResolver.getMessage(message, args);
        return new ReminderDto(OffsetDateTime.now(clock), text);
    }
}
