package org.bool.tgreminder.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.TemporalAccessor;

@Component
public class DateTimeParser {

    private final DateTimeFormatter formatter;
    
    private final ZoneOffset defaultOffset;
    
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
            .toFormatter(), ZoneOffset.ofHours(3), clock);
    }

    public DateTimeParser(DateTimeFormatter formatter, ZoneOffset defaultOffset, Clock clock) {
        this.formatter = formatter;
        this.defaultOffset = defaultOffset;
        this.clock = clock;
    }
    
    public OffsetDateTime parse(String text) {
        TemporalAccessor value = formatter.parseBest(text, OffsetDateTime::from, LocalDateTime::from, OffsetTime::from, LocalTime::from);
        if (value instanceof OffsetDateTime) {
            return (OffsetDateTime) value;
        }
        if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).atOffset(defaultOffset);
        }
        if (value instanceof OffsetTime) {
            return ((OffsetTime) value).atDate(LocalDate.now(clock.withZone(((OffsetTime) value).getOffset())));
        }
        return ((LocalTime) value).atDate(LocalDate.now(clock.withZone(defaultOffset))).atOffset(defaultOffset);
    }
}
