package org.bool.tgreminder.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

import org.bool.tgreminder.dto.ReminderDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderParserTest {

    private final OffsetDateTime testTime = LocalDateTime.parse("2020-05-25T10:30").atOffset(ZoneOffset.UTC);
    
    private final ZoneId testZone = ZoneOffset.ofHours(5);
    
    @Spy
    private ReminderFactory reminderFactory = new ReminderFactory(null, null);
    
    @Mock
    private DateTimeParser dateTimeParser;
    
    @InjectMocks
    private ReminderParser parser;
    
    @Test
    void testParseTime() {
        given(dateTimeParser.parseTime("<time>", testZone))
                .willReturn(testTime);
        
        ReminderDto result = parser.parse("<time> time test", testZone);
        
        assertEquals("time test", result.getMessage());
        assertEquals(testTime, result.getTime());
    }
    
    @Test
    void testParseInstant() {
        given(dateTimeParser.parseTime("<instant>", testZone))
                .willThrow(DateTimeParseException.class);
        given(dateTimeParser.parseInstant("<instant>", testZone))
                .willReturn(testTime);
        
        ReminderDto result = parser.parse("<instant> instant test", testZone);
        
        assertEquals("instant test", result.getMessage());
        assertEquals(testTime, result.getTime());
    }
    
    @Test
    void testParseDateTime() {
        given(dateTimeParser.parseTime("<date>", testZone))
                .willThrow(DateTimeParseException.class);
        given(dateTimeParser.parseInstant("<date>", testZone))
                .willThrow(DateTimeParseException.class);
        given(dateTimeParser.parseDateTime("<date>", "<time>", testZone))
                .willReturn(testTime);
        
        ReminderDto result = parser.parse("<date> <time> date time test", testZone);
        
        assertEquals("date time test", result.getMessage());
        assertEquals(testTime, result.getTime());
    }
}
