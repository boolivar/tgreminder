package org.bool.tgreminder.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;

@Component
public class DateTimeParser {

    private final DateTimeFormatter formatter;
    
    private final Clock clock;
    
    @Autowired
    public DateTimeParser(Clock clock) {
        this(new DateTimeFormatterBuilder()
            .optionalStart()
                .append(DateTimeFormatter.ISO_LOCAL_DATE).appendLiteral('T')
            .optionalEnd()
            .append(DateTimeFormatter.ISO_LOCAL_TIME)
            .optionalStart()
                .appendOffset("+HH:mm", "Z")
            .optionalEnd()
            .toFormatter(), clock);
    }

    public DateTimeParser(DateTimeFormatter formatter, Clock clock) {
        this.formatter = formatter;
        this.clock = clock;
    }
    
    public OffsetDateTime parse(String text, String userTimeZone) {
        return parse(text, userTimeZone != null ? ZoneId.of(userTimeZone) : clock.getZone());
    }
    
    public OffsetDateTime parse(String text, ZoneId userZoneId) {
        TemporalAccessor value = formatter.parseBest(text, OffsetDateTime::from, LocalDateTime::from, OffsetTime::from, LocalTime::from);
        if (value instanceof OffsetDateTime) {
            return (OffsetDateTime) value;
        }
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).atZone(userZoneId).toOffsetDateTime();
        }
        if (value instanceof OffsetTime) {
            return ((OffsetTime) value).atDate(clock.instant().atZone(userZoneId).toLocalDate());
        }
        return clock.instant().atZone(userZoneId).with((LocalTime) value).toOffsetDateTime();
    }
}
