package org.bool.tgreminder.core;

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
    
    private final MessageSender messageSender;
    
    private final Clock clock;
    
    public Reminder(ReminderScheduler scheduler, Repository repository, MessageSender messageSender, Clock clock) {
        this.scheduler = scheduler;
        this.repository = repository;
        this.messageSender = messageSender;
        this.clock = clock;
    }
    
    @PostConstruct
    public void reschedule() {
        reschedule(OffsetDateTime.now(clock));
    }
    
    public void remind(Long userId, Long chatId, String message, OffsetDateTime time) {
        time = time.truncatedTo(ChronoUnit.MINUTES);
        if (time.isAfter(OffsetDateTime.now(clock).plus(THRESHOLD))) {
            schedule(userId, chatId, message, time);
        } else {
            messageSender.sendMessage(chatId, message);
        }
    }
    
    private void schedule(Long userId, Long chatId, String message, OffsetDateTime time) {
        Long chatIndex = repository.increment(chatId);
        repository.store(userId, chatId, chatIndex, message, time);
        scheduler.schedule(this::remind, time);
    }
    
    private void remind(OffsetDateTime time) {
        repository.queryByTime(time, messageSender::sendMessage);
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
}
