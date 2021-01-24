package org.bool.tgreminder.core;

import org.apache.commons.lang3.StringUtils;
import org.bool.tgreminder.dto.ReminderDto;
import org.bool.tgreminder.i18n.Messages;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class MessageParser {
    
    private final ReminderFactory reminderFactory;
    
    private final List<? extends CommandHandler> handlers;
    
    public MessageParser(ReminderFactory reminderFactory, List<? extends CommandHandler> handlers) {
        this.reminderFactory = reminderFactory;
        this.handlers = handlers;
    }
    
    public ReminderDto parse(Integer userId, Long chatId, String text) {
        String[] parts = StringUtils.splitByWholeSeparator(text, " ", 3);
        if (parts == null || parts.length < 1) {
            return reminderFactory.instantMessage(Messages.EMPTY_REQUEST);
        }
        return handlers.stream()
                .map(handler -> handler.handle(userId, chatId, parts))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseGet(() -> reminderFactory.instantMessage(Messages.UNKNOWN_COMMAND));
    }
}
