package org.bool.tgreminder.core;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

@Component
public class DateTimeParser {

    private final TimeParser timeParser;
    
    private final Clock clock;
    
    public DateTimeParser(TimeParser timeParser, Clock clock) {
        this.timeParser = timeParser;
        this.clock = clock;
    }
    
    public OffsetDateTime parseInstant(String instant, ZoneId zoneId) {
        return timeParser.parseDateTime(instant);
    }
    
    public OffsetDateTime parseTime(String time, ZoneId zoneId) {
        LocalTime localTime = timeParser.parseTime(time);
        return ZonedDateTime.now(clock).withZoneSameInstant(zoneId).with(localTime).toOffsetDateTime();
    }
    
    public OffsetDateTime parseDateTime(String date, String time, ZoneId zoneId) {
        LocalDate localDate = timeParser.parseDate(date);
        LocalTime localTime = timeParser.parseTime(time);
        return ZonedDateTime.of(localDate, localTime, zoneId).toOffsetDateTime();
    }
}
