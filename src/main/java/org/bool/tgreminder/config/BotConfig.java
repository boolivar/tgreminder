package org.bool.tgreminder.config;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SetWebhook;
import com.pengrad.telegrambot.response.BaseResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bool.tgreminder.core.TelegramBotToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfig {
    
    private static final Logger log = LoggerFactory.getLogger(BotConfig.class);
    
    private static final String[] UPDATE_TYPES = { "message", "channel_post", "inline_query", "callback_query" };
    
    @Value("${telegram.bot.webhook:}")
    private String webhook;
    
    @Bean
    public TelegramBot telegramBot(TelegramBotToken token) {
        return new TelegramBot(token.getValue());
    }
    
    @Autowired
    public void configureUpdates(TelegramBot telegramBot, UpdatesListener listener) {
        if (StringUtils.isNotBlank(webhook)) {
            BaseResponse response = telegramBot.execute(new SetWebhook().allowedUpdates(UPDATE_TYPES).url(webhook));
            Validate.validState(response.isOk(), "Webhook %s registration error: %s", webhook, response);
            log.info("Webhook {} registered: {}", webhook, response);
        } else {
            telegramBot.setUpdatesListener(listener);
        }
    }
}
