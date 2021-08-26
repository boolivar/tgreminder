package org.bool.tgreminder.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

class TimeParserTest {

    private final TimeParser parser = new TimeParser();

    @Test
    void testTimeParse() {
        assertThrows(NullPointerException.class, () -> parser.parseTime(null));
        assertThrows(DateTimeParseException.class, () -> parser.parseTime(""));
        assertThrows(DateTimeParseException.class, () -> parser.parseTime("asd"));
        assertThrows(DateTimeParseException.class, () -> parser.parseTime("1030"));
        
        assertEquals(LocalTime.of(10, 30), parser.parseTime("10:30"));
    }
    
    @Test
    void testDateParse() {
        assertThrows(NullPointerException.class, () -> parser.parseDate(null));
        assertThrows(DateTimeParseException.class, () -> parser.parseDate(""));
        assertThrows(DateTimeParseException.class, () -> parser.parseDate("asd"));
        
        assertEquals(LocalDate.of(2000, 1, 1), parser.parseDate("01.01.2000"));
        assertEquals(LocalDate.of(2030, 12, 5), parser.parseDate("05.12.2030"));
    }
    
    @Test
    void testInstantParse() {
        assertThrows(NullPointerException.class, () -> parser.parseDateTime(null));
        assertThrows(DateTimeParseException.class, () -> parser.parseDateTime(""));
        assertThrows(DateTimeParseException.class, () -> parser.parseDateTime("asd"));
        
        assertEquals(OffsetDateTime.of(2001, 5, 1, 23, 10, 0, 0, ZoneOffset.ofHours(-1)), parser.parseDateTime("2001-05-01T23:10:00-01:00"));
        assertEquals(OffsetDateTime.of(2030, 1, 5, 9, 30, 0, 0, ZoneOffset.ofHours(2)), parser.parseDateTime("2030-01-05T09:30:00+02:00"));
    }
}
