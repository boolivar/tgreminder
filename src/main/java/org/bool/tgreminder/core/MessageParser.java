package org.bool.tgreminder.core;

import org.apache.commons.lang3.StringUtils;
import org.bool.tgreminder.dto.ReminderDto;
import org.bool.tgreminder.i18n.Messages;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageParser {
    
    private final ReminderFactory reminderFactory;
    
    private final DateTimeParser dateTimeParser;
    
    private final Repository repository;
    
    private final Clock clock;
    
    public MessageParser(ReminderFactory reminderFactory, DateTimeParser dateTimeParser, Repository repository, Clock clock) {
        this.reminderFactory = reminderFactory;
        this.dateTimeParser = dateTimeParser;
        this.repository = repository;
        this.clock = clock;
    }
    
    public ReminderDto parse(Long chatId, String text) {
        String[] parts = StringUtils.splitByWholeSeparator(text, " ", 3);
        if (parts == null || parts.length < 1) {
            return reminderFactory.instantMessage(Messages.EMPTY_REQUEST);
        }
        
        if ("/start".equals(parts[0])) {
            return reminderFactory.instantMessage(Messages.HELLO);
        }
        
        if ("/list".equals(parts[0])) {
            List<ReminderDto> reminders = repository.findByChatId(chatId, OffsetDateTime.now(clock));
            if (reminders.isEmpty()) {
                return reminderFactory.instantMessage(Messages.EMPTY_LIST); 
            }
            String message = reminders.stream()
                    .map(r -> StringUtils.joinWith(" ", r.getChatIndex(), r.getTime(), StringUtils.abbreviate(r.getMessage(), 16)))
                    .collect(Collectors.joining("\n"));
            return reminderFactory.instantMessage(message);
        }
        
        if ("/remind".equals(parts[0])) {
            if (parts.length != 3) {
                return reminderFactory.instantMessage(Messages.INVALID_REQUEST);
            }
            
            try {
                return new ReminderDto(null, dateTimeParser.parse(parts[1]), parts[2]);
            } catch (RuntimeException e) {
                return reminderFactory.instantMessage(Messages.INVALID_TIME_FORMAT);
            }
        }
        
        return reminderFactory.instantMessage(Messages.UNKNOWN_COMMAND);
    }
}
