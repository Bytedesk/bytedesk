package com.bytedesk.ticket.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.member.MemberRestService;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.core.rbac.user.UserRestService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic_subscription.TopicSubscriptionRestService;
import com.bytedesk.service.visitor.VisitorRestService;
import com.bytedesk.ticket.process.ProcessEntity;
import com.bytedesk.ticket.process.ProcessRepository;
import com.bytedesk.ticket.service.TicketNotificationService;
import com.bytedesk.ticket.ticket.assignment.TicketAssignmentService;
import com.bytedesk.ticket.ticket.dto.TicketWorkflowActionResponse;
import com.bytedesk.ticket.ticket.dto.TicketWorkflowTaskResponse;
import com.bytedesk.ticket.ticket.enums.TicketStatusEnum;

/**
 * 客服强制关单（FORCE_CLOSE）单测：
 * 1. 报告人节点 + 客服 operator → 注入 FORCE_CLOSE 动作
 * 2. 报告人节点 + reporter operator → 仍为验证动作，不含 FORCE_CLOSE
 * 3. 执行 forceClose → 完成 customerVerify 任务、工单 CLOSED、verified=true
 * 4. 自定义流程报告人节点配置 availableActions 时守卫豁免
 * 5. 非 RESOLVED 状态执行 forceClose → 抛异常
 */
class TicketServiceForceCloseTest {

    @Test
    void buildWorkflowTaskResponseShouldInjectForceCloseForAgentOnReporterNode() throws Exception {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildResolvedTicketWithReporter();
        Task task = mockCustomerVerifyTask("visitor-reporter-1");

        TicketWorkflowTaskResponse response = invokeBuildWorkflowTaskResponse(
                fixture.service, ticket, task, "member-agent-1", new JSONObject());

        assertThat(response.getActionable()).isTrue();
        assertThat(response.getActions()).hasSize(1);
        TicketWorkflowActionResponse action = response.getActions().get(0);
        assertThat(action.getKey()).isEqualTo("FORCE_CLOSE");
        assertThat(action.getType()).isEqualTo("forceClose");
        assertThat(Boolean.TRUE.equals(action.getDanger())).isTrue();
        assertThat(action.getTaskDefinitionKey()).isEqualTo("customerVerify");
    }

    @Test
    void buildWorkflowTaskResponseShouldKeepVerifyActionsForReporterOperator() throws Exception {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildResolvedTicketWithReporter();
        Task task = mockCustomerVerifyTask("visitor-reporter-1");

        TicketWorkflowTaskResponse response = invokeBuildWorkflowTaskResponse(
                fixture.service, ticket, task, "visitor-reporter-1", buildVerifiedGatewaySchema());

        assertThat(response.getActionable()).isTrue();
        assertThat(response.getActions()).extracting(TicketWorkflowActionResponse::getKey)
                .containsExactlyInAnyOrder("COMPLETE_VERIFIED", "COMPLETE_REJECTED");
    }

    @Test
    void buildWorkflowTaskResponseShouldNotInjectForceCloseWhenNotResolved() throws Exception {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildResolvedTicketWithReporter();
        ticket.setStatus(TicketStatusEnum.PROCESSING.name());
        Task task = mockCustomerVerifyTask("visitor-reporter-1");

        TicketWorkflowTaskResponse response = invokeBuildWorkflowTaskResponse(
                fixture.service, ticket, task, "member-agent-1", new JSONObject());

        // 归属访客 + 非处理人查询 + 非 RESOLVED → 无可操作动作
        assertThat(response.getActionable()).isFalse();
        assertThat(response.getActions()).isEmpty();
    }

    @Test
    void executeWorkflowActionShouldForceCloseResolvedTicket() throws Exception {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildResolvedTicketWithReporter();
        Task task = mockCustomerVerifyTask("visitor-reporter-1");

        when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(Optional.of(ticket));
        // executeWorkflowAction: getActiveTaskOrThrow 走 taskId 分支
        TaskQuery taskQuery = mock(TaskQuery.class);
        when(fixture.taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.taskId(task.getId())).thenReturn(taskQuery);
        when(taskQuery.active()).thenReturn(taskQuery);
        when(taskQuery.singleResult()).thenReturn(task);
        // complete 后无剩余活动任务 → 流程实例结束
        TaskQuery nextTaskQuery = mock(TaskQuery.class);
        when(nextTaskQuery.processInstanceId(ticket.getProcessInstanceId())).thenReturn(nextTaskQuery);
        when(nextTaskQuery.active()).thenReturn(nextTaskQuery);
        when(nextTaskQuery.list()).thenReturn(List.of());
        // completeTask 返回新 query 实例
        when(fixture.taskService.createTaskQuery()).thenReturn(taskQuery, nextTaskQuery);
        when(fixture.processRepository.findByUid(anyString())).thenReturn(Optional.empty());
        Comment comment = mock(Comment.class);
        when(fixture.taskService.addComment(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(comment);
        initConvertUtilsContext();

        TicketRequest request = new TicketRequest();
        request.setUid(ticket.getUid());
        request.setOrgUid(ticket.getOrgUid());
        request.setTaskId(task.getId());
        request.setActionKey("FORCE_CLOSE");
        request.setAssigneeUid("member-agent-1");
        request.setProcessComment("访客长期未确认");

        TicketResponse response = fixture.service.executeWorkflowAction(request);

        assertThat(response.getStatus()).isEqualTo(TicketStatusEnum.CLOSED.name());
        assertThat(ticket.getVerified()).isTrue();
        // 任务完成变量带 verified=true 与强制关单审计标记
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> variablesCaptor = ArgumentCaptor.forClass(Map.class);
        verify(fixture.taskService).complete(eq(task.getId()), variablesCaptor.capture());
        assertThat(variablesCaptor.getValue())
                .containsEntry("verified", true)
                .containsEntry("forceClosed", true)
                .containsEntry("forceClosedBy", "member-agent-1");
        verify(fixture.taskService).addComment(eq(task.getId()), eq(ticket.getProcessInstanceId()),
                eq(TicketStatusEnum.CLOSED.name()), anyString());
        // SLA 终态收尾由 persistAndNotifyStatusChange 触发
        verify(fixture.ticketSLAService).finalizeOpenRecordsForTerminalTicket(any(TicketEntity.class),
                any(ZonedDateTime.class), any());
        verify(fixture.ticketAssignmentService, never()).autoAssignForNextNode(any(), anyString());
    }

    @Test
    void executeWorkflowActionShouldRejectForceCloseWhenNotResolved() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildResolvedTicketWithReporter();
        ticket.setStatus(TicketStatusEnum.PROCESSING.name());
        Task task = mockCustomerVerifyTask("visitor-reporter-1");

        when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(Optional.of(ticket));
        TaskQuery taskQuery = mock(TaskQuery.class);
        when(fixture.taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.taskId(task.getId())).thenReturn(taskQuery);
        when(taskQuery.active()).thenReturn(taskQuery);
        when(taskQuery.singleResult()).thenReturn(task);
        when(fixture.processRepository.findByUid(anyString())).thenReturn(Optional.empty());

        TicketRequest request = new TicketRequest();
        request.setUid(ticket.getUid());
        request.setOrgUid(ticket.getOrgUid());
        request.setTaskId(task.getId());
        request.setActionKey("FORCE_CLOSE");
        request.setAssigneeUid("member-agent-1");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> fixture.service.executeWorkflowAction(request));
        assertThat(exception.getMessage()).contains("不能强制关单");
        verify(fixture.taskService, never()).complete(anyString(), anyMap());
    }

    @Test
    void executeWorkflowActionShouldRejectForceCloseWhenOperatorIsReporter() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildResolvedTicketWithReporter();
        Task task = mockCustomerVerifyTask("visitor-reporter-1");

        when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(Optional.of(ticket));
        TaskQuery taskQuery = mock(TaskQuery.class);
        when(fixture.taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.taskId(task.getId())).thenReturn(taskQuery);
        when(taskQuery.active()).thenReturn(taskQuery);
        when(taskQuery.singleResult()).thenReturn(task);
        when(fixture.processRepository.findByUid(anyString())).thenReturn(Optional.empty());

        TicketRequest request = new TicketRequest();
        request.setUid(ticket.getUid());
        request.setOrgUid(ticket.getOrgUid());
        request.setTaskId(task.getId());
        request.setActionKey("FORCE_CLOSE");
        request.setAssigneeUid("visitor-reporter-1");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> fixture.service.executeWorkflowAction(request));
        assertThat(exception.getMessage()).contains("报告人请使用确认解决");
        verify(fixture.taskService, never()).complete(anyString(), anyMap());
    }

    @Test
    void buildRuntimeContextShouldExemptForceCloseFromAvailableActionsGuard() throws Exception {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildResolvedTicketWithReporter();
        // 自定义流程：报告人节点显式配置了 availableActions（不含 FORCE_CLOSE）
        JSONObject nodeData = new JSONObject();
        JSONArray availableActions = new JSONArray();
        JSONObject verifiedAction = new JSONObject();
        verifiedAction.put("key", "COMPLETE_VERIFIED");
        availableActions.add(verifiedAction);
        nodeData.put("availableActions", availableActions);
        JSONObject node = new JSONObject();
        node.put("id", "verifyByReporter");
        node.put("data", nodeData);
        JSONObject flowgramSchema = new JSONObject();
        JSONArray nodes = new JSONArray();
        nodes.add(node);
        flowgramSchema.put("nodes", nodes);

        ProcessEntity process = mock(ProcessEntity.class);
        // 注意：toJSONString 需延迟求值——后续测试步骤会继续修改 nodeData（增删 assigneeType）
        when(process.getFlowgramSchema()).thenAnswer(invocation -> flowgramSchema.toJSONString());
        when(fixture.processRepository.findByUid(ticket.getProcessEntityUid()))
                .thenReturn(Optional.of(process));

        Task task = mock(Task.class);
        when(task.getTaskDefinitionKey()).thenReturn("verifyByReporter");
        when(task.getId()).thenReturn("task-custom-1");
        when(task.getName()).thenReturn("报告人验证");
        when(task.getAssignee()).thenReturn("visitor-reporter-1");
        when(task.getProcessInstanceId()).thenReturn("process-instance-fc-1");

        // 节点未配置 assigneeType 且非默认 customerVerify key → 不注入（注入条件不满足）
        nodeData.remove("assigneeType");
        TicketWorkflowTaskResponse response = invokeBuildWorkflowTaskResponse(
                fixture.service, ticket, task, "member-agent-1", flowgramSchema);
        assertThat(response.getActionable()).isFalse();

        // 配置 assigneeType=reporter → 注入 FORCE_CLOSE
        nodeData.put("assigneeType", "reporter");
        TicketWorkflowTaskResponse responseWithReporterType = invokeBuildWorkflowTaskResponse(
                fixture.service, ticket, task, "member-agent-1", flowgramSchema);
        assertThat(responseWithReporterType.getActionable()).isTrue();
        assertThat(responseWithReporterType.getActions())
                .extracting(TicketWorkflowActionResponse::getKey)
                .containsExactly("FORCE_CLOSE");

        // 关键回归：节点配置了 availableActions（不含 FORCE_CLOSE）时，
        // FORCE_CLOSE 提交不被 buildRuntimeContext 守卫拒绝（系统级注入动作豁免）
        Object runtimeContext = invokeBuildRuntimeContext(
                fixture.service, ticket, task, "FORCE_CLOSE");
        assertThat(runtimeContext).isNotNull();
        // 对照：未配置在 availableActions 中的其他动作仍被拒绝
        assertThrows(InvocationTargetException.class, () -> invokeBuildRuntimeContext(
                fixture.service, ticket, task, "HOLD"));
    }

    /**
     * 构造与默认流程一致的 flowgram schema：customerVerify 后继网关带 verified
     * true/false 分支条件，使 reporter 查询时 buildVerifiedWorkflowActions 生效。
     */
    private static JSONObject buildVerifiedGatewaySchema() {
        JSONObject gatewayData = new JSONObject();
        JSONArray conditions = new JSONArray();
        JSONObject trueCondition = new JSONObject();
        trueCondition.put("expression", "${verified == true}");
        JSONObject falseCondition = new JSONObject();
        falseCondition.put("expression", "${verified == false}");
        conditions.add(trueCondition);
        conditions.add(falseCondition);
        gatewayData.put("conditions", conditions);

        JSONObject gatewayNode = new JSONObject();
        gatewayNode.put("id", "verifyGateway");
        gatewayNode.put("type", "exclusiveGateway");
        gatewayNode.put("data", gatewayData);

        JSONObject verifyNode = new JSONObject();
        verifyNode.put("id", "customerVerify");
        verifyNode.put("type", "approval");

        JSONObject edge = new JSONObject();
        edge.put("sourceNodeId", "customerVerify");
        edge.put("targetNodeId", "verifyGateway");

        JSONObject schema = new JSONObject();
        schema.put("nodes", new JSONArray(List.of(verifyNode, gatewayNode)));
        schema.put("edges", new JSONArray(List.of(edge)));
        return schema;
    }

    private static Task mockCustomerVerifyTask(String assignee) {
        Task task = mock(Task.class);
        when(task.getId()).thenReturn("task-verify-1");
        when(task.getName()).thenReturn("工单待验证");
        when(task.getTaskDefinitionKey()).thenReturn("customerVerify");
        when(task.getAssignee()).thenReturn(assignee);
        when(task.getProcessInstanceId()).thenReturn("process-instance-fc-1");
        return task;
    }

    private static TicketEntity buildResolvedTicketWithReporter() {
        UserProtobuf reporter = UserProtobuf.builder()
                .uid("visitor-reporter-1")
                .nickname("Reporter")
                .build();
        TicketEntity ticket = TicketEntity.builder()
                .uid("ticket-fc-1")
                .orgUid("org-1")
                .processEntityUid("process-fc-1")
                .processInstanceId("process-instance-fc-1")
                .status(TicketStatusEnum.RESOLVED.name())
                .build();
        ticket.setReporter(reporter.toJson());
        return ticket;
    }

    private static void initConvertUtilsContext() {
        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBean(ModelMapper.class)).thenReturn(new ModelMapper());
        new ApplicationContextHolder().setApplicationContext(applicationContext);
    }

    private static TicketWorkflowTaskResponse invokeBuildWorkflowTaskResponse(TicketService service,
            TicketEntity ticket, Task task, String operatorUid, JSONObject flowgramSchema) throws Exception {
        Method method = TicketService.class.getDeclaredMethod(
                "buildWorkflowTaskResponse",
                TicketEntity.class,
                Task.class,
                String.class,
                JSONObject.class);
        method.setAccessible(true);
        return (TicketWorkflowTaskResponse) method.invoke(service, ticket, task, operatorUid, flowgramSchema);
    }

    private static Object invokeBuildRuntimeContext(TicketService service,
            TicketEntity ticket, Task task, String actionKey) throws Exception {
        Method method = TicketService.class.getDeclaredMethod(
                "buildRuntimeContext",
                TicketEntity.class,
                Task.class,
                String.class);
        method.setAccessible(true);
        return method.invoke(service, ticket, task, actionKey);
    }

    private static class Fixture {
        private final RuntimeService runtimeService = mock(RuntimeService.class);
        private final TaskService taskService = mock(TaskService.class);
        private final HistoryService historyService = mock(HistoryService.class);
        private final MemberRestService memberRestService = mock(MemberRestService.class);
        private final UserRestService userRestService = mock(UserRestService.class);
        private final VisitorRestService visitorRestService = mock(VisitorRestService.class);
        private final ThreadRestService threadRestService = mock(ThreadRestService.class);
        private final TopicSubscriptionRestService topicSubscriptionRestService = mock(TopicSubscriptionRestService.class);
        private final TicketRestService ticketRestService = mock(TicketRestService.class);
        private final TicketNotificationService ticketNotificationService = mock(TicketNotificationService.class);
        private final ProcessRepository processRepository = mock(ProcessRepository.class);
        private final TicketSLAService ticketSLAService = mock(TicketSLAService.class);
        private final TicketAssignmentService ticketAssignmentService = mock(TicketAssignmentService.class);

        private final TicketService service = new TicketService(
                runtimeService,
                taskService,
                historyService,
                memberRestService,
                userRestService,
                visitorRestService,
                threadRestService,
                topicSubscriptionRestService,
                ticketRestService,
                ticketNotificationService,
                processRepository,
                ticketSLAService,
                ticketAssignmentService);
    }
}
