package org.bool.tgreminder.core.handlers;

import org.bool.tgreminder.core.CommandHandler;
import org.bool.tgreminder.core.ReminderFactory;
import org.bool.tgreminder.core.UserRepository;
import org.bool.tgreminder.dto.ReminderDto;
import org.bool.tgreminder.dto.UserDto;
import org.bool.tgreminder.i18n.Messages;
import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class TimezoneHandler implements CommandHandler {

    private final ReminderFactory reminderFactory;
    
    private final UserRepository userRepository;
    
    public TimezoneHandler(ReminderFactory reminderFactory, UserRepository userRepository) {
        this.reminderFactory = reminderFactory;
        this.userRepository = userRepository;
    }
    
    @Override
    public ReminderDto handle(Integer userId, Long chatId, String[] args) {
        if ("/timezone".equals(args[0])) {
            if (args.length > 2) {
                return reminderFactory.instantMessage(Messages.INVALID_REQUEST);
            }
            UserDto user = args.length > 1
                    ? updateTimeZone(userId, ZoneId.of(args[1]))
                    : userRepository.findById(userId);
            return reminderFactory.instantMessage(Messages.TIMEZONE_UPDATED, user.getTimeZone());
        }
        return null;
    }
    
    private UserDto updateTimeZone(Integer userId, ZoneId zoneId) {
        UserDto user = new UserDto(userId, null, zoneId.toString());
        userRepository.updateUser(user);
        return user;
    }
}
