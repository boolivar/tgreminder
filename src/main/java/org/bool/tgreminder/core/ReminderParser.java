package org.bool.tgreminder.core;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;

import org.apache.commons.lang3.StringUtils;
import org.bool.tgreminder.dto.ReminderDto;
import org.springframework.stereotype.Component;

@Component
public class ReminderParser {
    
    private final DateTimeParser dateTimeParser;
    
    private final ReminderFactory reminderFactory;
    
    public ReminderParser(DateTimeParser dateTimeParser, ReminderFactory reminderFactory) {
        this.dateTimeParser = dateTimeParser;
        this.reminderFactory = reminderFactory;
    }
    
    public ReminderDto parse(String text, ZoneId zoneId) {
        String[] parts = StringUtils.split(text, " ", 2);
        try {
            OffsetDateTime time = parseTime(parts[0], zoneId);
            return reminderFactory.message(time, parts[1]);
        } catch (DateTimeParseException e) {
            return parse(parts[0], parts[1], zoneId);
        }
    }
    
    private ReminderDto parse(String date, String text, ZoneId zoneId) {
        String[] parts = StringUtils.split(text, " ", 2);
        OffsetDateTime time = dateTimeParser.parseDateTime(date, parts[0], zoneId);
        return reminderFactory.message(time, parts[1]);
    }

    private OffsetDateTime parseTime(String time, ZoneId zoneId) {
        try {
            return dateTimeParser.parseTime(time, zoneId);
        } catch (DateTimeParseException e) {
            return dateTimeParser.parseInstant(time, zoneId);
        }
    }
}