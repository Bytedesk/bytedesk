package com.bytedesk.ticket.ticket.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.flowable.engine.TaskService;
import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.ticket.process.ProcessEntity;
import com.bytedesk.ticket.process.ProcessRepository;
import com.bytedesk.ticket.service.TicketNotificationService;
import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketRepository;
import com.bytedesk.ticket.ticket_settings.TicketSettingsEntity;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRepository;
import com.bytedesk.ticket.ticket_settings_basic.TicketAssignmentModeEnum;
import com.bytedesk.ticket.ticket_settings_basic.TicketBasicSettingsEntity;

class TicketAssignmentServiceTest {

    @Test
    void resolveByStrategyShouldUseRoundRobinWhenTicketSettingsMissing() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        MemberEntity member = MemberEntity.builder().uid("member-1").build();
        when(fixture.memberRepository.findByDeptUidAndDeletedFalse("dept-1")).thenReturn(List.of(member));

        AssignmentResolutionResult result = fixture.service.resolveByStrategy(ticket);

        assertTrue(result.isResolved());
        assertEquals("member-1", result.assigneeUid());
        assertEquals(TicketAssignmentModeEnum.DEFAULT.name(), result.strategy());
    }

    @Test
    void resolveFromWorkflowNodeShouldInheritGlobalAssignmentModeWhenNodeModeBlank() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        ticket.setProcessEntityUid("process-1");
        ticket.setTicketSettingsUid("settings-1");

        ProcessEntity process = ProcessEntity.builder()
                .uid("process-1")
                .flowgramSchema(buildDepartmentNodeSchema("task-1"))
                .build();
        TicketSettingsEntity settings = TicketSettingsEntity.builder()
                .uid("settings-1")
                .basicSettings(TicketBasicSettingsEntity.builder()
                        .assignmentMode(TicketAssignmentModeEnum.LEAST_ACTIVE.name())
                        .build())
                .build();
        MemberEntity memberA = MemberEntity.builder().uid("member-a").build();
        MemberEntity memberB = MemberEntity.builder().uid("member-b").build();

        when(fixture.processRepository.findByUid("process-1")).thenReturn(Optional.of(process));
        when(fixture.ticketSettingsRepository.findByUid("settings-1")).thenReturn(Optional.of(settings));
        when(fixture.memberRepository.findByDeptUidAndDeletedFalse("dept-1")).thenReturn(List.of(memberA, memberB));
        when(fixture.ticketRepository.countByAssigneeContainingAndStatusNotAndStatusNot(anyString(), anyString(), anyString()))
                .thenReturn(5L, 1L);

        AssignmentResolutionResult result = fixture.service.resolveFromWorkflowNode(ticket, "task-1");

        assertTrue(result.isResolved());
        assertEquals("member-b", result.assigneeUid());
        assertEquals(TicketAssignmentModeEnum.LEAST_ACTIVE.name(), result.strategy());
    }

    private static TicketEntity buildTicket() {
        return TicketEntity.builder()
                .uid("ticket-1")
                .orgUid("org-1")
                .departmentUid("dept-1")
                .build();
    }

    private static String buildDepartmentNodeSchema(String nodeId) {
        JSONObject root = new JSONObject();
        root.put("nodes", List.of(
                new JSONObject()
                        .fluentPut("id", "start")
                        .fluentPut("type", "start"),
                new JSONObject()
                        .fluentPut("id", nodeId)
                        .fluentPut("type", "approval")
                        .fluentPut("data", new JSONObject()
                                .fluentPut("assigneeType", "department")
                                .fluentPut("assignmentMode", ""))));
        root.put("edges", List.of(
                new JSONObject()
                        .fluentPut("sourceNodeId", "start")
                        .fluentPut("targetNodeId", nodeId)));
        return root.toJSONString();
    }

    private static class Fixture {
        private final ProcessRepository processRepository = mock(ProcessRepository.class);
        private final MemberRepository memberRepository = mock(MemberRepository.class);
        private final TicketRepository ticketRepository = mock(TicketRepository.class);
        private final TicketSettingsRepository ticketSettingsRepository = mock(TicketSettingsRepository.class);
        private final TaskService taskService = mock(TaskService.class);
        private final TicketAssignmentLogRepository assignmentLogRepository = mock(TicketAssignmentLogRepository.class);
        private final TicketUserOrgRoleRepository userOrgRoleRepository = mock(TicketUserOrgRoleRepository.class);
        private final TicketNotificationService ticketNotificationService = mock(TicketNotificationService.class);

        private final TicketAssignmentService service = new TicketAssignmentService(
                processRepository,
                memberRepository,
                ticketRepository,
                ticketSettingsRepository,
                taskService,
                assignmentLogRepository,
                userOrgRoleRepository,
                ticketNotificationService);
    }
}
