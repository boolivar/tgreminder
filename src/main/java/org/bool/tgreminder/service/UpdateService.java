package org.bool.tgreminder.service;

import com.pengrad.telegrambot.model.Update;

import org.bool.tgreminder.core.MessageParser;
import org.bool.tgreminder.core.Reminder;
import org.bool.tgreminder.core.UpdateToken;
import org.bool.tgreminder.dto.ReminderDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UpdateService {
    
    private static final Logger log = LoggerFactory.getLogger(UpdateService.class);

    private final MessageParser messageParser;
    
    private final Reminder reminder;
    
    private final UpdateToken updateToken;
    
    public UpdateService(MessageParser messageParser, Reminder reminder, UpdateToken updateToken) {
        this.messageParser = messageParser;
        this.reminder = reminder;
        this.updateToken = updateToken;
    }
    
    public void update(String key, Update update) {
        log.info("Update {} {}", key, update);
        if (updateToken.getValue().equals(key)) {
            ReminderDto message = messageParser.parse(update.message().text());
            reminder.remind(update.message().chat().id(), message.getMessage(), message.getTime());
        } else {
            log.warn("Invalid key: {}", key);
        }
    }
}
