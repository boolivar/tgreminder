package org.bool.tgreminder.config;

import com.pengrad.telegrambot.TelegramBot;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntegrationTestConfig {

    @MockBean
    private TelegramBot telegramBot;
}
