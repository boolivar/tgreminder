package org.bool.tgreminder.core.handlers;

import org.apache.commons.lang3.StringUtils;
import org.bool.tgreminder.core.CommandHandler;
import org.bool.tgreminder.core.ReminderFactory;
import org.bool.tgreminder.core.UserRepository;
import org.bool.tgreminder.dto.ReminderDto;
import org.bool.tgreminder.dto.UserDto;
import org.bool.tgreminder.i18n.Messages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.ZoneId;
import java.util.Objects;

@Component
public class TimezoneHandler implements CommandHandler {

    private final ReminderFactory reminderFactory;
    
    private final UserRepository userRepository;
    
    private final String defaultTimeZone;
    
    @Autowired
    public TimezoneHandler(ReminderFactory reminderFactory, UserRepository userRepository, Clock clock) {
        this(reminderFactory, userRepository, clock.getZone().toString());
    }
    
    public TimezoneHandler(ReminderFactory reminderFactory, UserRepository userRepository, String defaultTimeZone) {
        this.reminderFactory = reminderFactory;
        this.userRepository = userRepository;
        this.defaultTimeZone = defaultTimeZone;
    }
    
    @Override
    public ReminderDto handle(Integer userId, Long chatId, String[] args) {
        if ("/timezone".equals(args[0])) {
            UserDto user = args.length > 1
                    ? updateTimeZone(userId, ZoneId.of(args[1]))
                    : userRepository.findById(userId);
            return reminderFactory.instantMessage(Messages.TIMEZONE_UPDATED, Objects.toString(user.getTimeZone(), defaultTimeZone));
        }
        return null;
    }
    
    private UserDto updateTimeZone(Integer userId, ZoneId zoneId) {
        UserDto user = new UserDto(userId, null, zoneId.toString());
        userRepository.updateUser(user);
        return user;
    }
}
