package com.bytedesk.service.workgroup_routing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.redis.RedisRoundRobinSequenceService;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.presence.PresenceFacadeService;
import com.bytedesk.service.queue.QueueRestService;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRepository;
import com.bytedesk.service.workgroup_routing.event.WorkgroupRoutingAdvanceEvent;
import com.bytedesk.service.workgroup_routing.event.WorkgroupRoutingRefreshEvent;

/**
 * 工单独立选人入口测试：原子取号轮询、不消费客服会话预计算状态、不发布推进/刷新事件。
 */
class WorkgroupRoutingServiceTicketSelectionTest {

    /** 内存版原子序号源：模拟 Redis INCR 语义（按 scopeKey 独立、跨线程严格递增） */
    static final class InMemorySequenceService extends RedisRoundRobinSequenceService {

        private final java.util.Map<String, java.util.concurrent.atomic.AtomicLong> counters =
                new java.util.concurrent.ConcurrentHashMap<>();

        InMemorySequenceService() {
            super(null);
        }

        @Override
        public long nextSequence(String scopeKey) {
            return counters.computeIfAbsent(scopeKey, k -> new java.util.concurrent.atomic.AtomicLong())
                    .incrementAndGet();
        }
    }

    /** 离线状态的客服：工单选人不再要求在线可接待 */
    private static AgentEntity offlineAgent(String agentUid, String memberUid) {
        return AgentEntity.builder()
                .uid(agentUid)
                .orgUid("org-1")
                .status(com.bytedesk.service.agent.AgentStatusEnum.OFFLINE.name())
                .member(com.bytedesk.core.member.MemberEntity.builder()
                        .uid(memberUid)
                        .orgUid("org-1")
                        .build())
                .build();
    }

    @Test
    void selectAgentForTicketShouldIncludeOfflineAgents() {
        // 回归复现：服务重启后客服长连接未重建时建外部工单，旧实现 getAvailableAgents 过滤掉
        // 全部离线客服 → no available workgroup agents → 处理人为空。
        // 工单是异步业务：离线客服也必须参与轮询
        WorkgroupEntity workgroup = WorkgroupEntity.builder()
                .uid("wg-offline").orgUid("org-1")
                .agents(List.of(offlineAgent("agent-off-a", "member-off-a"),
                        offlineAgent("agent-off-b", "member-off-b")))
                .build();
        PresenceFacadeService presence = Mockito.mock(PresenceFacadeService.class);
        when(presence.getAvailableAgents(workgroup)).thenReturn(List.of());
        WorkgroupRoutingService service = buildService(
                Mockito.mock(WorkgroupRoutingRepository.class),
                Mockito.mock(WorkgroupRepository.class),
                Mockito.mock(BytedeskEventPublisher.class), presence, new InMemorySequenceService());

        assertEquals("member-off-a",
                service.selectAgentForTicket(workgroup, "EXTERNAL", "ROUND_ROBIN").getMember().getUid());
        assertEquals("member-off-b",
                service.selectAgentForTicket(workgroup, "EXTERNAL", "ROUND_ROBIN").getMember().getUid());
        // 在线判定不参与工单候选池
        verify(presence, never()).getAvailableAgents(workgroup);
    }

    private static AgentEntity agent(String agentUid, String memberUid) {
        return AgentEntity.builder()
                .uid(agentUid)
                .orgUid("org-1")
                .member(com.bytedesk.core.member.MemberEntity.builder()
                        .uid(memberUid)
                        .orgUid("org-1")
                        .build())
                .build();
    }

    private static WorkgroupRoutingService buildService(
            WorkgroupRoutingRepository routingRepository,
            WorkgroupRepository workgroupRepository,
            BytedeskEventPublisher publisher,
            PresenceFacadeService presenceFacadeService,
            RedisRoundRobinSequenceService sequenceService) {
        return new WorkgroupRoutingService(
                routingRepository,
                workgroupRepository,
                publisher,
                Mockito.mock(UidUtils.class),
                Mockito.mock(QueueRestService.class),
                Mockito.mock(ThreadRestService.class),
                presenceFacadeService,
                sequenceService);
    }

    @Test
    void selectAgentForTicketShouldRoundRobinAllMemberBackedAgentsStrictly() {
        // 工单是异步业务：离线/OFFLINE 状态客服也参与轮询（服务重启后会话重建窗口内建单不得悬空）
        WorkgroupEntity workgroup = WorkgroupEntity.builder()
                .uid("wg-1").orgUid("org-1")
                .agents(List.of(offlineAgent("agent-a", "member-a"), offlineAgent("agent-b", "member-b"),
                        offlineAgent("agent-c", "member-c")))
                .build();
        BytedeskEventPublisher publisher = Mockito.mock(BytedeskEventPublisher.class);
        WorkgroupRoutingRepository routingRepository = Mockito.mock(WorkgroupRoutingRepository.class);
        WorkgroupRoutingService service = buildService(routingRepository,
                Mockito.mock(WorkgroupRepository.class),
                publisher, Mockito.mock(PresenceFacadeService.class), new InMemorySequenceService());

        String picked1 = service.selectAgentForTicket(workgroup, "EXTERNAL", "ROUND_ROBIN").getMember().getUid();
        String picked2 = service.selectAgentForTicket(workgroup, "EXTERNAL", "ROUND_ROBIN").getMember().getUid();
        String picked3 = service.selectAgentForTicket(workgroup, "EXTERNAL", "ROUND_ROBIN").getMember().getUid();
        String picked4 = service.selectAgentForTicket(workgroup, "EXTERNAL", "ROUND_ROBIN").getMember().getUid();

        // 严格 A/B/C 循环，重复分配不再集中到同一人
        assertEquals("member-a", picked1);
        assertEquals("member-b", picked2);
        assertEquals("member-c", picked3);
        assertEquals("member-a", picked4);
        // 不读不推客服会话预计算状态
        verify(publisher, never()).publishEvent(any(WorkgroupRoutingAdvanceEvent.class));
        verify(publisher, never()).publishEvent(any(WorkgroupRoutingRefreshEvent.class));
        verify(routingRepository, never()).findByWorkgroupUidAndDeletedFalseOrderByUpdatedAtDescIdDesc(anyString());
    }

    @Test
    void selectAgentForTicketShouldDeduplicateByMemberUid() {
        // 同一 member 的两个 agent + 另一个 member 的 agent：按 Member 去重后 2 人
        WorkgroupEntity workgroup = WorkgroupEntity.builder()
                .uid("wg-dup").orgUid("org-1")
                .agents(List.of(offlineAgent("agent-a1", "member-a"), offlineAgent("agent-a2", "member-a"),
                        offlineAgent("agent-b", "member-b")))
                .build();
        WorkgroupRoutingService service = buildService(
                Mockito.mock(WorkgroupRoutingRepository.class),
                Mockito.mock(WorkgroupRepository.class),
                Mockito.mock(BytedeskEventPublisher.class), Mockito.mock(PresenceFacadeService.class),
                new InMemorySequenceService());

        String picked1 = service.selectAgentForTicket(workgroup, "EXTERNAL", "ROUND_ROBIN").getMember().getUid();
        String picked2 = service.selectAgentForTicket(workgroup, "EXTERNAL", "ROUND_ROBIN").getMember().getUid();
        String picked3 = service.selectAgentForTicket(workgroup, "EXTERNAL", "ROUND_ROBIN").getMember().getUid();

        // member-a 不因有两个 Agent 身份而获得双倍权重
        assertEquals("member-a", picked1);
        assertEquals("member-b", picked2);
        assertEquals("member-a", picked3);
    }

    @Test
    void selectAgentForTicketShouldIgnoreAgentsWithoutValidMember() {
        AgentEntity noMember = AgentEntity.builder().uid("agent-no-member").orgUid("org-1").build();
        AgentEntity blankMember = AgentEntity.builder().uid("agent-blank")
                .orgUid("org-1")
                .member(com.bytedesk.core.member.MemberEntity.builder().uid("").build())
                .build();
        WorkgroupEntity workgroup = WorkgroupEntity.builder()
                .uid("wg-invalid").orgUid("org-1")
                .agents(List.of(noMember, blankMember))
                .build();
        WorkgroupRoutingService service = buildService(
                Mockito.mock(WorkgroupRoutingRepository.class),
                Mockito.mock(WorkgroupRepository.class),
                Mockito.mock(BytedeskEventPublisher.class), Mockito.mock(PresenceFacadeService.class),
                new InMemorySequenceService());

        assertNull(service.selectAgentForTicket(workgroup, "EXTERNAL", "ROUND_ROBIN"));
    }

    @Test
    void selectAgentForTicketShouldReturnNullForManualMode() {
        WorkgroupRoutingService service = buildService(
                Mockito.mock(WorkgroupRoutingRepository.class),
                Mockito.mock(WorkgroupRepository.class),
                Mockito.mock(BytedeskEventPublisher.class),
                Mockito.mock(PresenceFacadeService.class),
                new InMemorySequenceService());
        WorkgroupEntity workgroup = WorkgroupEntity.builder().uid("wg-manual").orgUid("org-1").build();

        assertNull(service.selectAgentForTicket(workgroup, "EXTERNAL", "MANUAL"));
    }

    @Test
    void selectAgentForTicketShouldPropagateSequenceFailure() {
        WorkgroupEntity workgroup = WorkgroupEntity.builder()
                .uid("wg-fail").orgUid("org-1")
                .agents(List.of(offlineAgent("agent-a", "member-a")))
                .build();
        RedisRoundRobinSequenceService failing = new RedisRoundRobinSequenceService(null) {
            @Override
            public long nextSequence(String scopeKey) {
                throw new RoundRobinSequenceException("redis unavailable");
            }
        };
        WorkgroupRoutingService service = buildService(
                Mockito.mock(WorkgroupRoutingRepository.class),
                Mockito.mock(WorkgroupRepository.class),
                Mockito.mock(BytedeskEventPublisher.class), Mockito.mock(PresenceFacadeService.class), failing);

        // Redis 故障：明确失败，不回退首人/随机
        assertThrows(RedisRoundRobinSequenceService.RoundRobinSequenceException.class,
                () -> service.selectAgentForTicket(workgroup, "EXTERNAL", "ROUND_ROBIN"));
    }

    @Test
    void scopeKeyShouldEncodeSegmentsWithoutAmbiguity() {
        // 长度前缀编码：不同段组合不会产生相同 key
        String key1 = WorkgroupRoutingService.buildTicketScopeKey("org-1", "EXTERNAL", "WORKGROUP", "wg-2:x");
        String key2 = WorkgroupRoutingService.buildTicketScopeKey("org-1:wg-2", "EXTERNAL", "WORKGROUP", "x");
        assertTrue(!key1.equals(key2));
        assertEquals("5_org-1:8_EXTERNAL:9_WORKGROUP:6_wg-2:x", key1);
    }

    @Test
    void twoWorkgroupsShouldUseIsolatedSequences() {
        WorkgroupEntity wg1 = WorkgroupEntity.builder()
                .uid("wg-1").orgUid("org-1")
                .agents(List.of(offlineAgent("agent-a", "member-a"), offlineAgent("agent-b", "member-b")))
                .build();
        WorkgroupEntity wg2 = WorkgroupEntity.builder()
                .uid("wg-2").orgUid("org-1")
                .agents(List.of(offlineAgent("agent-c", "member-c"), offlineAgent("agent-d", "member-d")))
                .build();
        WorkgroupRoutingService service = buildService(
                Mockito.mock(WorkgroupRoutingRepository.class),
                Mockito.mock(WorkgroupRepository.class),
                Mockito.mock(BytedeskEventPublisher.class), Mockito.mock(PresenceFacadeService.class),
                new InMemorySequenceService());

        // 两个工作组各自从序号 1 开始，互不消费
        assertEquals("member-a", service.selectAgentForTicket(wg1, "EXTERNAL", "ROUND_ROBIN").getMember().getUid());
        assertEquals("member-c", service.selectAgentForTicket(wg2, "EXTERNAL", "ROUND_ROBIN").getMember().getUid());
        assertEquals("member-b", service.selectAgentForTicket(wg1, "EXTERNAL", "ROUND_ROBIN").getMember().getUid());
        assertEquals("member-d", service.selectAgentForTicket(wg2, "EXTERNAL", "ROUND_ROBIN").getMember().getUid());
    }

    @Test
    void differentTicketTypesShouldUseIsolatedSequences() {
        WorkgroupEntity workgroup = WorkgroupEntity.builder()
                .uid("wg-type").orgUid("org-1")
                .agents(List.of(offlineAgent("agent-a", "member-a"), offlineAgent("agent-b", "member-b")))
                .build();
        WorkgroupRoutingService service = buildService(
                Mockito.mock(WorkgroupRoutingRepository.class),
                Mockito.mock(WorkgroupRepository.class),
                Mockito.mock(BytedeskEventPublisher.class), Mockito.mock(PresenceFacadeService.class),
                new InMemorySequenceService());

        // 规划 §5.1.2：作用域为 orgUid + ticketType + scopeKind + scopeId，
        // INTERNAL 与 EXTERNAL 各自从序号 1 开始，互不消费
        assertEquals("member-a", service.selectAgentForTicket(workgroup, "EXTERNAL", "ROUND_ROBIN").getMember().getUid());
        assertEquals("member-a", service.selectAgentForTicket(workgroup, "INTERNAL", "ROUND_ROBIN").getMember().getUid());
        assertEquals("member-b", service.selectAgentForTicket(workgroup, "EXTERNAL", "ROUND_ROBIN").getMember().getUid());
        assertEquals("member-b", service.selectAgentForTicket(workgroup, "INTERNAL", "ROUND_ROBIN").getMember().getUid());
    }

    @Test
    void nonRoundRobinModeShouldFallBackToLegacyEntryWithoutSequenceConsumption() {
        PresenceFacadeService presence = Mockito.mock(PresenceFacadeService.class);
        com.bytedesk.service.workgroup_settings.WorkgroupSettingsEntity settings =
                com.bytedesk.service.workgroup_settings.WorkgroupSettingsEntity.builder()
                        .routingMode("WEIGHTED_RANDOM")
                        .build();
        WorkgroupEntity workgroup = WorkgroupEntity.builder()
                .uid("wg-legacy").orgUid("org-1").settings(settings)
                .agents(List.of(agent("agent-a", "member-a")))
                .build();
        when(presence.getAvailableAgents(workgroup)).thenReturn(workgroup.getAgents());
        WorkgroupRoutingRepository routingRepository = Mockito.mock(WorkgroupRoutingRepository.class);
        when(routingRepository.findByWorkgroupUidAndDeletedFalseOrderByUpdatedAtDescIdDesc(anyString()))
                .thenReturn(List.of(WorkgroupRoutingEntity.builder()
                        .uid("state-1").workgroupUid("wg-legacy").orgUid("org-1")
                        .routingMode("WEIGHTED_RANDOM").cursor(0L).nextAgentUid("agent-a")
                        .build()));
        WorkgroupRoutingService service = buildService(routingRepository,
                Mockito.mock(WorkgroupRepository.class),
                Mockito.mock(BytedeskEventPublisher.class), presence, new InMemorySequenceService());

        // 非 ROUND_ROBIN 工单模式保持既有三参数行为（预计算状态），不取号
        AgentEntity selected = service.selectAgentForTicket(workgroup, "EXTERNAL", "WEIGHTED_RANDOM");
        assertEquals("member-a", selected.getMember().getUid());
    }
}
