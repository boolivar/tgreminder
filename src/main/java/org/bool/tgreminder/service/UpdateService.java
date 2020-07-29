package org.bool.tgreminder.service;

import com.pengrad.telegrambot.model.Update;

import org.apache.commons.lang3.StringUtils;
import org.bool.tgreminder.core.Reminder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UpdateService {
    
    private static final Logger log = LoggerFactory.getLogger(UpdateService.class);

    private final Reminder reminder;
    
    public UpdateService(Reminder reminder) {
        this.reminder = reminder;
    }
    
    public void update(String key, Update update) {
        log.info("Update {} {}", key, update);
        
        String text = readMessage(update.message().text());
        if (StringUtils.isNotBlank(text)) {
            reminder.remind(update.message().chat().id(), text);
        }
    }
    
    private String readMessage(String text) {
        if (StringUtils.equals(text, "/start")) {
            return "Hello!";
        }
        return StringUtils.substringAfter(text, "/start ");
    }
}
