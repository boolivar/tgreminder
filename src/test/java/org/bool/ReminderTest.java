package org.bool;

import com.pengrad.telegrambot.TelegramBot;

import org.bool.tgreminder.core.Reminder;
import org.bool.tgreminder.core.Repository;
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

class ReminderTest {
    
    private final Repository repository = mock(Repository.class);
    
    private final TaskScheduler scheduler = mock(TaskScheduler.class);
    
    private final TelegramBot telegramBot = mock(TelegramBot.class);
    
    private final AtomicReference<ScheduledFuture<?>> ref = new AtomicReference<>();
    
    private final Reminder reminder = new Reminder(repository, scheduler, telegramBot, ref);
    
    @Test
    void testRemind() {
        OffsetDateTime time = Instant.ofEpochSecond(420000).atOffset(ZoneOffset.UTC);
        
        given(scheduler.schedule(any(), any(Instant.class)))
                .willReturn(new TestFuture<>(300));
        
        reminder.remind(42L, "test", time);
        
        then(repository).should().store(42L, "test", time);
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
        
        reminder.remind(22L, "test1", time1);
        reminder.remind(33L, "test2", time2);
        
        then(repository).should().store(22L, "test1", time1);
        then(repository).should().store(33L, "test2", time2);
        
        assertEquals(expected, ref.get().getDelay(TimeUnit.MILLISECONDS));
        assertFalse(ref.get().isCancelled());
        assertTrue(f1.isCancelled() != f2.isCancelled());
    }
    
    static class TestFuture<T> extends CompletableFuture<T> implements ScheduledFuture<T> {

        private static final Comparator<Delayed> COMPARATOR = Comparator.comparingLong(d -> d.getDelay(TimeUnit.MILLISECONDS));
        
        public final long delayMs;
        
        public TestFuture(long delayMs) {
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
