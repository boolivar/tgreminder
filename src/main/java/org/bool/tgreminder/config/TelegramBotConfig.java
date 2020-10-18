package org.bool.tgreminder.config;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.DeleteWebhook;
import com.pengrad.telegrambot.request.SetWebhook;
import com.pengrad.telegrambot.response.BaseResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.bool.tgreminder.core.UpdateToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class TelegramBotConfig {
    
    private static final Logger log = LoggerFactory.getLogger(TelegramBotConfig.class);
    
    private static final String[] UPDATE_TYPES = { "message", "channel_post", "inline_query", "callback_query" };
    
    @Value("${telegram.bot.webhook:}")
    private String webhook;
    
    @Autowired
    private TelegramBot telegramBot;
    
    @Autowired
    private UpdatesListener updatesListener;
    
    @Autowired
    private UpdateToken token;
    
    @PostConstruct
    public void configureUpdates() {
        if (StringUtils.isNotBlank(webhook)) {
            BaseResponse response = telegramBot.execute(new SetWebhook().allowedUpdates(UPDATE_TYPES).url(webhook + "?key=" + token));
            Validate.validState(response.isOk(), "Webhook %s registration error: %s", webhook, response);
            log.info("Webhook {} registered: {}", webhook, response);
        } else {
            telegramBot.setUpdatesListener(updatesListener);
        }
    }
    
    @PreDestroy
    public void deleteWebhook() {
        BaseResponse response = telegramBot.execute(new DeleteWebhook());
        if (response.isOk()) {
            log.info("Webhook removed: {}", response);
        } else {
            log.error("Error remove webhook: {}", response);
        }
    }
    
    @Bean
    public UUID webhookKey() {
        return UUID.randomUUID();
    }
    
    @Configuration
    public static class BotConfig { 
    
        @Value("${telegram.bot.token:}")
        private String token;
        
        @Bean
        public TelegramBot telegramBot() {
            return new TelegramBot(token);
        }
    }
}
