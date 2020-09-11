package org.bool.tgreminder.service;

import org.bool.tgreminder.core.Repository;
import org.bool.tgreminder.dto.ReminderDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RemindService {
    
    private final Repository repository;
    
    public RemindService(Repository repository) {
        this.repository = repository;
    }

    public void remind(Long userId, String request) {
    }

    public List<ReminderDto> list(Long userId) {
        return repository.findByUserId(userId);
    }

    public void cancel(Long userId, Integer index) {
    }

    public void cancelAll(Long userId) {
    }
}
