package org.bool.tgreminder.service;

import org.bool.tgreminder.core.Reminder;
import org.bool.tgreminder.core.Repository;
import org.bool.tgreminder.dto.ReminderDto;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class RemindService {

    private final Reminder reminder;
    
    private final Repository repository;
    
    private final Clock clock;
    
    public RemindService(Reminder reminder, Repository repository, Clock clock) {
        this.reminder = reminder;
        this.repository = repository;
        this.clock = clock;
    }
    
    public void remind(Long userId, Long chatId, String message, OffsetDateTime time) {
        reminder.remind(userId, chatId, message, time);
    }

    public List<ReminderDto> list(Long userId, Long chatId) {
        return repository.findByChatId(chatId, OffsetDateTime.now(clock));
    }

    public void cancel(Long userId, Long chatId, Integer chatIndex) {
        if (chatIndex != null) {
            repository.delete(userId, chatId, chatIndex);
        } else {
            repository.deleteAll(userId, chatId);
        }
    }
}
