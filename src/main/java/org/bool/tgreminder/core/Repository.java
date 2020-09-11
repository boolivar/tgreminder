package org.bool.tgreminder.core;

import org.bool.tgreminder.dto.ReminderDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.List;

@Component
public class Repository {

    private final JdbcTemplate jdbcTemplate;
    
    public Repository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public List<ReminderDto> findByUserId(Long userId) {
        return jdbcTemplate.query("select 1", this::mapDto);
    }
    
    private ReminderDto mapDto(ResultSet rs, int index) {
        return new ReminderDto();
    }
}
