package org.bool.tgreminder.core.handlers;

import org.apache.commons.lang3.StringUtils;
import org.bool.tgreminder.core.CommandHandler;
import org.bool.tgreminder.core.ReminderFactory;
import org.bool.tgreminder.core.Repository;
import org.bool.tgreminder.dto.ReminderDto;
import org.bool.tgreminder.i18n.Messages;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ListHandler implements CommandHandler {

    private final ReminderFactory reminderFactory;
    
    private final Repository repository;
    
    public ListHandler(ReminderFactory reminderFactory, Repository repository) {
        this.reminderFactory = reminderFactory;
        this.repository = repository;
    }
    
    @Override
    public ReminderDto handle(Long chatId, String[] args) {
        if ("/list".equals(args[0])) {
            List<ReminderDto> reminders = repository.findByChatId(chatId, reminderFactory.now());
            if (reminders.isEmpty()) {
                return reminderFactory.instantMessage(Messages.EMPTY_LIST); 
            }
            String message = reminders.stream()
                    .map(r -> StringUtils.joinWith(" ", r.getChatIndex(), r.getTime(), StringUtils.abbreviate(r.getMessage(), 16)))
                    .collect(Collectors.joining("\n"));
            return reminderFactory.instantMessage(message);
        }
        return null;
    }
}
