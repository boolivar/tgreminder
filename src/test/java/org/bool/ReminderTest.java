package org.bool;

import com.pengrad.telegrambot.TelegramBot;

import org.bool.tgreminder.core.Reminder;
import org.bool.tgreminder.core.Repository;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
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
    
    @Test
    void testReschedule() {
        OffsetDateTime time1 = Instant.ofEpochSecond(220000).atOffset(ZoneOffset.UTC);
        OffsetDateTime time2 = Instant.ofEpochSecond(520000).atOffset(ZoneOffset.UTC);
        
        given(scheduler.schedule(any(), eq(time1.toInstant())))
                .willReturn(new TestFuture<>(200));
        given(scheduler.schedule(any(), eq(time1.toInstant())))
                .willReturn(new TestFuture<>(500));
        
        reminder.remind(22L, "test1", time1);
        reminder.remind(52L, "test2", time2);
        
        then(repository).should().store(22L, "test1", time1);
        then(repository).should().store(52L, "test2", time2);
        
        assertEquals(200, ref.get().getDelay(TimeUnit.MILLISECONDS));
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
