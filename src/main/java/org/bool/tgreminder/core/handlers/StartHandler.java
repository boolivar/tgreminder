package org.bool.tgreminder.core.handlers;

import org.bool.tgreminder.core.CommandHandler;
import org.bool.tgreminder.core.ReminderFactory;
import org.bool.tgreminder.core.UserRepository;
import org.bool.tgreminder.dto.ReminderDto;
import org.bool.tgreminder.dto.UserDto;
import org.bool.tgreminder.i18n.Messages;
import org.springframework.stereotype.Component;

@Component
public class StartHandler implements CommandHandler {

    private final ReminderFactory reminderFactory;
    
    private final UserRepository userRepository;
    
    public StartHandler(ReminderFactory reminderFactory, UserRepository userRepository) {
        this.reminderFactory = reminderFactory;
        this.userRepository = userRepository;
    }
    
    @Override
    public ReminderDto handle(Integer userId, Long chatId, String[] args) {
        if ("/start".equals(args[0])) {
            UserDto user = userRepository.findById(userId);
            if (user == null) {
                userRepository.insertUser(new UserDto(userId, null, null));
            }
            return reminderFactory.instantMessage(Messages.HELLO);
        }
        return null;
    }
}