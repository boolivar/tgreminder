package org.bool.tgreminder.core.handlers;

import org.bool.tgreminder.core.CommandHandler;
import org.bool.tgreminder.core.ReminderFactory;
import org.bool.tgreminder.dto.ReminderDto;
import org.bool.tgreminder.i18n.Messages;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class TimezoneHandler implements CommandHandler {

    private final ReminderFactory reminderFactory;
    
    public TimezoneHandler(ReminderFactory reminderFactory) {
        this.reminderFactory = reminderFactory;
    }
    
    @Override
    public ReminderDto handle(Long chatId, String[] args) {
        if ("/timezone".equals(args[0])) {
            if (args.length > 2) {
                return reminderFactory.instantMessage(Messages.INVALID_REQUEST);
            }
            ZoneId timeZone = args.length > 1 ? ZoneId.of(args[1]) : ZoneId.systemDefault();
            return reminderFactory.instantMessage(Messages.TIMEZONE_UPDATED, timeZone.toString());
        }
        return null;
    }
}
