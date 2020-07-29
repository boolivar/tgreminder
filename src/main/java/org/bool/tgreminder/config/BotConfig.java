package org.bool.tgreminder.config;

import com.pengrad.telegrambot.TelegramBot;

import org.bool.tgreminder.core.TelegramBotToken;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {

    @Bean
    public TelegramBot telegramBot(TelegramBotToken token) {
        return new TelegramBot(token.getValue());
    }
}
