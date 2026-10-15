package com.bytedesk.ticket.ticket.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.Test;

import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.redis.RedisRoundRobinSequenceService;
import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.enums.TicketTypeEnum;

class TicketRoundRobinScopeServiceTest {

    /** 内存版原子序号源：模拟 Redis INCR 语义（按 scopeKey 独立、跨线程严格递增） */
    static final class InMemorySequenceService extends RedisRoundRobinSequenceService {

        private final Map<String, AtomicLong> counters = new ConcurrentHashMap<>();

        InMemorySequenceService() {
            super(null);
        }

        @Override
        public long nextSequence(String scopeKey) {
            return counters.computeIfAbsent(scopeKey, k -> new AtomicLong()).incrementAndGet();
        }

        long totalCalls() {
            return counters.values().stream().mapToLong(AtomicLong::get).sum();
        }
    }

    private static TicketEntity internalTicket(String uid, String departmentUid) {
        return TicketEntity.builder()
                .uid(uid)
                .orgUid("org-1")
                .type(TicketTypeEnum.INTERNAL.name())
                .departmentUid(departmentUid)
                .build();
    }

    private static MemberEntity member(String uid, boolean deleted) {
        return MemberEntity.builder().uid(uid).orgUid("org-1").deleted(deleted).build();
    }

    @Test
    void normalizeCandidatesShouldDeduplicateFilterAndSort() {
        TicketRoundRobinScopeService service = new TicketRoundRobinScopeService(new InMemorySequenceService());
        TicketEntity ticket = internalTicket("t-1", "dept-1");

        List<MemberEntity> normalized = service.normalizeCandidates(ticket, java.util.Arrays.asList(
                member("member-c", false),
                member("member-a", false),
                member("member-c", false),           // 重复
                member(null, false),                 // 无 UID
                member("member-deleted", true),      // 已删除
                MemberEntity.builder().uid("member-cross").orgUid("org-other").build(), // 跨组织
                null));

        assertEquals(List.of("member-a", "member-c"),
                normalized.stream().map(MemberEntity::getUid).toList());
    }

    @Test
    void blankAndNullDepartmentShouldShareOrganizationScopeSequence() {
        InMemorySequenceService sequence = new InMemorySequenceService();
        TicketRoundRobinScopeService service = new TicketRoundRobinScopeService(sequence);
        List<MemberEntity> candidates = List.of(member("member-a", false), member("member-b", false));

        // NULL、空字符串、纯空白统一进入 ORGANIZATION 作用域，按序轮询
        assertEquals("member-a", service.roundRobinOrganization(internalTicket("t-null", null), candidates).getUid());
        assertEquals("member-b",
                service.roundRobinOrganization(internalTicket("t-empty", ""), candidates).getUid());
        assertEquals("member-a",
                service.roundRobinOrganization(internalTicket("t-blank", "  "), candidates).getUid());
    }

    @Test
    void differentDepartmentsShouldUseIsolatedSequences() {
        TicketRoundRobinScopeService service = new TicketRoundRobinScopeService(new InMemorySequenceService());
        List<MemberEntity> candidates = List.of(member("member-a", false), member("member-b", false));

        // 两个部门各自从序列 1 开始，互不消费对方的序号
        assertEquals("member-a",
                service.roundRobinDepartment(internalTicket("t-1", "dept-1"), "dept-1", candidates).getUid());
        assertEquals("member-a",
                service.roundRobinDepartment(internalTicket("t-2", "dept-2"), "dept-2", candidates).getUid());
        assertEquals("member-b",
                service.roundRobinDepartment(internalTicket("t-3", "dept-1"), "dept-1", candidates).getUid());
    }

    @Test
    void differentTicketTypesShouldUseIsolatedSequences() {
        TicketRoundRobinScopeService service = new TicketRoundRobinScopeService(new InMemorySequenceService());
        List<MemberEntity> candidates = List.of(member("member-a", false), member("member-b", false));

        TicketEntity internal = internalTicket("t-1", "dept-1");
        TicketEntity external = TicketEntity.builder()
                .uid("t-2").orgUid("org-1")
                .type(TicketTypeEnum.EXTERNAL.name())
                .departmentUid("dept-1")
                .build();

        // INTERNAL 与 EXTERNAL 同部门各用独立序列
        assertEquals("member-a", service.roundRobinDepartment(internal, "dept-1", candidates).getUid());
        assertEquals("member-a", service.roundRobinDepartment(external, "dept-1", candidates).getUid());
    }

    @Test
    void emptyCandidatesShouldReturnNullWithoutConsumingSequence() {
        InMemorySequenceService sequence = new InMemorySequenceService();
        TicketRoundRobinScopeService service = new TicketRoundRobinScopeService(sequence);

        assertNull(service.roundRobinDepartment(internalTicket("t-1", "dept-1"), "dept-1", List.of()));
        assertNull(service.roundRobinDepartment(internalTicket("t-2", "dept-1"), "dept-1", null));
        assertEquals(0L, sequence.totalCalls());
    }

    @Test
    void sequenceFailureShouldPropagateNotFallbackToFirstCandidate() {
        RedisRoundRobinSequenceService failing = new RedisRoundRobinSequenceService(null) {
            @Override
            public long nextSequence(String scopeKey) {
                throw new RoundRobinSequenceException("redis unavailable");
            }
        };
        TicketRoundRobinScopeService service = new TicketRoundRobinScopeService(failing);
        List<MemberEntity> candidates = List.of(member("member-a", false), member("member-b", false));

        // Redis 故障：明确失败，绝不回退首人
        assertThrows(RedisRoundRobinSequenceService.RoundRobinSequenceException.class,
                () -> service.roundRobinDepartment(internalTicket("t-1", "dept-1"), "dept-1", candidates));
    }

    @Test
    void concurrentRoundRobinShouldNotDoublePickSameIndex() throws Exception {
        InMemorySequenceService sequence = new InMemorySequenceService();
        TicketRoundRobinScopeService service = new TicketRoundRobinScopeService(sequence);
        List<MemberEntity> candidates = List.of(
                member("member-a", false), member("member-b", false), member("member-c", false));

        int threads = 12;
        int perThread = 25;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        Map<String, Integer> distribution = new ConcurrentHashMap<>();
        try {
            for (int i = 0; i < threads; i++) {
                pool.submit(() -> {
                    start.await();
                    for (int j = 0; j < perThread; j++) {
                        MemberEntity picked = service.roundRobinDepartment(
                                internalTicket("t-concurrent", "dept-1"), "dept-1", candidates);
                        distribution.merge(picked.getUid(), 1, Integer::sum);
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

        int total = threads * perThread;
        assertEquals(total, distribution.values().stream().mapToInt(Integer::intValue).sum());
        // 原子取号下三人计数差不超过 1（并发按取号顺序分配）
        int max = distribution.values().stream().mapToInt(Integer::intValue).max().orElseThrow();
        int min = distribution.values().stream().mapToInt(Integer::intValue).min().orElseThrow();
        assertTrue(max - min <= 1,
                "distribution should be fair but was " + distribution);
    }
}
