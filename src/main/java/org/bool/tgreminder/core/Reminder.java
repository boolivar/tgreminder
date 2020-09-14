package org.bool.tgreminder.core;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;

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
    
    public void remind(Long id, String message, OffsetDateTime time) {
        repository.store(id, message, time);
        ScheduledFuture<?> future = scheduler.schedule(() -> repository.queryByTime(time, this::send), time.toInstant());
        ScheduledFuture<?> old = ref.getAndAccumulate(future, ObjectUtils::min);
        if (old != null) {
            ObjectUtils.max(old, future).cancel(false);
        }
    }
    
    private void send(Long chatId, String message) {
        SendResponse response = telegramBot.execute(new SendMessage(chatId, message));
        if (!response.isOk()) {
            log.error("Error sending message to {}: {} {} {}",
                    chatId, response.errorCode(), response.description(), response.parameters());
        }
    }
}
