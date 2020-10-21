package org.bool.tgreminder.core;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(Lifecycle.PER_CLASS)
class DateTimeParserTest {

    private final OffsetDateTime time = OffsetDateTime.of(LocalDateTime.of(2001, 5, 1, 22, 30), ZoneOffset.UTC);
    
    private final Clock clock = Clock.fixed(time.toInstant(), ZoneOffset.UTC);
    
    private final DateTimeParser parser = new DateTimeParser(clock);
    
    @MethodSource
    @ParameterizedTest
    void testParseValues(String text, OffsetDateTime expected) {
        assertEquals(expected, parser.parse(text));
    }
    
    Stream<Arguments> testParseValues() {
        return Stream.of(
                Arguments.of("2007-12-03T10:15:30+01:00", OffsetDateTime.parse("2007-12-03T10:15:30+01:00")),
                Arguments.of("2015-10-10T08:00", OffsetDateTime.parse("2015-10-10T08:00:00+03:00")),
                Arguments.of("10:20+01:00", OffsetDateTime.parse("2001-05-01T10:20:00+01:00")),
                Arguments.of("10:20+02:00", OffsetDateTime.parse("2001-05-02T10:20:00+02:00")),
                Arguments.of("23:15:20", OffsetDateTime.parse("2001-05-02T23:15:20+03:00"))
        );
    }
}
