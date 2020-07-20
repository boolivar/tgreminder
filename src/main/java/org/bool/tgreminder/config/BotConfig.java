package org.bool.tgreminder.config;

import com.pengrad.telegrambot.TelegramBot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("telegram-bot")
public class BotConfig {

    private String token;

    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(token);
    }
}
