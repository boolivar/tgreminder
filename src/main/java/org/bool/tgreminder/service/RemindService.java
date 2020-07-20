package org.bool.tgreminder.service;

import org.bool.tgreminder.dto.ReminderDto;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class RemindService {

    public void remind(Long userId, String request) {
    }

    public List<ReminderDto> list(Long userId) {
        return Collections.emptyList();
    }

    public void cancel(Long userId, Integer index) {
    }

    public void cancelAll(Long userId) {
    }
}
