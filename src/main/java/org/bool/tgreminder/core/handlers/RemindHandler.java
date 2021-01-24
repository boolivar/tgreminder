package org.bool.tgreminder.core.handlers;

import org.bool.tgreminder.core.CommandHandler;
import org.bool.tgreminder.core.DateTimeParser;
import org.bool.tgreminder.core.ReminderFactory;
import org.bool.tgreminder.dto.ReminderDto;
import org.bool.tgreminder.i18n.Messages;
import org.springframework.stereotype.Component;

@Component
public class RemindHandler implements CommandHandler {

    private final ReminderFactory reminderFactory;
    
    private final DateTimeParser dateTimeParser;
    
    public RemindHandler(ReminderFactory reminderFactory, DateTimeParser dateTimeParser) {
        this.reminderFactory = reminderFactory;
        this.dateTimeParser = dateTimeParser;
    }
    
    @Override
    public ReminderDto handle(Integer userId, Long chatId, String[] parts) {
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
        return null;
    }
}
