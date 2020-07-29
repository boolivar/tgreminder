package org.bool.tgreminder.core;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class Reminder {
    
    private static final Logger log = LoggerFactory.getLogger(Reminder.class);
    
    private final TelegramBot telegramBot;

    private final SchedulingTaskExecutor taskExecutor;

    public Reminder(TelegramBot telegramBot, SchedulingTaskExecutor taskExecutor) {
        this.telegramBot = telegramBot;
        this.taskExecutor = taskExecutor;
    }

    public boolean remind() {
        return true;
    }

    public void remind(Long id, String message) {
        taskExecutor.execute(() -> send(id, message), TimeUnit.SECONDS.toMillis(5));
    }
    
    private void send(Long chatId, String message) {
        SendResponse response = telegramBot.execute(new SendMessage(chatId, message));
        if (!response.isOk()) {
            log.error("Error sending message to {}: {} {} {}",
                    chatId, response.errorCode(), response.description(), response.parameters());
        }
    }
}
