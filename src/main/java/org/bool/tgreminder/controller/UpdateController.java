package org.bool.tgreminder.controller;

import com.pengrad.telegrambot.model.Update;

import org.bool.tgreminder.service.UpdateService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/update")
public class UpdateController {
    
    private final UpdateService updateService;
    
    public UpdateController(UpdateService updateService) {
        this.updateService = updateService;
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void update(@RequestParam("key") String key, @RequestBody Update update) {
        updateService.update(key, update);
    }
}
