package org.bool.tgreminder.core;

import org.apache.commons.lang3.StringUtils;
import org.bool.tgreminder.dto.ReminderDto;
import org.bool.tgreminder.i18n.MessageResolver;
import org.bool.tgreminder.i18n.Messages;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MessageParser {
    
	private final DateTimeParser dateTimeParser;
    
    private final MessageResolver messageResolver;
    
    private final Repository repository;
    
    private final Clock clock;
    
    public MessageParser(DateTimeParser dateTimeParser, MessageResolver messageResolver, Repository repository, Clock clock) {
        this.dateTimeParser = dateTimeParser;
        this.messageResolver = messageResolver;
        this.repository = repository;
        this.clock = clock;
    }
    
    public ReminderDto parse(Long chatId, String text) {
        String[] parts = StringUtils.splitByWholeSeparator(text, " ", 3);
        if (parts == null || parts.length < 1) {
            return instantMessage(Messages.EMPTY_REQUEST);
        }
        
        if ("/start".equals(parts[0])) {
            return instantMessage(Messages.HELLO);
        }
        
        if ("/list".equals(parts[0])) {
            List<ReminderDto> reminders = repository.findByChatId(chatId, OffsetDateTime.now(clock));
            return reminders.isEmpty() ? instantMessage(Messages.EMPTY_LIST) : instantMessage(reminders.stream()
                    .map(r -> StringUtils.joinWith(" ", r.getChatIndex(), r.getTime(), StringUtils.abbreviate(r.getMessage(), 16)))
                    .collect(Collectors.joining("\n")));
        }
        
        if ("/remind".equals(parts[0])) {
            if (parts.length != 3) {
                return instantMessage(Messages.INVALID_REQUEST);
            }
            
            try {
                return new ReminderDto(null, dateTimeParser.parse(parts[1]), parts[2]);
            } catch (RuntimeException e) {
                return instantMessage(Messages.INVALID_TIME_FORMAT);
            }
        }
        
        return instantMessage(Messages.UNKNOWN_COMMAND);
    }
    
    private ReminderDto instantMessage(Messages message, String... args) {
        String text = messageResolver.getMessage(message, args);
        return instantMessage(text);
    }
    
    private ReminderDto instantMessage(String text) {
        return new ReminderDto(null, OffsetDateTime.now(clock), text);
    }
}
