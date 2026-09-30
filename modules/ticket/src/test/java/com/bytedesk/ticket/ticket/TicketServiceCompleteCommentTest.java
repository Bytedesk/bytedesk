package com.bytedesk.ticket.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.time.ZonedDateTime;
import java.util.List;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
 * complete 类型动作未填写处理意见/原因时的默认备注文案验证：
 * - COMPLETE（客服标记已解决）→ 「客服标记工单为已解决」
 * - COMPLETE_VERIFIED（访客确认解决）→ 「访客确认工单已解决」
 * - COMPLETE_REJECTED（访客标记未解决）→ 「访客标记工单为未解决」
 * - 自定义 type=complete 动作 → 保留「流程任务已完成」兼容回退
 * - 填写 processComment 时仍以用户输入为准
 */
class TicketServiceCompleteCommentTest {

    private Fixture fixture;
    private TaskQuery taskQuery;
    private Task task;
    private Comment comment;

    @BeforeEach
    void setUp() throws Exception {
        fixture = new Fixture();
        taskQuery = mock(TaskQuery.class);
        task = mock(Task.class);
        comment = mock(Comment.class);

        when(task.getId()).thenReturn("task-1");
        when(task.getProcessInstanceId()).thenReturn("process-1");
        when(task.getTaskDefinitionKey()).thenReturn("customerVerify");

        when(fixture.taskService.createTaskQuery()).thenReturn(taskQuery);
        when(taskQuery.taskId(any())).thenReturn(taskQuery);
        when(taskQuery.processInstanceId(any())).thenReturn(taskQuery);
        when(taskQuery.active()).thenReturn(taskQuery);
        when(taskQuery.singleResult()).thenReturn(task);
        when(taskQuery.list()).thenReturn(List.of());

        when(fixture.taskService.addComment(any(), any(), any(), any())).thenReturn(comment);

        injectMockApplicationContext();
    }

    @AfterEach
    void tearDown() throws Exception {
        resetApplicationContext();
    }

    @Test
    void agentCompleteWithoutCommentShouldWriteResolvedDefaultComment() {
        TicketEntity ticket = buildTicket(TicketStatusEnum.PROCESSING);
        when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(java.util.Optional.of(ticket));
        when(task.getAssignee()).thenReturn("agent-1");

        TicketResponse response = fixture.service
                .executeWorkflowAction(buildRequest(ticket, "COMPLETE", "agent-1", null));

        assertThat(response).isNotNull();
        verify(fixture.taskService).addComment(eq("task-1"), eq("process-1"), eq("COMPLETE"),
                eq("客服标记工单为已解决"));
    }

    @Test
    void reporterCompleteVerifiedShouldWriteVisitorConfirmedComment() {
        TicketEntity ticket = buildTicket(TicketStatusEnum.PROCESSING);
        when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(java.util.Optional.of(ticket));
        when(task.getAssignee()).thenReturn("reporter-1");

        TicketResponse response = fixture.service
                .executeWorkflowAction(buildRequest(ticket, "COMPLETE_VERIFIED", "reporter-1", null));

        assertThat(response).isNotNull();
        verify(fixture.taskService).addComment(eq("task-1"), eq("process-1"), eq("COMPLETE_VERIFIED"),
                eq("访客确认工单已解决"));
    }

    @Test
    void reporterCompleteRejectedShouldWriteVisitorRejectedComment() {
        TicketEntity ticket = buildTicket(TicketStatusEnum.PROCESSING);
        when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(java.util.Optional.of(ticket));
        when(task.getAssignee()).thenReturn("reporter-1");

        TicketResponse response = fixture.service
                .executeWorkflowAction(buildRequest(ticket, "COMPLETE_REJECTED", "reporter-1", null));

        assertThat(response).isNotNull();
        verify(fixture.taskService).addComment(eq("task-1"), eq("process-1"), eq("COMPLETE_REJECTED"),
                eq("访客标记工单为未解决"));
    }

    @Test
    void customCompleteActionShouldKeepLegacyFallbackComment() {
        TicketEntity ticket = buildTicket(TicketStatusEnum.PROCESSING);
        when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(java.util.Optional.of(ticket));
        when(task.getAssignee()).thenReturn("agent-1");

        TicketResponse response = fixture.service
                .executeWorkflowAction(buildRequest(ticket, "CUSTOM_COMPLETE", "agent-1", null));

        assertThat(response).isNotNull();
        verify(fixture.taskService).addComment(eq("task-1"), eq("process-1"), eq("CUSTOM_COMPLETE"),
                eq("流程任务已完成"));
    }

    @Test
    void processCommentShouldTakePrecedenceOverDefaultComment() {
        TicketEntity ticket = buildTicket(TicketStatusEnum.PROCESSING);
        when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(java.util.Optional.of(ticket));
        when(task.getAssignee()).thenReturn("reporter-1");

        TicketResponse response = fixture.service.executeWorkflowAction(
                buildRequest(ticket, "COMPLETE_VERIFIED", "reporter-1", "已在电话中与客户确认解决"));

        assertThat(response).isNotNull();
        verify(fixture.taskService).addComment(eq("task-1"), eq("process-1"), eq("COMPLETE_VERIFIED"),
                eq("已在电话中与客户确认解决"));
    }

    private static TicketRequest buildRequest(TicketEntity ticket, String actionKey, String operatorUid,
            String processComment) {
        TicketRequest request = new TicketRequest();
        request.setUid(ticket.getUid());
        request.setTaskId("task-1");
        request.setActionKey(actionKey);
        request.setAssigneeUid(operatorUid);
        if (processComment != null) {
            request.setProcessComment(processComment);
        }
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
        ticket.setCreatedAt(ZonedDateTime.parse("2026-09-30T10:00:00Z"));
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
