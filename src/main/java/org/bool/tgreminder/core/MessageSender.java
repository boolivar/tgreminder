package org.bool.tgreminder.core;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {
    
    private static final Logger log = LoggerFactory.getLogger(MessageSender.class);
    
    private final TelegramBot telegramBot;
    
    public MessageSender(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }
    
    public void sendMessage(Long chatId, String message) {
        SendResponse response = telegramBot.execute(new SendMessage(chatId, message));
        if (!response.isOk()) {
            log.error("Error sending message to {}: {} {} {}",
                    chatId, response.errorCode(), response.description(), response.parameters());
        }
    }
}
