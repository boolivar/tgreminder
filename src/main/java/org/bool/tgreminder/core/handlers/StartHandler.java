package org.bool.tgreminder.core.handlers;

import org.bool.tgreminder.core.CommandHandler;
import org.bool.tgreminder.core.ReminderFactory;
import org.bool.tgreminder.dto.ReminderDto;
import org.bool.tgreminder.i18n.Messages;
import org.springframework.stereotype.Component;

@Component
public class StartHandler implements CommandHandler {

    private final ReminderFactory reminderFactory;
    
    public StartHandler(ReminderFactory reminderFactory) {
        this.reminderFactory = reminderFactory;
    }
    
    @Override
    public ReminderDto handle(Long chatId, String[] args) {
        if ("/start".equals(args[0])) {
            return reminderFactory.instantMessage(Messages.HELLO);
        }
        return null;
    }
}