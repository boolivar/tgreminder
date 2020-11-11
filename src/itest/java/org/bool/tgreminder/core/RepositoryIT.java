package org.bool.tgreminder.core;

import org.bool.tgreminder.dto.ReminderDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@Transactional
@SpringBootTest
class RepositoryIT {
    
    @Autowired
    private Repository repository;
    
    @Test
    void testEmpty() {
        assertThat(repository.findByChatId(5L))
            .isEmpty();
        assertThat(repository.find(5L, 5L))
            .isEmpty();
        assertThat(repository.findNext(OffsetDateTime.MIN))
            .isEmpty();
        
        assertThat(repository.deleteAll(5L, 5L))
            .isEqualTo(0);
        assertThat(repository.delete(5L, 5L, 1))
            .isEqualTo(0);
        
        BiConsumer<Long, String> handler = Mockito.mock(BiConsumer.class);
        repository.queryByTime(Instant.EPOCH.atOffset(ZoneOffset.UTC), handler);
        Mockito.verifyNoInteractions(handler);
    }
    
    @Test
    void testStore() {
        repository.store(5L, 5L, "test1", time("2001-01-01T01:30"));
        repository.store(7L, 5L, "test2", time("2002-02-02T02:30"));
        
        assertThat(repository.find(5L, 5L))
                .hasSize(1)
                .element(0)
                .extracting(ReminderDto::getMessage, ReminderDto::getChatIndex)
                .contains("test1", 1);
        
        assertThat(repository.findByChatId(5L))
                .hasSize(2)
                .extracting(ReminderDto::getMessage, ReminderDto::getChatIndex)
                .contains(tuple("test1", 1), tuple("test2", 2));
        
        assertThat(repository.findNext(time("2000-12-31T23:30")))
                .contains(time("2001-01-01T01:30"));
    }
    
    @Test
    void testDelete() {
        repository.store(5L, 5L, "a", time("2002-02-03T22:30"));
        repository.store(5L, 5L, "b", time("2002-02-03T23:30"));
        
        assertThat(repository.findByChatId(5L))
                .hasSize(2);
        
        assertThat(repository.delete(5L, 5L, 1))
                .isEqualTo(1);
        
        assertThat(repository.findByChatId(5L))
                .hasSize(1)
                .element(0)
                .extracting(ReminderDto::getMessage, ReminderDto::getChatIndex)
                .contains("b", 2);
        
        assertThat(repository.delete(5L, 2))
                .isEqualTo(1);
        
        assertThat(repository.findByChatId(5L))
                .isEmpty();
    }
    
    @Test
    void testQueryByTime() {
        repository.store(5L, 5L, "ONE", time("2005-05-05T03:30"));
        repository.store(5L, 6L, "TWO", time("2006-03-04T23:30"));
        
        BiConsumer<Long, String> handler = Mockito.mock(BiConsumer.class);
        repository.queryByTime(time("2005-05-05T03:30"), handler);
        repository.queryByTime(time("2006-03-04T23:30"), handler);
        
        Mockito.verify(handler).accept(5L, "ONE");
        Mockito.verify(handler).accept(6L, "TWO");
    }
    
    @Nested
    class ConcurrentIT {
    
        private final ExecutorService executor = Executors.newCachedThreadPool();
        
        @AfterEach
        void shutdownExecutor() {
            executor.shutdown();
        }
        
        @CsvSource({"10, 10"})
        @ParameterizedTest
        void testChatIndexIncrement(int threadCount, int recordCount) throws Exception {
            List<Callable<Void>> tasks = LongStream.range(0, threadCount)
                    .<Callable<Void>>mapToObj(userId -> () -> {
                        for (int i = 0; i < recordCount; ++i) {
                            repository.store(userId, 3L, "test", OffsetDateTime.now());
                        }
                        return null;
                    })
                    .collect(Collectors.toList());
            
            for (Future<?> result : executor.invokeAll(tasks)) {
                result.get();
            }
            
            assertThat(repository.delete(3L, threadCount * recordCount + 1))
                    .isEqualTo(0);
            assertThat(repository.delete(3L, threadCount * recordCount))
                    .isEqualTo(1);
        }
    }
    
    private OffsetDateTime time(String text) {
        return LocalDateTime.parse(text).atOffset(ZoneOffset.UTC);
    }
}
