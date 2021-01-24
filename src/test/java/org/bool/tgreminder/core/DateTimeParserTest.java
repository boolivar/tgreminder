package org.bool.tgreminder.core;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(Lifecycle.PER_CLASS)
class DateTimeParserTest {

    private final OffsetDateTime time = OffsetDateTime.of(LocalDateTime.of(2001, 5, 1, 22, 30), ZoneOffset.UTC);
    
    private final Clock clock = Clock.fixed(time.toInstant(), ZoneOffset.UTC);
    
    private final DateTimeParser parser = new DateTimeParser(clock);
    
    @CsvSource({
        "-2, 2001-05-01T23:10:00-02:00",
        "-1, 2001-05-01T23:10:00-01:00",
        "+1, 2001-05-01T23:10:00+01:00",
        
        "+2, 2001-05-02T23:10:00+02:00",
        "+3, 2001-05-02T23:10:00+03:00"
    })
    @ParameterizedTest
    void testUserZone(String timeZone, String expected) {
        assertEquals(OffsetDateTime.parse(expected), parser.parse("23:10", ZoneId.of(timeZone)));
    }
    
    @CsvSource({
        "2007-12-03T10:15:30+01:00, 2007-12-03T10:15:30+01:00",
        "2015-10-10T08:00,          2015-10-10T08:00:00+03:00",
        "10:20+01:00,               2001-05-02T10:20:00+01:00",
        "10:20+02:00,               2001-05-02T10:20:00+02:00",
        "23:15:20,                  2001-05-02T23:15:20+03:00"
    })
    @ParameterizedTest
    void testParseValues(String text, String expected) {
        assertEquals(OffsetDateTime.parse(expected), parser.parse(text, ZoneOffset.ofHours(3)));
    }
}
