package com.bytedesk.service.routing_strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bytedesk.core.enums.VisitorCallTypeEnum;
import com.bytedesk.core.tag.TagEntity;
import com.bytedesk.core.tag.TagRepository;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.service.agent.AgentCapacityService;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.presence.PresenceFacadeService;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup_settings.WorkgroupSettingsEntity;

@ExtendWith(MockitoExtension.class)
class WorkgroupTagRoutingServiceTest {

    @Mock private TagRepository tagRepository;
    @Mock private PresenceFacadeService presence;
    @Mock private AgentCapacityService capacity;

    private WorkgroupTagRoutingService service;
    private ThreadEntity thread;
    private WorkgroupEntity workgroup;
    private AgentEntity first;
    private AgentEntity second;

    @BeforeEach
    void setup() {
        service = new WorkgroupTagRoutingService(tagRepository, presence, capacity);
        thread = ThreadEntity.builder().uid("thread-1").tagRoutingEvaluated(false)
                .routingTagSnapshot(List.of("tag-1", "tag-2")).build();
        first = AgentEntity.builder().uid("agent-1").orgUid("org-1").build();
        second = AgentEntity.builder().uid("agent-2").orgUid("org-1").build();
        workgroup = WorkgroupEntity.builder().uid("wg-1").orgUid("org-1")
                .agents(List.of(first, second))
                .settings(WorkgroupSettingsEntity.builder().tagRoutingEnabled(true).build()).build();
        Map<String, String> bindings = new LinkedHashMap<>();
        workgroup.getSettings().setTagAgentBindings(bindings);
    }

    @Test
    void selectsFirstEligibleUidInSnapshotOrder() {
        bind("tag-1", first);
        when(presence.isAgentOnlineAndAvailableForCallType(first, VisitorCallTypeEnum.TEXT)).thenReturn(true);
        when(capacity.hasCapacity(first)).thenReturn(true);
        assertEquals(first, service.selectAgent(thread, workgroup, VisitorCallTypeEnum.TEXT).orElseThrow());
    }

    @Test
    void skipsFullAgentAndUsesNextTag() {
        bind("tag-1", first);
        bind("tag-2", second);
        when(presence.isAgentOnlineAndAvailableForCallType(first, VisitorCallTypeEnum.TEXT)).thenReturn(true);
        when(capacity.hasCapacity(first)).thenReturn(false);
        when(presence.isAgentOnlineAndAvailableForCallType(second, VisitorCallTypeEnum.TEXT)).thenReturn(true);
        when(capacity.hasCapacity(second)).thenReturn(true);
        assertEquals(second, service.selectAgent(thread, workgroup, VisitorCallTypeEnum.TEXT).orElseThrow());
    }

    @Test
    void skipsOfflineAgentAndUnknownUid() {
        bind("tag-2", second);
        when(presence.isAgentOnlineAndAvailableForCallType(second, VisitorCallTypeEnum.TEXT)).thenReturn(false);
        assertTrue(service.selectAgent(thread, workgroup, VisitorCallTypeEnum.TEXT).isEmpty());
        verifyNoInteractions(capacity);
    }

    @Test
    void cannotSelectDeletedForeignOrNonmemberAgent() {
        first.setDeleted(true);
        second.setOrgUid("other-org");
        bind("tag-1", first);
        bind("tag-2", second);
        assertTrue(service.selectAgent(thread, workgroup, VisitorCallTypeEnum.TEXT).isEmpty());
        verifyNoInteractions(presence, capacity);
        first.setDeleted(false);
        workgroup.setAgents(List.of());
        assertTrue(service.selectAgent(thread, workgroup, VisitorCallTypeEnum.TEXT).isEmpty());
    }

    @Test
    void disabledManualLegacyAndCompletedDecisionsNeverQueryTags() {
        workgroup.getSettings().setTagRoutingEnabled(false);
        assertEmpty();
        workgroup.getSettings().setTagRoutingEnabled(null);
        assertEmpty();
        workgroup.getSettings().setTagRoutingEnabled(true);
        workgroup.getSettings().setRoutingMode("MANUAL");
        assertEmpty();
        workgroup.getSettings().setRoutingMode("ROUND_ROBIN");
        thread.setTagRoutingEvaluated(true);
        assertEmpty();
        thread.setTagRoutingEvaluated(null);
        assertEmpty();
        thread.setTagRoutingEvaluated(false).setChatting();
        assertEmpty();
        thread.setClose();
        assertEmpty();
        verifyNoInteractions(tagRepository, presence, capacity);
    }

    @Test
    void unknownOrUnboundTagFallsBackAndOtherCallTypesDoNotQuery() {
        // 快照中的 tag 未在工作组绑定 Map 中配置：直接回退，不查标签库
        assertEmpty();
        assertTrue(service.selectAgent(thread, workgroup, VisitorCallTypeEnum.PHONE).isEmpty());
        verifyNoInteractions(tagRepository);
    }

    private void assertEmpty() {
        assertTrue(service.selectAgent(thread, workgroup, VisitorCallTypeEnum.TEXT).isEmpty());
    }

    private void bind(String uid, AgentEntity agent) {
        workgroup.getSettings().getTagAgentBindings().put(uid, agent.getUid());
        when(tagRepository.findByUidAndOrgUidAndTypeAndDeletedFalse(uid, "org-1", "ROUTE"))
                .thenReturn(Optional.of(TagEntity.builder().uid(uid).orgUid("org-1").type("ROUTE").build()));
    }
}
