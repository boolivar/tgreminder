package org.bool.tgreminder.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

class ReminderSchedulerTest {
    
    private final TaskScheduler scheduler = mock(TaskScheduler.class);
    
    private final AtomicReference<ScheduledFuture<?>> ref = new AtomicReference<>();
    
    private final ReminderScheduler reminder = new ReminderScheduler(scheduler, ref);
    
    @Test
    void testRemind() {
        OffsetDateTime time = Instant.ofEpochSecond(420000).atOffset(ZoneOffset.UTC);
        
        given(scheduler.schedule(any(), eq(time.toInstant())))
                .willReturn(new TestFuture<>(300));
        
        reminder.schedule(Object::toString, time);
        
        assertEquals(300, ref.get().getDelay(TimeUnit.MILLISECONDS));
    }
    
    @CsvSource({
        "200,300,200",
        "300,200,200",
        "500,200,200",
        "100,300,100",
        "100,100,100",
        "200,200,200",
    })
    @ParameterizedTest
    void testReschedule(long first, long second, long expected) {
        OffsetDateTime time1 = Instant.ofEpochSecond(220000).atOffset(ZoneOffset.UTC);
        OffsetDateTime time2 = Instant.ofEpochSecond(330000).atOffset(ZoneOffset.UTC);
        
        given(scheduler.schedule(any(), eq(time1.toInstant())))
                .willReturn(new TestFuture<>(first));
        given(scheduler.schedule(any(), eq(time2.toInstant())))
                .willReturn(new TestFuture<>(second));
        
        ScheduledFuture<?> f1 = scheduler.schedule(null, time1.toInstant());
        ScheduledFuture<?> f2 = scheduler.schedule(null, time2.toInstant());
        
        reminder.schedule(Object::toString, time1);
        reminder.schedule(Object::hashCode, time2);
        
        assertEquals(expected, ref.get().getDelay(TimeUnit.MILLISECONDS));
        assertFalse(ref.get().isCancelled());
        assertTrue(f1.isCancelled() != f2.isCancelled());
    }
    
    @CsvSource({
        "200,300",
        "300,200",
        "500,200",
        "100,300",
        "100,100",
        "200,200",
    })
    @ParameterizedTest
    void testReset(long first, long second) {
        OffsetDateTime time1 = Instant.ofEpochSecond(800000).atOffset(ZoneOffset.UTC);
        OffsetDateTime time2 = Instant.ofEpochSecond(900000).atOffset(ZoneOffset.UTC);
        
        given(scheduler.schedule(any(), eq(time1.toInstant())))
                .willReturn(new TestFuture<>(first));
        given(scheduler.schedule(any(), eq(time2.toInstant())))
                .willReturn(new TestFuture<>(second));
        
        ScheduledFuture<?> f1 = scheduler.schedule(null, time1.toInstant());
        ScheduledFuture<?> f2 = scheduler.schedule(null, time2.toInstant());
        
        reminder.reset(Object::hashCode, time1);
        reminder.reset(Object::toString, time2);
        
        assertSame(f2, ref.get());
        assertFalse(f1.isCancelled());
        assertFalse(f2.isCancelled());
    }
    
    static class TestFuture<T> extends CompletableFuture<T> implements ScheduledFuture<T> {

        private static final Comparator<Delayed> COMPARATOR = Comparator.comparingLong(d -> d.getDelay(TimeUnit.MILLISECONDS));
        
        private final long delayMs;
        
        TestFuture(long delayMs) {
            this.delayMs = delayMs;
        }
        
        @Override
        public long getDelay(TimeUnit unit) {
            return unit.convert(delayMs, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            return COMPARATOR.compare(this, o);
        }
    }
}
