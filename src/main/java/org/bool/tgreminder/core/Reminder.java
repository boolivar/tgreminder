package org.bool.tgreminder.core;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

import javax.annotation.PostConstruct;

@Component
public class Reminder {
    
    private static final Logger log = LoggerFactory.getLogger(Reminder.class);
    
    private static final Duration THRESHOLD = Duration.ofMinutes(5);
    
    private final ReminderScheduler scheduler;
    
    private final Repository repository;
    
    private final TelegramBot telegramBot;
    
    private final Clock clock;
    
    public Reminder(ReminderScheduler scheduler, Repository repository, TelegramBot telegramBot, Clock clock) {
        this.scheduler = scheduler;
        this.repository = repository;
        this.telegramBot = telegramBot;
        this.clock = clock;
    }
    
    @PostConstruct
    public void reschedule() {
        reschedule(OffsetDateTime.now(clock));
    }
    
    public void remind(Long id, String message, OffsetDateTime time) {
        time = time.truncatedTo(ChronoUnit.MINUTES);
        if (time.isAfter(OffsetDateTime.now(clock).plus(THRESHOLD))) {
            schedule(id, message, time);
        } else {
            send(id, message);
        }
    }
    
    private void schedule(Long id, String message, OffsetDateTime time) {
        repository.store(id, message, time);
        scheduler.schedule(this::remind, time);
    }
    
    private void remind(OffsetDateTime time) {
        repository.queryByTime(time, this::send);
        reschedule(time);
    }
    
    private void reschedule(OffsetDateTime time) {
        OffsetDateTime next = repository.findNext(time).orElse(null);
        if (next != null) {
            scheduler.reset(this::remind, next);
        } else {
            scheduler.reset();
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
