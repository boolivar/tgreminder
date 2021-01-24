package org.bool.tgreminder.core;

import org.bool.tgreminder.dto.ReminderDto;

public interface CommandHandler {
    ReminderDto handle(Integer userId, Long chatId, String[] args);
}
