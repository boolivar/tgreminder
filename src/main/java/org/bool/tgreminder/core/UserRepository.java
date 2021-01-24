package org.bool.tgreminder.core;

import org.bool.tgreminder.dto.UserDto;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public UserDto findById(Integer id) {
        List<UserDto> results = jdbcTemplate.query("SELECT * FROM users WHERE id=?", this::map, id);
        return DataAccessUtils.singleResult(results);
    }
    
    public void insertUser(UserDto user) {
        int updated = jdbcTemplate.update("INSERT INTO users VALUES(id=?, name=?, time_zone=?)",
                user.getId(), user.getName(), user.getTimeZone());
        if (updated != 1) {
            throw new IncorrectResultSizeDataAccessException("User insert error: " + user.getId(), 1, updated);
        }
    }
    
    public void updateUser(UserDto user) {
        int updated = jdbcTemplate.update("UPDATE users SET name=?, time_zone=? WHERE id=?",
                user.getName(), user.getTimeZone(), user.getId());
        if (updated != 1) {
            throw new IncorrectResultSizeDataAccessException("User update error: " + user.getId(), 1, updated);
        }
    }
    
    private UserDto map(ResultSet rs, int index) throws SQLException {
        return new UserDto(rs.getInt("id"), rs.getString("name"), rs.getString("time_zone"));
    }
}
