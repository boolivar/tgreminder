package org.bool.tgreminder.core;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class TimeParser {
    
    private final DateTimeFormatter LOCAL_DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public LocalTime parseTime(String text) {
        return LocalTime.parse(text);
    }
    
    public OffsetDateTime parseDateTime(String text) {
        return OffsetDateTime.parse(text);
    }
    
    public LocalDate parseDate(String text) {
        return LocalDate.parse(text, LOCAL_DATE_FORMAT);
    }
}
