package org.bool.tgreminder.core;

import org.bool.tgreminder.dto.ReminderDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.function.BiConsumer;

@Component
public class Repository {

    private final JdbcTemplate jdbcTemplate;
    
    public Repository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public void queryByTime(OffsetDateTime time, BiConsumer<Long, String> handler) {
        jdbcTemplate.query("select * from REMINDERS where TIME = ?)", (ResultSet rs) -> handler.accept(rs.getLong("USER_ID"), rs.getString("MESSAGE")), Timestamp.from(time.toInstant()));
    }
    
    public List<ReminderDto> findNext(OffsetDateTime time) {
        return jdbcTemplate.query("select * from REMINDERS where TIME = (select MIN(TIME) from REMINDERS where TIME > ?)", this::mapDto, Timestamp.from(time.toInstant()));
    }
    
    public List<ReminderDto> findByUserId(Long userId) {
        return jdbcTemplate.query("select * from REMINDERS where USER_ID = ?", this::mapDto, userId);
    }
    
    public void store(Long userId, String message, OffsetDateTime time) {
        jdbcTemplate.update("insert into REMINDERS(ID, USER_ID, MESSAGE, TIME) values(nextval('REMINDERS_SEQ'), ?, ?, ?)",
                userId, message, Timestamp.from(time.toInstant()));
    }
    
    private ReminderDto mapDto(ResultSet rs, int index) throws SQLException {
        return new ReminderDto(OffsetDateTime.ofInstant(rs.getTimestamp("TIME").toInstant(), ZoneOffset.UTC), rs.getString("MESSAGE"));
    }
}
