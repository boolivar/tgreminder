package org.bool.tgreminder.core;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;

import javax.annotation.PostConstruct;

@Component
public class Reminder {
    
    private static final Logger log = LoggerFactory.getLogger(Reminder.class);
    
    private final ReminderScheduler scheduler;
    
    private final Repository repository;
    
    private final TelegramBot telegramBot;
    
    public Reminder(ReminderScheduler scheduler, Repository repository, TelegramBot telegramBot) {
        this.scheduler = scheduler;
        this.repository = repository;
        this.telegramBot = telegramBot;
    }
    
    @PostConstruct
    public void reschedule() {
        reschedule(OffsetDateTime.now());
    }
    
    public void remind(Long id, String message, OffsetDateTime time) {
        repository.store(id, message, time);
        scheduler.schedule(this::remind, time);
    }
    
    private void remind(OffsetDateTime time) {
        repository.queryByTime(time, this::send);
        reschedule(time);
    }
    
    private void reschedule(OffsetDateTime time) {
        Optional<OffsetDateTime> next = repository.findNext(time);
        if (next.isPresent()) {
            scheduler.reset(this::remind, time);
        }
        log.info("Reschedule to {}", next);
    }
    
    private void send(Long chatId, String message) {
        SendResponse response = telegramBot.execute(new SendMessage(chatId, message));
        if (!response.isOk()) {
            log.error("Error sending message to {}: {} {} {}",
                    chatId, response.errorCode(), response.description(), response.parameters());
        }
    }
}
