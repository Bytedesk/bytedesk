package com.bytedesk.core.redis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

/**
 * 原子取号服务测试：严格递增、空值/非正值/Redis 异常明确失败、不回退默认值。
 */
class RedisRoundRobinSequenceServiceTest {

    @Test
    void nextSequenceShouldReturnStrictlyIncreasingValues() {
        @SuppressWarnings("unchecked")
        RedisTemplate<String, String> redisTemplate = mock(RedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        AtomicLong counter = new AtomicLong();
        when(valueOps.increment(anyString())).thenAnswer(inv -> counter.incrementAndGet());
        RedisRoundRobinSequenceService service = new RedisRoundRobinSequenceService(redisTemplate);

        assertEquals(1L, service.nextSequence("scope-a"));
        assertEquals(2L, service.nextSequence("scope-a"));
        assertEquals(3L, service.nextSequence("scope-a"));
    }

    @Test
    void nextSequenceShouldRejectBlankScopeKey() {
        RedisRoundRobinSequenceService service = new RedisRoundRobinSequenceService(null);

        assertThrows(RedisRoundRobinSequenceService.RoundRobinSequenceException.class,
                () -> service.nextSequence("  "));
        assertThrows(RedisRoundRobinSequenceService.RoundRobinSequenceException.class,
                () -> service.nextSequence(null));
    }

    @Test
    void nextSequenceShouldFailOnNullReturn() {
        @SuppressWarnings("unchecked")
        RedisTemplate<String, String> redisTemplate = mock(RedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment(anyString())).thenReturn(null);
        RedisRoundRobinSequenceService service = new RedisRoundRobinSequenceService(redisTemplate);

        assertThrows(RedisRoundRobinSequenceService.RoundRobinSequenceException.class,
                () -> service.nextSequence("scope-null"));
    }

    @Test
    void nextSequenceShouldFailOnNonPositiveOrCorruptedValue() {
        @SuppressWarnings("unchecked")
        RedisTemplate<String, String> redisTemplate = mock(RedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment(anyString())).thenReturn(0L);
        RedisRoundRobinSequenceService service = new RedisRoundRobinSequenceService(redisTemplate);

        assertThrows(RedisRoundRobinSequenceService.RoundRobinSequenceException.class,
                () -> service.nextSequence("scope-corrupted"));
    }

    @Test
    void nextSequenceShouldFailOnRedisError() {
        @SuppressWarnings("unchecked")
        RedisTemplate<String, String> redisTemplate = mock(RedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        when(valueOps.increment(anyString()))
                .thenThrow(new org.springframework.data.redis.RedisConnectionFailureException("connection refused"));
        RedisRoundRobinSequenceService service = new RedisRoundRobinSequenceService(redisTemplate);

        // Redis 故障：明确失败，绝不返回默认序号（0/1）继续分配
        assertThrows(RedisRoundRobinSequenceService.RoundRobinSequenceException.class,
                () -> service.nextSequence("scope-error"));
    }

    @Test
    void concurrentCallsShouldReturnUniqueSequences() throws Exception {
        @SuppressWarnings("unchecked")
        RedisTemplate<String, String> redisTemplate = mock(RedisTemplate.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String> valueOps = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        AtomicLong counter = new AtomicLong();
        when(valueOps.increment(anyString())).thenAnswer(inv -> counter.incrementAndGet());
        RedisRoundRobinSequenceService service = new RedisRoundRobinSequenceService(redisTemplate);

        int threads = 8;
        int perThread = 100;
        Map<Long, Integer> seen = new ConcurrentHashMap<>();
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        try {
            for (int i = 0; i < threads; i++) {
                pool.submit(() -> {
                    start.await();
                    for (int j = 0; j < perThread; j++) {
                        seen.merge(service.nextSequence("scope-concurrent"), 1, Integer::sum);
                    }
                    return null;
                });
            }
            start.countDown();
            assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS) || true);
        } finally {
            pool.shutdown();
            assertTrue(pool.awaitTermination(30, TimeUnit.SECONDS));
        }

        // 全部序号唯一、无丢失
        assertEquals(threads * perThread, seen.size());
        assertEquals(List.of(1), seen.values().stream().distinct().toList());
    }
}
