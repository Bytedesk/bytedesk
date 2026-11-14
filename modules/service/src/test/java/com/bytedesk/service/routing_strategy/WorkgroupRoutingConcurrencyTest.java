package com.bytedesk.service.routing_strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRepository;

import jakarta.persistence.LockModeType;

class WorkgroupRoutingConcurrencyTest {

    @Test
    void concurrentAllocationsSerializeAndObserveCommittedOwnership() throws Exception {
        assertEquals(LockModeType.PESSIMISTIC_WRITE,
                ThreadRepository.class.getMethod("findByUidForRouting", String.class).getAnnotation(Lock.class).value());
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:routing-lock-" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("CREATE TABLE routing_thread(uid VARCHAR(64) PRIMARY KEY,status VARCHAR(32),evaluated BOOLEAN)");
        jdbc.update("INSERT INTO routing_thread VALUES ('thread-1','NEW',false)");
        ThreadRepository repository = mock(ThreadRepository.class);
        CountDownLatch secondAttemptedLock = new CountDownLatch(1);
        AtomicInteger reads = new AtomicInteger();
        // JDBC-backed repository seam: exercise the coordinator with real independent transactions/row locks.
        when(repository.findByUidForRouting(anyString())).thenAnswer(invocation -> {
            if (reads.incrementAndGet() == 2) {
                secondAttemptedLock.countDown();
            }
            return Optional.of(jdbc.queryForObject("SELECT uid,status,evaluated FROM routing_thread WHERE uid=? FOR UPDATE",
                    (row, index) -> ThreadEntity.builder().uid(row.getString(1)).status(row.getString(2))
                            .tagRoutingEvaluated(row.getBoolean(3)).build(),
                    invocation.getArgument(0, String.class)));
        });
        when(repository.saveAndFlush(any(ThreadEntity.class))).thenAnswer(invocation -> {
            ThreadEntity entity = invocation.getArgument(0, ThreadEntity.class);
            jdbc.update("UPDATE routing_thread SET status=?,evaluated=? WHERE uid=?",
                    entity.getStatus(), entity.getTagRoutingEvaluated(), entity.getUid());
            return entity;
        });
        WorkgroupRoutingTransaction service = new WorkgroupRoutingTransaction(
                repository, new DataSourceTransactionManager(dataSource));
        CountDownLatch firstInside = new CountDownLatch(1);
        CountDownLatch releaseFirst = new CountDownLatch(1);
        CountDownLatch secondInside = new CountDownLatch(1);
        AtomicInteger allocations = new AtomicInteger();
        var executor = Executors.newFixedThreadPool(2);
        try {
            var first = executor.submit(() -> service.execute("thread-1", thread -> {
                allocations.incrementAndGet();
                firstInside.countDown();
                await(releaseFirst);
                thread.setChatting();
                return null;
            }));
            assertTrue(firstInside.await(5, TimeUnit.SECONDS));
            var second = executor.submit(() -> service.execute("thread-1", thread -> {
                secondInside.countDown();
                assertTrue(thread.getTagRoutingEvaluated());
                assertTrue(thread.isChatting());
                return null;
            }));
            assertTrue(secondAttemptedLock.await(5, TimeUnit.SECONDS));
            assertFalse(secondInside.await(100, TimeUnit.MILLISECONDS));
            releaseFirst.countDown();
            first.get(5, TimeUnit.SECONDS);
            second.get(5, TimeUnit.SECONDS);
            assertEquals(1, allocations.get());
        } finally {
            releaseFirst.countDown();
            executor.shutdownNow();
            executor.awaitTermination(5, TimeUnit.SECONDS);
            jdbc.execute("DROP ALL OBJECTS");
        }
    }

    @Test
    void failedAllocationRollsBackDatabaseStateAndSuppressesNotification() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL("jdbc:h2:mem:routing-rollback-" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1");
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("CREATE TABLE routing_thread(uid VARCHAR(64) PRIMARY KEY,status VARCHAR(32),evaluated BOOLEAN)");
        jdbc.update("INSERT INTO routing_thread VALUES ('thread-1','ROBOTING',false)");
        ThreadRepository repository = mock(ThreadRepository.class);
        when(repository.findByUidForRouting("thread-1")).thenReturn(Optional.of(
                ThreadEntity.builder().uid("thread-1").tagRoutingEvaluated(false).build()));
        WorkgroupRoutingTransaction service = new WorkgroupRoutingTransaction(
                repository, new DataSourceTransactionManager(dataSource));
        AtomicInteger notifications = new AtomicInteger();
        try {
            org.junit.jupiter.api.Assertions.assertThrows(IllegalStateException.class,
                    () -> service.execute("thread-1", thread -> {
                        jdbc.update("UPDATE routing_thread SET status='CHATTING' WHERE uid='thread-1'");
                        service.afterCommit(notifications::incrementAndGet);
                        throw new IllegalStateException("queue save failed");
                    }));
            assertEquals("ROBOTING", jdbc.queryForObject("SELECT status FROM routing_thread", String.class));
            assertEquals(false, jdbc.queryForObject("SELECT evaluated FROM routing_thread", Boolean.class));
            assertEquals(0, notifications.get());
        } finally {
            jdbc.execute("DROP ALL OBJECTS");
        }
    }

    private static void await(CountDownLatch latch) {
        try {
            if (!latch.await(5, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Routing test latch timed out");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Routing test interrupted", e);
        }
    }
}
