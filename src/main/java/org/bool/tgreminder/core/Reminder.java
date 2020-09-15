package org.bool.tgreminder.core;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.PostConstruct;

@Component
public class Reminder {
    
    private static final Logger log = LoggerFactory.getLogger(Reminder.class);
    
    private final Repository repository;
    
    private final TaskScheduler scheduler;
    
    private final TelegramBot telegramBot;
    
    private final AtomicReference<ScheduledFuture<?>> ref;
    
    public Reminder(Repository repository, TaskScheduler scheduler, TelegramBot telegramBot) {
        this(repository, scheduler, telegramBot, new AtomicReference<>());
    }
    
    public Reminder(Repository repository, TaskScheduler scheduler, TelegramBot telegramBot, AtomicReference<ScheduledFuture<?>> ref) {
        this.repository = repository;
        this.scheduler = scheduler;
        this.telegramBot = telegramBot;
        this.ref = ref;
    }
    
    @PostConstruct
    public void reschedule() {
        reschedule(OffsetDateTime.now());
    }
    
    public void remind(Long id, String message, OffsetDateTime time) {
        repository.store(id, message, time);
        ScheduledFuture<?> future = schedule(time);
        ScheduledFuture<?> old = ref.getAndAccumulate(future, this::min);
        if (old != null) {
            ScheduledFuture<?> toCancel = max(future, old);
            toCancel.cancel(false);
            if (toCancel != future) {
                log.info("Schedule to {}", time);
            }
        }
    }
    
    private ScheduledFuture<?> schedule(OffsetDateTime time) {
        return scheduler.schedule(() -> remind(time), time.toInstant());
    }
    
    private void reschedule(OffsetDateTime time) {
        Optional<OffsetDateTime> next = repository.findNext(time);
        if (next.isPresent()) {
            ref.set(schedule(next.get()));
        }
        log.info("Reschedule to {}", next);
    }
    
    private void remind(OffsetDateTime time) {
        repository.queryByTime(time, this::send);
        reschedule(time);
    }
    
    private <T extends Comparable<? super T>> T min(T a, T b) {
        return a == null || b.compareTo(a) < 0 ? b : a;
    }
    
    private <T extends Comparable<? super T>> T max(T a, T b) {
        return a == null || b.compareTo(a) > 0 ? b : a;
    }
    
    private void send(Long chatId, String message) {
        SendResponse response = telegramBot.execute(new SendMessage(chatId, message));
        if (!response.isOk()) {
            log.error("Error sending message to {}: {} {} {}",
                    chatId, response.errorCode(), response.description(), response.parameters());
        }
    }
}
