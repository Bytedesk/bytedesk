/*
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *   selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *   仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售
 *   Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *   contact: 270580156@qq.com
 *   技术/商务联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.ticket.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.ZonedDateTime;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;

import com.bytedesk.core.member.MemberRestService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserRestService;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic_subscription.TopicSubscriptionRestService;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.service.visitor.VisitorRestService;
import com.bytedesk.ticket.process.ProcessRepository;
import com.bytedesk.ticket.service.TicketNotificationService;
import com.bytedesk.ticket.ticket.assignment.TicketAssignmentService;
import com.bytedesk.ticket.ticket.enums.TicketStatusEnum;

/**
 * 重复确认/重复提交场景的幂等行为验证：
 * 1. 终态工单（VERIFIED_OK/CLOSED/CANCELLED）执行任意工作流动作 → 幂等返回当前工单，不查询任务；
 * 2. 携带已完成 taskId 但工单非终态（REOPENED 双击竞态）→ 历史任务命中同流程实例 → 幂等返回当前工单；
 * 3. 非法 taskId（查无历史任务）→ 仍抛「任务不存在或已结束」；
 * 4. taskId 属于其它流程实例 → 仍抛「任务不属于该工单流程实例」。
 */
class TicketServiceWorkflowDuplicateVerifyTest {

    private Fixture fixture;
    private TaskQuery taskQuery;
    private HistoricTaskInstanceQuery historicTaskInstanceQuery;

    @BeforeEach
    void setUp() throws Exception {
        fixture = new Fixture();
        taskQuery = mock(TaskQuery.class);
        historicTaskInstanceQuery = mock(HistoricTaskInstanceQuery.class);

        when(fixture.taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.taskId(any())).thenReturn(taskQuery);
        when(taskQuery.active()).thenReturn(taskQuery);

        when(fixture.historyService.createHistoricTaskInstanceQuery()).thenReturn(historicTaskInstanceQuery);
        when(historicTaskInstanceQuery.taskId(any())).thenReturn(historicTaskInstanceQuery);

        injectMockApplicationContext();
    }

    @AfterEach
    void tearDown() throws Exception {
        resetApplicationContext();
    }

    @Test
    void terminalVerifiedTicketShouldReturnCurrentTicketIdempotently() {
        TicketEntity ticket = buildTicket(TicketStatusEnum.VERIFIED_OK);
        when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(java.util.Optional.of(ticket));
        mockTaskQuerySingleResult(null);

        TicketRequest request = buildVerifyRequest(ticket, "stale-task-1");

        TicketResponse response = fixture.service.executeWorkflowAction(request);

        assertThat(response).isNotNull();
        assertThat(response.getUid()).isEqualTo(ticket.getUid());
        assertThat(response.getStatus()).isEqualTo(TicketStatusEnum.VERIFIED_OK.name());
        // 终态守卫应直接短路，不触碰任务查询
        verify(fixture.taskService, Mockito.never()).createTaskQuery();
    }

    @Test
    void terminalClosedTicketShouldReturnCurrentTicketIdempotently() {
        TicketEntity ticket = buildTicket(TicketStatusEnum.CLOSED);
        when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(java.util.Optional.of(ticket));
        mockTaskQuerySingleResult(null);

        TicketResponse response = fixture.service.executeWorkflowAction(buildVerifyRequest(ticket, null));

        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(TicketStatusEnum.CLOSED.name());
        verify(fixture.taskService, Mockito.never()).createTaskQuery();
    }

    @Test
    void duplicateSubmitOnEndedTaskShouldReturnCurrentTicketWhenReopened() {
        // 场景：访客点「未解决」后双击残留按钮——工单已 REOPENED（非终态），旧 taskId 已完成
        TicketEntity ticket = buildTicket(TicketStatusEnum.REOPENED);
        when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(java.util.Optional.of(ticket));
        mockTaskQuerySingleResult(null);
        HistoricTaskInstance historicTask = mock(HistoricTaskInstance.class);
        when(historicTask.getProcessInstanceId()).thenReturn(ticket.getProcessInstanceId());
        when(historicTaskInstanceQuery.singleResult()).thenReturn(historicTask);

        TicketResponse response = fixture.service.executeWorkflowAction(buildVerifyRequest(ticket, "ended-task-1"));

        assertThat(response).isNotNull();
        assertThat(response.getUid()).isEqualTo(ticket.getUid());
        assertThat(response.getStatus()).isEqualTo(TicketStatusEnum.REOPENED.name());
    }

    @Test
    void unknownTaskIdShouldStillThrowOriginalError() {
        TicketEntity ticket = buildTicket(TicketStatusEnum.REOPENED);
        when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(java.util.Optional.of(ticket));
        mockTaskQuerySingleResult(null);
        when(historicTaskInstanceQuery.singleResult()).thenReturn(null);

        assertThatThrownBy(() -> fixture.service.executeWorkflowAction(buildVerifyRequest(ticket, "not-exist-task")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("任务不存在或已结束");
    }

    @Test
    void endedTaskFromOtherProcessInstanceShouldStillThrowOriginalError() {
        TicketEntity ticket = buildTicket(TicketStatusEnum.REOPENED);
        when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(java.util.Optional.of(ticket));
        mockTaskQuerySingleResult(null);
        HistoricTaskInstance historicTask = mock(HistoricTaskInstance.class);
        when(historicTask.getProcessInstanceId()).thenReturn("process-other");
        when(historicTaskInstanceQuery.singleResult()).thenReturn(historicTask);

        assertThatThrownBy(() -> fixture.service.executeWorkflowAction(buildVerifyRequest(ticket, "ended-task-1")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("任务不存在或已结束");
    }

    @Test
    void activeTaskFromOtherProcessInstanceShouldStillThrow() {
        TicketEntity ticket = buildTicket(TicketStatusEnum.REOPENED);
        when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(java.util.Optional.of(ticket));
        org.flowable.task.api.Task task = mock(org.flowable.task.api.Task.class);
        when(task.getProcessInstanceId()).thenReturn("process-other");
        mockTaskQuerySingleResult(task);

        assertThatThrownBy(() -> fixture.service.executeWorkflowAction(buildVerifyRequest(ticket, "active-task-1")))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("任务不属于该工单流程实例");
    }

    private void mockTaskQuerySingleResult(org.flowable.task.api.Task task) {
        when(taskQuery.singleResult()).thenReturn(task);
    }

    private static TicketRequest buildVerifyRequest(TicketEntity ticket, String taskId) {
        TicketRequest request = new TicketRequest();
        request.setUid(ticket.getUid());
        request.setTaskId(taskId);
        request.setActionKey("COMPLETE_VERIFIED");
        request.setAssigneeUid("reporter-1");
        return request;
    }

    private static TicketEntity buildTicket(TicketStatusEnum status) {
        UserProtobuf reporter = UserProtobuf.builder()
                .uid("reporter-1")
                .nickname("Reporter")
                .build();
        TicketEntity ticket = TicketEntity.builder()
                .uid("ticket-1")
                .orgUid("org-1")
                .title("Printer issue")
                .reporter(reporter.toJson())
                .processInstanceId("process-1")
                .status(status.name())
                .build();
        ticket.setCreatedAt(ZonedDateTime.parse("2026-09-11T10:00:00Z"));
        return ticket;
    }

    /**
     * TicketConvertUtils.convertToResponse 依赖 ApplicationContextHolder 获取 ModelMapper，
     * 单测环境无 Spring 上下文，反射注入 mock ApplicationContext。
     */
    private static void injectMockApplicationContext() throws Exception {
        ApplicationContext appContext = mock(ApplicationContext.class);
        ModelMapper modelMapper = mock(ModelMapper.class);
        when(appContext.getBean(ModelMapper.class)).thenReturn(modelMapper);
        when(modelMapper.map(any(TicketEntity.class), eq(TicketResponse.class))).thenAnswer(invocation -> {
            TicketEntity source = invocation.getArgument(0);
            TicketResponse response = new TicketResponse();
            response.setUid(source.getUid());
            response.setStatus(source.getStatus());
            return response;
        });
        Field field = ApplicationContextHolder.class.getDeclaredField("context");
        field.setAccessible(true);
        field.set(null, appContext);
    }

    private static void resetApplicationContext() throws Exception {
        Field field = ApplicationContextHolder.class.getDeclaredField("context");
        field.setAccessible(true);
        field.set(null, null);
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
