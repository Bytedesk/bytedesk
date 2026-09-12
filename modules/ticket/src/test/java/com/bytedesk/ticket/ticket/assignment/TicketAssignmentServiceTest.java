package com.bytedesk.ticket.ticket.assignment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.flowable.engine.TaskService;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.jupiter.api.Test;

import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.member.MemberRepository;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;
import com.bytedesk.service.workgroup_routing.WorkgroupRoutingService;
import com.bytedesk.ticket.process.ProcessEntity;
import com.bytedesk.ticket.process.ProcessRepository;
import com.bytedesk.ticket.service.TicketNotificationService;
import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketRepository;
import com.bytedesk.ticket.ticket.enums.TicketStatusEnum;
import com.bytedesk.ticket.ticket.enums.TicketTypeEnum;
import com.bytedesk.ticket.ticket_settings.TicketSettingsEntity;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRestService;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRepository;
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
        void resolveFromWorkflowNodeShouldFallbackDisabledGlobalModeToRoundRobinWhenNodeModeBlank() {
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
        AssignmentResolutionResult result = fixture.service.resolveFromWorkflowNode(ticket, "task-1");

        assertTrue(result.isResolved());
        assertEquals("member-a", result.assigneeUid());
        assertEquals(TicketAssignmentModeEnum.DEFAULT.name(), result.strategy());
    }

        @Test
        void resolveFromWorkflowNodeShouldPreferConfiguredDepartmentUidsOverTicketDepartment() {
                Fixture fixture = new Fixture();
                TicketEntity ticket = buildTicket();
                ticket.setProcessEntityUid("process-2");

                ProcessEntity process = ProcessEntity.builder()
                                .uid("process-2")
                                .flowgramSchema(buildDepartmentNodeSchema("task-2", List.of("dept-2")))
                                .build();
                MemberEntity ticketDepartmentMember = MemberEntity.builder().uid("member-ticket-dept").build();
                MemberEntity configuredDepartmentMember = MemberEntity.builder().uid("member-configured-dept").build();

                when(fixture.processRepository.findByUid("process-2")).thenReturn(Optional.of(process));
                when(fixture.memberRepository.findByDeptUidAndDeletedFalse("dept-1")).thenReturn(List.of(ticketDepartmentMember));
                when(fixture.memberRepository.findByDeptUidAndDeletedFalse("dept-2")).thenReturn(List.of(configuredDepartmentMember));

                AssignmentResolutionResult result = fixture.service.resolveFromWorkflowNode(ticket, "task-2");

                assertTrue(result.isResolved());
                assertEquals("member-configured-dept", result.assigneeUid());
        }

    @Test
    void resolveByStrategyShouldUseWorkgroupRoutingForExternalTicket() {
        Fixture fixture = new Fixture();
        MemberEntity member = MemberEntity.builder().uid("member-workgroup-1").build();
        AgentEntity agent = AgentEntity.builder().uid("agent-1").member(member).build();
        WorkgroupEntity workgroup = WorkgroupEntity.builder().uid("wg-1").agents(List.of(agent)).build();
        TicketEntity ticket = TicketEntity.builder()
                .uid("ticket-external-1")
                .orgUid("org-1")
                .type(TicketTypeEnum.EXTERNAL.name())
                .workgroupUid("wg-1")
                .build();

        when(fixture.workgroupRestService.findByUid("wg-1")).thenReturn(Optional.of(workgroup));
        when(fixture.workgroupRoutingService.selectAgent(workgroup, null, TicketAssignmentModeEnum.DEFAULT.name()))
                .thenReturn(agent);

        AssignmentResolutionResult result = fixture.service.resolveByStrategy(ticket);

        assertTrue(result.isResolved());
        assertEquals("member-workgroup-1", result.assigneeUid());
        assertEquals(TicketAssignmentModeEnum.DEFAULT.name(), result.strategy());
    }

    @Test
    void resolveByStrategyShouldUseWorkgroupScopedSettingsWhenTicketSettingsUidMissing() {
        Fixture fixture = new Fixture();
        MemberEntity member = MemberEntity.builder().uid("member-random-1").build();
        AgentEntity agent = AgentEntity.builder().uid("agent-random-1").member(member).build();
        WorkgroupEntity workgroup = WorkgroupEntity.builder().uid("wg-random").agents(List.of(agent)).build();
        TicketSettingsEntity settings = TicketSettingsEntity.builder()
                .uid("settings-random")
                .basicSettings(TicketBasicSettingsEntity.builder()
                        .assignmentMode(TicketAssignmentModeEnum.RANDOM.name())
                        .build())
                .build();
        TicketEntity ticket = TicketEntity.builder()
                .uid("ticket-external-random")
                .orgUid("org-1")
                .type(TicketTypeEnum.EXTERNAL.name())
                .workgroupUid("wg-random")
                .build();

        when(fixture.workgroupRestService.findByUid("wg-random")).thenReturn(Optional.of(workgroup));
        when(fixture.ticketSettingsRestService.resolveEntityByWorkgroup("org-1", "wg-random", TicketTypeEnum.EXTERNAL.name()))
                .thenReturn(settings);
        when(fixture.workgroupRoutingService.selectAgent(workgroup, null, TicketAssignmentModeEnum.DEFAULT.name()))
                .thenReturn(agent);

        AssignmentResolutionResult result = fixture.service.resolveByStrategy(ticket);

        assertTrue(result.isResolved());
        assertEquals("member-random-1", result.assigneeUid());
                assertEquals(TicketAssignmentModeEnum.DEFAULT.name(), result.strategy());
    }

    @Test
    void resolveFromWorkflowNodeShouldResolveReporterPlaceholderToReporterSource() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        ticket.setProcessEntityUid("process-reporter-1");
        ticket.setReporter(UserProtobuf.builder().uid("visitor-reporter-1").nickname("Reporter").build().toJson());

        ProcessEntity process = ProcessEntity.builder()
                .uid("process-reporter-1")
                .flowgramSchema(buildUserNodeSchema("customerVerify", "${reporterUid}"))
                .build();
        when(fixture.processRepository.findByUid("process-reporter-1")).thenReturn(Optional.of(process));

        AssignmentResolutionResult result = fixture.service.resolveFromWorkflowNode(ticket, "customerVerify");

        assertTrue(result.isResolved());
        assertEquals("visitor-reporter-1", result.assigneeUid());
        assertEquals(AssignmentSource.REPORTER, result.source());
    }

    @Test
    void resolveFromWorkflowNodeShouldResolveAssigneeUidPlaceholder() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        ticket.setProcessEntityUid("process-assignee-1");
        ticket.setAssignee(UserProtobuf.builder().uid("member-current-1").build().toJson());

        ProcessEntity process = ProcessEntity.builder()
                .uid("process-assignee-1")
                .flowgramSchema(buildUserNodeSchema("processTicket", "${assigneeUid}"))
                .build();
        when(fixture.processRepository.findByUid("process-assignee-1")).thenReturn(Optional.of(process));
        when(fixture.memberRepository.findByUid("member-current-1"))
                .thenReturn(Optional.of(MemberEntity.builder().uid("member-current-1").build()));

        AssignmentResolutionResult result = fixture.service.resolveFromWorkflowNode(ticket, "processTicket");

        assertTrue(result.isResolved());
        assertEquals("member-current-1", result.assigneeUid());
        assertEquals(AssignmentSource.NODE_CONFIG, result.source());
    }

    @Test
    void resolveFromWorkflowNodeShouldResolveLiteralUserUid() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        ticket.setProcessEntityUid("process-literal-1");

        ProcessEntity process = ProcessEntity.builder()
                .uid("process-literal-1")
                .flowgramSchema(buildUserNodeSchema("task-literal", "member-lit-1"))
                .build();
        when(fixture.processRepository.findByUid("process-literal-1")).thenReturn(Optional.of(process));
        when(fixture.memberRepository.findByUid("member-lit-1"))
                .thenReturn(Optional.of(MemberEntity.builder().uid("member-lit-1").build()));

        AssignmentResolutionResult result = fixture.service.resolveFromWorkflowNode(ticket, "task-literal");

        assertTrue(result.isResolved());
        assertEquals("member-lit-1", result.assigneeUid());
        assertEquals(AssignmentSource.NODE_CONFIG, result.source());
    }

    @Test
    void autoAssignForNextNodeShouldSkipReporterOwnedTask() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        ticket.setProcessEntityUid("process-reporter-next");
        ticket.setReporter(UserProtobuf.builder().uid("visitor-reporter-1").build().toJson());
        ticket.setAssignee(UserProtobuf.builder().uid("member-agent-1").build().toJson());

        ProcessEntity process = ProcessEntity.builder()
                .uid("process-reporter-next")
                .flowgramSchema(buildUserNodeSchema("customerVerify", "${reporterUid}"))
                .build();
        Task task = mock(Task.class);
        TaskQuery taskQuery = mock(TaskQuery.class);
        when(fixture.taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.processInstanceId("process-1")).thenReturn(taskQuery);
        when(taskQuery.active()).thenReturn(taskQuery);
        when(taskQuery.list()).thenReturn(List.of(task));
        when(task.getId()).thenReturn("task-verify-1");
        when(task.getTaskDefinitionKey()).thenReturn("customerVerify");
        when(task.getAssignee()).thenReturn("member-agent-1");
        when(fixture.processRepository.findByUid("process-reporter-next")).thenReturn(Optional.of(process));

        AssignmentResolutionResult result = fixture.service.autoAssignForNextNode(ticket, "process-1");

        assertTrue(result.isResolved());
        assertEquals("visitor-reporter-1", result.assigneeUid());
        assertEquals(AssignmentSource.REPORTER, result.source());
        // 报告人节点不做成员级重派：不改任务归属、不覆盖工单处理人、不写分配日志
        verify(fixture.taskService, never()).setAssignee(anyString(), anyString());
        verify(fixture.taskService, never()).claim(anyString(), anyString());
        verify(fixture.assignmentLogRepository, never()).save(any(TicketAssignmentLogEntity.class));
        verify(fixture.ticketRepository, never()).save(any(TicketEntity.class));
        assertEquals("member-agent-1", ticket.getAssignee().getUid());
    }

    @Test
    void autoAssignForNextNodeShouldKeepAssigneeWhenPlaceholderResolvesToCurrentAssignee() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        ticket.setProcessEntityUid("process-loopback");
        ticket.setAssignee(UserProtobuf.builder().uid("member-agent-1").build().toJson());

        ProcessEntity process = ProcessEntity.builder()
                .uid("process-loopback")
                .flowgramSchema(buildUserNodeSchema("processTicket", "${assigneeUid}"))
                .build();
        Task task = mock(Task.class);
        TaskQuery taskQuery = mock(TaskQuery.class);
        when(fixture.taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.processInstanceId("process-1")).thenReturn(taskQuery);
        when(taskQuery.active()).thenReturn(taskQuery);
        when(taskQuery.list()).thenReturn(List.of(task));
        when(task.getId()).thenReturn("task-process-1");
        when(task.getTaskDefinitionKey()).thenReturn("processTicket");
        when(task.getAssignee()).thenReturn("member-agent-1");
        when(fixture.processRepository.findByUid("process-loopback")).thenReturn(Optional.of(process));
        when(fixture.memberRepository.findByUid("member-agent-1"))
                .thenReturn(Optional.of(MemberEntity.builder().uid("member-agent-1").build()));

        AssignmentResolutionResult result = fixture.service.autoAssignForNextNode(ticket, "process-1");

        assertTrue(result.isResolved());
        assertEquals("member-agent-1", result.assigneeUid());
        // 访客点「未解决」回退 processTicket 场景：处理权保持不变，不错派给其他客服
        verify(fixture.taskService, never()).setAssignee(anyString(), anyString());
        verify(fixture.taskService, never()).claim(anyString(), anyString());
    }

    @Test
    void resolveFromWorkflowNodeShouldNotAutoAssignWhenGlobalModeManualForDepartmentNode() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        ticket.setProcessEntityUid("process-manual-dept");
        ticket.setTicketSettingsUid("settings-manual-dept");

        ProcessEntity process = ProcessEntity.builder()
                .uid("process-manual-dept")
                .flowgramSchema(buildDepartmentNodeSchema("waitClaim"))
                .build();
        TicketSettingsEntity settings = TicketSettingsEntity.builder()
                .uid("settings-manual-dept")
                .basicSettings(TicketBasicSettingsEntity.builder()
                        .assignmentMode(TicketAssignmentModeEnum.MANUAL.name())
                        .build())
                .build();
        // 部门内存在可分配候选成员，但手动分配模式下仍不应自动分配
        when(fixture.processRepository.findByUid("process-manual-dept")).thenReturn(Optional.of(process));
        when(fixture.ticketSettingsRepository.findByUid("settings-manual-dept")).thenReturn(Optional.of(settings));
        when(fixture.memberRepository.findByDeptUidAndDeletedFalse("dept-1"))
                .thenReturn(List.of(MemberEntity.builder().uid("member-a").build()));

        AssignmentResolutionResult result = fixture.service.resolveFromWorkflowNode(ticket, "waitClaim");

        assertFalse(result.isResolved());
        assertEquals(AssignmentSource.NODE_CONFIG, result.source());
    }

    @Test
    void resolveFromWorkflowNodeShouldNotAutoAssignWhenGlobalModeManualForConfiguredDepartmentUids() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        ticket.setProcessEntityUid("process-manual-dept-uids");
        ticket.setTicketSettingsUid("settings-manual-dept-uids");

        ProcessEntity process = ProcessEntity.builder()
                .uid("process-manual-dept-uids")
                .flowgramSchema(buildDepartmentNodeSchema("task-1", List.of("dept-2")))
                .build();
        TicketSettingsEntity settings = TicketSettingsEntity.builder()
                .uid("settings-manual-dept-uids")
                .basicSettings(TicketBasicSettingsEntity.builder()
                        .assignmentMode(TicketAssignmentModeEnum.MANUAL.name())
                        .build())
                .build();
        when(fixture.processRepository.findByUid("process-manual-dept-uids")).thenReturn(Optional.of(process));
        when(fixture.ticketSettingsRepository.findByUid("settings-manual-dept-uids")).thenReturn(Optional.of(settings));
        when(fixture.memberRepository.findByDeptUidAndDeletedFalse("dept-2"))
                .thenReturn(List.of(MemberEntity.builder().uid("member-dept-2").build()));

        AssignmentResolutionResult result = fixture.service.resolveFromWorkflowNode(ticket, "task-1");

        assertFalse(result.isResolved());
        assertEquals(AssignmentSource.NODE_CONFIG, result.source());
    }

    @Test
    void resolveFromWorkflowNodeShouldAutoAssignWhenNodeModeOverridesGlobalManual() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        ticket.setProcessEntityUid("process-node-override");
        ticket.setTicketSettingsUid("settings-node-override");

        ProcessEntity process = ProcessEntity.builder()
                .uid("process-node-override")
                .flowgramSchema(buildDepartmentNodeSchema("task-1", null,
                        TicketAssignmentModeEnum.ROUND_ROBIN.name()))
                .build();
        TicketSettingsEntity settings = TicketSettingsEntity.builder()
                .uid("settings-node-override")
                .basicSettings(TicketBasicSettingsEntity.builder()
                        .assignmentMode(TicketAssignmentModeEnum.MANUAL.name())
                        .build())
                .build();
        when(fixture.processRepository.findByUid("process-node-override")).thenReturn(Optional.of(process));
        when(fixture.ticketSettingsRepository.findByUid("settings-node-override")).thenReturn(Optional.of(settings));
        when(fixture.memberRepository.findByDeptUidAndDeletedFalse("dept-1"))
                .thenReturn(List.of(MemberEntity.builder().uid("member-a").build()));

        AssignmentResolutionResult result = fixture.service.resolveFromWorkflowNode(ticket, "task-1");

        // 节点显式配置的分配方式优先于全局设置（工作流配置驱动自动分配）
        assertTrue(result.isResolved());
        assertEquals("member-a", result.assigneeUid());
        assertEquals(TicketAssignmentModeEnum.ROUND_ROBIN.name(), result.strategy());
    }

    @Test
    void resolveFromWorkflowNodeShouldNotAutoAssignWhenGlobalModeManualForRoleNode() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        ticket.setProcessEntityUid("process-manual-role");
        ticket.setTicketSettingsUid("settings-manual-role");

        ProcessEntity process = ProcessEntity.builder()
                .uid("process-manual-role")
                .flowgramSchema(buildRoleNodeSchema("task-1", "role-1"))
                .build();
        TicketSettingsEntity settings = TicketSettingsEntity.builder()
                .uid("settings-manual-role")
                .basicSettings(TicketBasicSettingsEntity.builder()
                        .assignmentMode(TicketAssignmentModeEnum.MANUAL.name())
                        .build())
                .build();
        when(fixture.processRepository.findByUid("process-manual-role")).thenReturn(Optional.of(process));
        when(fixture.ticketSettingsRepository.findByUid("settings-manual-role")).thenReturn(Optional.of(settings));
        when(fixture.userOrgRoleRepository.findMemberUidsByOrgUidAndRoleUid("org-1", "role-1"))
                .thenReturn(List.of("member-role-1"));
        when(fixture.memberRepository.findByUid("member-role-1"))
                .thenReturn(Optional.of(MemberEntity.builder().uid("member-role-1").build()));

        AssignmentResolutionResult result = fixture.service.resolveFromWorkflowNode(ticket, "task-1");

        assertFalse(result.isResolved());
        assertEquals(AssignmentSource.NODE_CONFIG, result.source());
    }

    @Test
    void autoAssignShouldNotAssignInternalTicketWhenGlobalModeManual() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        ticket.setProcessEntityUid("process-manual-autoassign");
        ticket.setTicketSettingsUid("settings-manual-autoassign");

        ProcessEntity process = ProcessEntity.builder()
                .uid("process-manual-autoassign")
                .flowgramSchema(buildDepartmentNodeSchema("waitClaim"))
                .build();
        TicketSettingsEntity settings = TicketSettingsEntity.builder()
                .uid("settings-manual-autoassign")
                .basicSettings(TicketBasicSettingsEntity.builder()
                        .assignmentMode(TicketAssignmentModeEnum.MANUAL.name())
                        .build())
                .build();
        Task task = mock(Task.class);
        TaskQuery taskQuery = mock(TaskQuery.class);
        when(fixture.taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.processInstanceId("process-1")).thenReturn(taskQuery);
        when(taskQuery.active()).thenReturn(taskQuery);
        when(taskQuery.list()).thenReturn(List.of(task));
        when(task.getId()).thenReturn("task-wait-claim-1");
        when(task.getTaskDefinitionKey()).thenReturn("waitClaim");
        when(task.getAssignee()).thenReturn(null);
        when(fixture.processRepository.findByUid("process-manual-autoassign")).thenReturn(Optional.of(process));
        when(fixture.ticketSettingsRepository.findByUid("settings-manual-autoassign")).thenReturn(Optional.of(settings));
        // 部门内有候选人也必须保持未分配，等待人工认领/指派
        when(fixture.memberRepository.findByDeptUidAndDeletedFalse("dept-1"))
                .thenReturn(List.of(MemberEntity.builder().uid("member-a").build()));

        AssignmentResolutionResult result = fixture.service.autoAssign(ticket, "process-1");

        assertFalse(result.isResolved());
        verify(fixture.taskService, never()).claim(anyString(), anyString());
        verify(fixture.taskService, never()).setAssignee(anyString(), anyString());
        verify(fixture.assignmentLogRepository, never()).save(any(TicketAssignmentLogEntity.class));
        verify(fixture.ticketRepository, never()).save(any(TicketEntity.class));
        verify(fixture.ticketNotificationService, never()).notifyTicketAssigned(any(TicketEntity.class));
    }

    @Test
    void autoAssignShouldSyncExplicitTicketAssigneeIntoActiveTask() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        UserProtobuf assignee = UserProtobuf.builder()
                .uid("member-explicit-1")
                .nickname("Explicit Agent")
                .build();
        ticket.setAssignee(assignee.toJson());

        MemberEntity member = MemberEntity.builder()
                .uid("member-explicit-1")
                .nickname("Explicit Agent")
                .build();
        Task task = mock(Task.class);
        TaskQuery taskQuery = mock(TaskQuery.class);

        when(fixture.taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.processInstanceId("process-1")).thenReturn(taskQuery);
        when(taskQuery.active()).thenReturn(taskQuery);
        when(taskQuery.list()).thenReturn(List.of(task));
        when(task.getId()).thenReturn("task-1");
        when(task.getName()).thenReturn("Process Ticket");
        when(task.getTaskDefinitionKey()).thenReturn("processTicket");
        when(task.getAssignee()).thenReturn(null);
        when(fixture.memberRepository.findByUid("member-explicit-1")).thenReturn(Optional.of(member));
        when(fixture.ticketRepository.findByUid("ticket-1")).thenReturn(Optional.of(ticket));
        when(fixture.ticketRepository.save(ticket)).thenReturn(ticket);
        when(fixture.uidUtils.getUid()).thenReturn("assign-log-1");

        AssignmentResolutionResult result = fixture.service.autoAssign(ticket, "process-1");

        assertTrue(result.isResolved());
        assertEquals("member-explicit-1", result.assigneeUid());
        assertEquals(TicketStatusEnum.ASSIGNED.name(), ticket.getStatus());
        assertEquals("member-explicit-1", ticket.getAssignee().getUid());
        verify(fixture.taskService).claim("task-1", "member-explicit-1");
        verify(fixture.assignmentLogRepository).save(any(TicketAssignmentLogEntity.class));
        verify(fixture.ticketNotificationService).notifyTicketAssigned(ticket);
    }

    private static TicketEntity buildTicket() {
        return TicketEntity.builder()
                .uid("ticket-1")
                .orgUid("org-1")
                .departmentUid("dept-1")
                .build();
    }

    private static String buildUserNodeSchema(String nodeId, String assigneeUid) {
        JSONObject root = new JSONObject();
        JSONObject nodeData = new JSONObject()
                .fluentPut("assigneeType", "user")
                .fluentPut("assigneeUids", List.of(assigneeUid));
        root.put("nodes", List.of(
                new JSONObject()
                        .fluentPut("id", "start")
                        .fluentPut("type", "start"),
                new JSONObject()
                        .fluentPut("id", nodeId)
                        .fluentPut("type", "approval")
                        .fluentPut("data", nodeData)));
        root.put("edges", List.of(
                new JSONObject()
                        .fluentPut("sourceNodeId", "start")
                        .fluentPut("targetNodeId", nodeId)));
        return root.toJSONString();
    }

    private static String buildDepartmentNodeSchema(String nodeId) {
        return buildDepartmentNodeSchema(nodeId, null, "");
    }

    private static String buildDepartmentNodeSchema(String nodeId, List<String> assigneeUids) {
        return buildDepartmentNodeSchema(nodeId, assigneeUids, "");
    }

    private static String buildDepartmentNodeSchema(String nodeId, List<String> assigneeUids, String assignmentMode) {
        JSONObject root = new JSONObject();
        JSONObject nodeData = new JSONObject()
                .fluentPut("assigneeType", "department")
                .fluentPut("assignmentMode", assignmentMode);
        if (assigneeUids != null) {
            nodeData.fluentPut("assigneeUids", assigneeUids);
        }
        root.put("nodes", List.of(
                new JSONObject()
                        .fluentPut("id", "start")
                        .fluentPut("type", "start"),
                new JSONObject()
                        .fluentPut("id", nodeId)
                        .fluentPut("type", "approval")
                        .fluentPut("data", nodeData)));
        root.put("edges", List.of(
                new JSONObject()
                        .fluentPut("sourceNodeId", "start")
                        .fluentPut("targetNodeId", nodeId)));
        return root.toJSONString();
    }

    private static String buildRoleNodeSchema(String nodeId, String roleUid) {
        JSONObject root = new JSONObject();
        JSONObject nodeData = new JSONObject()
                .fluentPut("assigneeType", "role")
                .fluentPut("roleUid", roleUid)
                .fluentPut("assignmentMode", "");
        root.put("nodes", List.of(
                new JSONObject()
                        .fluentPut("id", "start")
                        .fluentPut("type", "start"),
                new JSONObject()
                        .fluentPut("id", nodeId)
                        .fluentPut("type", "approval")
                        .fluentPut("data", nodeData)));
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
        private final TicketSettingsRestService ticketSettingsRestService = mock(TicketSettingsRestService.class);
        private final TaskService taskService = mock(TaskService.class);
        private final TicketAssignmentLogRepository assignmentLogRepository = mock(TicketAssignmentLogRepository.class);
        private final TicketUserOrgRoleRepository userOrgRoleRepository = mock(TicketUserOrgRoleRepository.class);
        private final TicketNotificationService ticketNotificationService = mock(TicketNotificationService.class);
        private final WorkgroupRestService workgroupRestService = mock(WorkgroupRestService.class);
        private final WorkgroupRoutingService workgroupRoutingService = mock(WorkgroupRoutingService.class);
        private final UidUtils uidUtils = mock(UidUtils.class);

        private final TicketAssignmentService service = new TicketAssignmentService(
                processRepository,
                memberRepository,
                ticketRepository,
                ticketSettingsRepository,
                ticketSettingsRestService,
                taskService,
                assignmentLogRepository,
                userOrgRoleRepository,
                ticketNotificationService,
                workgroupRestService,
                workgroupRoutingService,
                uidUtils);
    }
}
