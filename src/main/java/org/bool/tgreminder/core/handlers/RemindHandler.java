package org.bool.tgreminder.core.handlers;

import java.time.ZoneId;

import org.bool.tgreminder.core.CommandHandler;
import org.bool.tgreminder.core.ReminderFactory;
import org.bool.tgreminder.core.ReminderParser;
import org.bool.tgreminder.core.UserRepository;
import org.bool.tgreminder.dto.ReminderDto;
import org.bool.tgreminder.i18n.Messages;
import org.springframework.stereotype.Component;

@Component
public class RemindHandler implements CommandHandler {

    private final UserRepository userRepository;
    
    private final ReminderParser reminderParser;
    
    private final ReminderFactory reminderFactory;
    
    public RemindHandler(UserRepository userRepository, ReminderParser reminderParser, ReminderFactory reminderFactory) {
        this.userRepository = userRepository;
        this.reminderParser = reminderParser;
        this.reminderFactory = reminderFactory;
    }
    
    @Override
    public ReminderDto handle(Integer userId, Long chatId, String[] parts) {
        if ("/remind".equals(parts[0])) {
            if (parts.length > 1) {
                String userTimeZone = userRepository.findById(userId).getTimeZone();
                ZoneId zoneId = reminderFactory.zoneId(userTimeZone);
                return reminderParser.parse(parts[1], zoneId);
            }
            return reminderFactory.instantMessage(Messages.INVALID_REQUEST);
        }
        return null;
    }
}
