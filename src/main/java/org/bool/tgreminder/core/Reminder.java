package org.bool.tgreminder.core;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class Reminder {
    
    private static final Logger log = LoggerFactory.getLogger(Reminder.class);
    
    private final TelegramBot telegramBot;

    private final TaskScheduler scheduler;

    public Reminder(TelegramBot telegramBot, TaskScheduler scheduler) {
        this.telegramBot = telegramBot;
        this.scheduler = scheduler;
    }

    public boolean remind() {
        return true;
    }

    public void remind(Long id, String message) {
        scheduler.schedule(() -> send(id, message), Instant.now().plusSeconds(5));
    }
    
    private void send(Long chatId, String message) {
        SendResponse response = telegramBot.execute(new SendMessage(chatId, message));
        if (!response.isOk()) {
            log.error("Error sending message to {}: {} {} {}",
                    chatId, response.errorCode(), response.description(), response.parameters());
        }
    }
}
