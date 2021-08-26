package org.bool.tgreminder.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class DateTimeParserTest {

    private final Clock clock = Clock.fixed(LocalDateTime.parse("2020-05-01T22:21").toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
    
    private final TimeParser timeParser = mock(TimeParser.class);

    private final DateTimeParser parser = new DateTimeParser(timeParser, clock);
    
    @CsvSource({
        "2020-05-01T10:30-01:00,    10:30,  -1",
        "2020-05-01T20:15:00Z,      20:15,   0",
        "2020-05-02T23:10:00+05:00, 23:10,  +5",
    })
    @ParameterizedTest
    void testParseTime(OffsetDateTime expected, LocalTime time, int offset) {
        when(timeParser.parseTime("<time>"))
                .thenReturn(time);
        
        assertEquals(expected, parser.parseTime("<time>", ZoneOffset.ofHours(offset)));
    }
    
    @CsvSource({
        "2020-05-01T23:10:00+03:00, -1",
        "2020-05-01T23:10:00+03:00,  0",
        "2020-05-01T23:10:00+03:00, +5",
    })
    @ParameterizedTest
    void testParseInstant(OffsetDateTime expected, int offset) {
        when(timeParser.parseDateTime("<instant>"))
                .thenReturn(expected);
        
        assertEquals(expected, parser.parseInstant("<instant>", ZoneOffset.ofHours(offset)));
    }
    
    @CsvSource({
        "2020-05-01T10:30-01:00,    2020-05-01, 10:30,  -1",
        "2021-02-01T20:15:00Z,      2021-02-01, 20:15,   0",
        "2000-05-05T23:10:00+05:00, 2000-05-05, 23:10,  +5",
    })
    @ParameterizedTest
    void testParseDateTime(OffsetDateTime expected, LocalDate date, LocalTime time, int offset) {
        when(timeParser.parseDate("<date>"))
                .thenReturn(date);
        when(timeParser.parseTime("<time>"))
                .thenReturn(time);
        
        assertEquals(expected, parser.parseDateTime("<date>", "<time>", ZoneOffset.ofHours(offset)));
    }
}
