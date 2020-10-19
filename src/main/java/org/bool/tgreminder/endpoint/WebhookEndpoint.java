package org.bool.tgreminder.endpoint;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.DeleteWebhook;
import com.pengrad.telegrambot.response.BaseResponse;

import org.bool.tgreminder.core.UpdateToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.DeleteOperation;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.stereotype.Component;

@Endpoint(id = "webhook")
@Component
public class WebhookEndpoint {

    private static final Logger log = LoggerFactory.getLogger(WebhookEndpoint.class); 
    
    private final TelegramBot telegramBot;
    
    private final UpdateToken token;

    public WebhookEndpoint(TelegramBot telegramBot, UpdateToken token) {
        this.telegramBot = telegramBot;
        this.token = token;
    }
    
    @DeleteOperation
    public void deleteWebhook(String key) {
        if (token.getValue().equals(key)) {
            BaseResponse response = telegramBot.execute(new DeleteWebhook());
            log.info("Remove webhook result: {}", response);
        } else {
            log.warn("Invalid key to remove webhook: {}", key);
        }
    }
}
