package org.bool.tgreminder.core;

import org.bool.tgreminder.dto.ReminderDto;
import org.springframework.dao.support.DataAccessUtils;
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
        jdbcTemplate.query("select * from REMINDERS where TIME = ?)", (ResultSet rs) -> handler.accept(rs.getLong("USER_ID"), rs.getString("MESSAGE")), Timestamp.from(time.toInstant()));
    }
    
    public Optional<OffsetDateTime> findNext(OffsetDateTime time) {
        List<OffsetDateTime> values = jdbcTemplate.query("select MIN(TIME) from REMINDERS where TIME > ?", (rs, i) -> convertTime(rs.getTimestamp(1)), time);
        return Optional.ofNullable(DataAccessUtils.singleResult(values));
    }
    
    public List<ReminderDto> findByUserId(Long userId) {
        return jdbcTemplate.query("select * from REMINDERS where USER_ID = ? order by TIME, ID", this::mapDto, userId);
    }
    
    public void store(Long userId, String message, OffsetDateTime time) {
        jdbcTemplate.update("insert into REMINDERS(ID, USER_ID, MESSAGE, TIME) values(nextval('REMINDERS_SEQ'), ?, ?, ?)",
                userId, message, Timestamp.from(time.toInstant()));
    }
    
    public int delete(Long userId, Integer index) {
        return jdbcTemplate.update("delete from REMINDERS where ID = (select ID from (select ID, row_number() over (order by TIME, ID) RN from REMINDERS where USER_ID = ?) where RN = ?)", userId, index);
    }
    
    public int deleteAll(Long userId) {
        return jdbcTemplate.update("delete from REMINDERS where USER_ID = ?", userId);
    }
    
    private ReminderDto mapDto(ResultSet rs, int index) throws SQLException {
        return new ReminderDto(convertTime(rs.getTimestamp("TIME")), rs.getString("MESSAGE"));
    }
    
    private OffsetDateTime convertTime(Timestamp timestamp) {
        return OffsetDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC);
    }
}
