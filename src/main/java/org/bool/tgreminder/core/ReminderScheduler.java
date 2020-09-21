package org.bool.tgreminder.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Component
public class ReminderScheduler {
    
    private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);
    
    private final TaskScheduler scheduler;
    
    private final AtomicReference<ScheduledFuture<?>> ref;

    @Autowired
    public ReminderScheduler(TaskScheduler scheduler) {
        this(scheduler, new AtomicReference<>());
    }
    
    public ReminderScheduler(TaskScheduler scheduler, AtomicReference<ScheduledFuture<?>> ref) {
        this.scheduler = scheduler;
        this.ref = ref;
    }
    
    public void reset(Consumer<OffsetDateTime> task, OffsetDateTime time) {
        ref.set(scheduleTask(task, time));
    }
    
    public void reset() {
        ref.set(null);
    }
    
    public void schedule(Consumer<OffsetDateTime> task, OffsetDateTime time) {
        ScheduledFuture<?> future = scheduleTask(task, time);
        ScheduledFuture<?> old = ref.getAndAccumulate(future, this::min);
        if (old != null) {
            max(future, old).cancel(false);
        }
        if (ref.get() == future) {
            log.info("Schedule to {}", time);
        }
    }
    
    private ScheduledFuture<?> scheduleTask(Consumer<OffsetDateTime> task, OffsetDateTime time) {
        return scheduler.schedule(() -> task.accept(time), time.toInstant());
    }
    
    private <T extends Comparable<? super T>> T min(T a, T b) {
        return a == null || b.compareTo(a) < 0 ? b : a;
    }
    
    private <T extends Comparable<? super T>> T max(T a, T b) {
        return a == null || b.compareTo(a) > 0 ? b : a;
    }
}
