package org.bool.tgreminder.service;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import org.bool.tgreminder.core.UpdateToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UpdateListener implements UpdatesListener {

    private final UpdateService updateService;
    
    private final UpdateToken updateToken;
    
    public UpdateListener(UpdateService updateService, UpdateToken updateToken) {
        this.updateService = updateService;
        this.updateToken = updateToken;
    }
    
    @Override
    public int process(List<Update> updates) {
        for (Update update : updates) {
            updateService.update(updateToken.getValue(), update);
        }
        return CONFIRMED_UPDATES_ALL;
    }
}
