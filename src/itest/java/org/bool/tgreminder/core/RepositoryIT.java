package org.bool.tgreminder.core;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Transactional
    @Test
    void testStore() {
        repository.store(5L, 5L, "test1", time("2001-01-01T01:30"));
        repository.store(7L, 5L, "test2", time("2002-02-02T02:30"));
        
        assertThat(repository.find(5L, 5L))
                .hasSize(1)
                .element(0)
                    .matches(r -> "test1".equals(r.getMessage()))
                    .matches(r -> r.getChatIndex() == 1);
        
        assertThat(repository.findByChatId(5L))
                .hasSize(2)
                .matches(rs -> "test1".equals(rs.get(0).getMessage()))
                .matches(rs -> "test2".equals(rs.get(1).getMessage()))
                .matches(rs -> rs.get(0).getChatIndex() == 1)
                .matches(rs -> rs.get(1).getChatIndex() == 2);
        
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
                    .matches(r -> "b".equals(r.getMessage()))
                    .matches(r -> r.getChatIndex() == 2);
        
        assertThat(repository.delete(5L, 2))
                .isEqualTo(1);
        
        assertThat(repository.findByChatId(5L))
                .isEmpty();
    }
    
    @Transactional
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
    
    @Tag("load")
    @Test
    void testLoad() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newCachedThreadPool();
        
        List<Callable<Void>> tasks = IntStream.range(0, 32)
                .mapToObj(Long::valueOf)
                .<Callable<Void>>map(userId -> () -> {
                    for (int i = 0; i < 1000; ++i) {
                        repository.store(userId, 3L, "test", OffsetDateTime.now());
                    }
                    return null;
                })
                .collect(Collectors.toList());
        
        for (Future<?> result : executor.invokeAll(tasks)) {
            result.get();
        }
        
        assertThat(repository.delete(3L, 32001))
                .isEqualTo(0);
        assertThat(repository.delete(3L, 32000))
                .isEqualTo(1);
        
        for (long i = 0; i < 16; ++i) {
            repository.deleteAll(i, 3L);
        }
    }
    
    private OffsetDateTime time(String text) {
        return LocalDateTime.parse(text).atOffset(ZoneOffset.UTC);
    }
}
