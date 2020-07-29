package org.bool.tgreminder.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UpdateListener implements UpdatesListener {

    private final UpdateService updateService;
    
    public UpdateListener(UpdateService updateService) {
        this.updateService = updateService;
    }
    
    @Autowired
    public void register(TelegramBot telegramBot) {
        telegramBot.setUpdatesListener(this);
    }
    
    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            updateService.update(null, update);
        }
        return CONFIRMED_UPDATES_ALL;
    }
}
