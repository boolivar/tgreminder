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
import java.util.Optional;
import java.util.function.BiConsumer;

@Component
public class Repository {

    private final JdbcTemplate jdbcTemplate;
    
    public Repository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public void queryByTime(OffsetDateTime time, BiConsumer<Long, String> handler) {
        jdbcTemplate.query("select * from REMINDERS where TIME = ?", (ResultSet rs) -> handler.accept(rs.getLong("CHAT_ID"), rs.getString("MESSAGE")), time);
    }
    
    public Optional<OffsetDateTime> findNext(OffsetDateTime time) {
        return Optional.ofNullable(jdbcTemplate.queryForObject("select MIN(TIME) from REMINDERS where TIME > ?", (rs, i) -> convertTime(rs.getTimestamp(1)), time));
    }
    
    public List<ReminderDto> findByChatId(Long chatId, OffsetDateTime time) {
        return jdbcTemplate.query("select * from REMINDERS where CHAT_ID = ? and TIME > ? order by TIME, ID", this::mapDto, chatId, time);
    }
    
    public List<ReminderDto> find(Long userId, Long chatId, OffsetDateTime time) {
        return jdbcTemplate.query("select * from REMINDERS where USER_ID = ? and CHAT_ID = ? and TIME > ? order by TIME, ID", this::mapDto, userId, chatId, time);
    }
    
    @BucketKey("chatId")
    public void store(Long userId, Long chatId, String message, OffsetDateTime time) {
        jdbcTemplate.update("insert into REMINDERS(ID, CHAT_INDEX, USER_ID, CHAT_ID, MESSAGE, TIME) values(nextval('REMINDERS_SEQ'), (select coalesce(max(CHAT_INDEX), 0) + 1 from REMINDERS where CHAT_ID = ?), ?, ?, ?, ?)",
                chatId, userId, chatId, message, time);
    }
    
    public int delete(Long chatId, Integer chatIndex) {
        return jdbcTemplate.update("delete from REMINDERS where CHAT_ID = ? and CHAT_INDEX = ?", chatId, chatIndex);
    }
    
    public int delete(Long userId, Long chatId, Integer chatIndex) {
        return jdbcTemplate.update("delete from REMINDERS where USER_ID = ? and CHAT_ID = ? and CHAT_INDEX = ?", userId, chatId, chatIndex);
    }
    
    public int deleteAll(Long userId, Long chatId) {
        return jdbcTemplate.update("delete from REMINDERS where USER_ID = ? and CHAT_ID = ?", userId, chatId);
    }
    
    private ReminderDto mapDto(ResultSet rs, int index) throws SQLException {
        return new ReminderDto(rs.getInt("CHAT_INDEX"), convertTime(rs.getTimestamp("TIME")), rs.getString("MESSAGE"));
    }
    
    private OffsetDateTime convertTime(Timestamp timestamp) {
        return timestamp != null ? OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC) : null;
    }
}
