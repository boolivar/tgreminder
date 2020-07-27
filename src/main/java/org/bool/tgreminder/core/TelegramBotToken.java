package org.bool.tgreminder.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TelegramBotToken {
    
    private final String value;

    public TelegramBotToken(@Value("${telegram-bot.token:}") String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
