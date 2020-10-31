package org.bool.tgreminder.controller;

import org.bool.tgreminder.dto.ReminderDto;
import org.bool.tgreminder.service.RemindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("api/remind")
@ResponseStatus(HttpStatus.OK)
public class RemindController {
    
    @Autowired
    private RemindService remindService;
    
    @GetMapping("{userId}/{chatId}")
    public List<ReminderDto> list(@PathVariable("userId") Long userId, @PathVariable("chatId") Long chatId) {
        return remindService.list(userId, chatId);
    }
    
    @PostMapping("{userId}/{chatId}")
    public void remind(@PathVariable("userId") Long userId, @PathVariable("chatId") Long chatId, @RequestParam("time") @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime time, @RequestBody String request) {
        remindService.remind(userId, chatId, request, time);
    }
    
    @DeleteMapping("{userId}/{chatId}/{index}")
    public void cancel(@PathVariable("userId") Long userId, @PathVariable("chatId") Long chatId, @PathVariable(name = "index", required = false) Integer index) {
        remindService.cancel(userId, chatId, index);
    }
}
