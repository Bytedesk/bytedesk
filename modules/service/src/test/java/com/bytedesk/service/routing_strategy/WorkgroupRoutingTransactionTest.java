package com.bytedesk.service.routing_strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRepository;

class WorkgroupRoutingTransactionTest {

    @Test
    void commitsActualOutcomeBeforeNotificationsAndRecordsNoMatchOnce() {
        List<String> events = new ArrayList<>();
        ThreadRepository repository = mock(ThreadRepository.class);
        ThreadEntity thread = ThreadEntity.builder().uid("thread-1").tagRoutingEvaluated(false).build();
        when(repository.findByUidForRouting("thread-1")).thenReturn(Optional.of(thread));
        WorkgroupRoutingTransaction service = new WorkgroupRoutingTransaction(repository, manager(events));

        service.execute("thread-1", latest -> {
            latest.setQueuing();
            service.afterCommit(() -> events.add("send"));
            assertFalse(latest.getTagRoutingEvaluated());
            return null;
        });

        assertEquals(List.of("begin", "commit", "send"), events);
        assertEquals(true, thread.getTagRoutingEvaluated());
        assertFalse(service.isActive());
        verify(repository).saveAndFlush(thread);
    }

    @Test
    void rollbackSuppressesNotificationsAndLeavesDecisionRetryable() {
        List<String> events = new ArrayList<>();
        ThreadRepository repository = mock(ThreadRepository.class);
        ThreadEntity thread = ThreadEntity.builder().uid("thread-1").tagRoutingEvaluated(false).build();
        when(repository.findByUidForRouting("thread-1")).thenReturn(Optional.of(thread));
        WorkgroupRoutingTransaction service = new WorkgroupRoutingTransaction(repository, manager(events));

        assertThrows(IllegalStateException.class, () -> service.execute("thread-1", latest -> {
            service.afterCommit(() -> events.add("send"));
            throw new IllegalStateException("enqueue failed");
        }));
        assertEquals(List.of("begin", "rollback"), events);
        assertFalse(thread.getTagRoutingEvaluated());
        assertFalse(service.isActive());
    }

    @Test
    void postCommitNotificationFailureDoesNotPretendToRollbackAllocation() {
        List<String> events = new ArrayList<>();
        ThreadRepository repository = mock(ThreadRepository.class);
        ThreadEntity thread = ThreadEntity.builder().uid("thread-1").tagRoutingEvaluated(false).build();
        when(repository.findByUidForRouting("thread-1")).thenReturn(Optional.of(thread));
        WorkgroupRoutingTransaction service = new WorkgroupRoutingTransaction(repository, manager(events));

        assertThrows(IllegalStateException.class, () -> service.execute("thread-1", latest -> {
            latest.setChatting();
            service.afterCommit(() -> { throw new IllegalStateException("send failed"); });
            return null;
        }));
        assertEquals(List.of("begin", "commit"), events);
        assertEquals(true, thread.getTagRoutingEvaluated());
        assertFalse(service.isActive());
    }

    @Test
    void inactiveNotificationsRetainImmediateBehavior() {
        ThreadRepository repository = mock(ThreadRepository.class);
        List<String> events = new ArrayList<>();
        WorkgroupRoutingTransaction service = new WorkgroupRoutingTransaction(repository, manager(events));
        service.afterCommit(() -> events.add("send"));
        assertEquals(List.of("send"), events);
        verifyNoInteractions(repository);
    }

    @Test
    void backupContinuationRunsOutsideRowTransactionWithoutPrematureDecisionMarker() {
        List<String> events = new ArrayList<>();
        ThreadRepository repository = mock(ThreadRepository.class);
        ThreadEntity thread = ThreadEntity.builder().uid("thread-1").tagRoutingEvaluated(false).build();
        when(repository.findByUidForRouting("thread-1")).thenReturn(Optional.of(thread));
        WorkgroupRoutingTransaction service = new WorkgroupRoutingTransaction(repository, manager(events));
        service.execute("thread-1", latest -> {
            latest.setQueuing();
            return service.deferResult(() -> {
                assertFalse(service.isActive());
                events.add("backup");
                return null;
            });
        });
        assertEquals(List.of("begin", "commit", "backup"), events);
        assertFalse(thread.getTagRoutingEvaluated());
        org.mockito.Mockito.verify(repository, org.mockito.Mockito.never()).saveAndFlush(thread);
    }

    private AbstractPlatformTransactionManager manager(List<String> events) {
        return new AbstractPlatformTransactionManager() {
            private static final long serialVersionUID = 1L;
            @Override protected Object doGetTransaction() { return new Object(); }
            @Override protected void doBegin(Object transaction, org.springframework.transaction.TransactionDefinition definition) {
                events.add("begin");
            }
            @Override protected void doCommit(DefaultTransactionStatus status) { events.add("commit"); }
            @Override protected void doRollback(DefaultTransactionStatus status) { events.add("rollback"); }
        };
    }
}
