package com.bytedesk.ticket.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.ZonedDateTime;
import java.util.Map;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.task.Comment;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.flowable.task.api.Task;

import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.member.MemberRestService;
import com.bytedesk.core.rbac.user.UserRestService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic_subscription.TopicSubscriptionRestService;
import com.bytedesk.service.visitor.VisitorRestService;
import com.bytedesk.ticket.process.ProcessRepository;
import com.bytedesk.ticket.service.TicketNotificationService;
import com.bytedesk.ticket.ticket.assignment.TicketAssignmentService;
import com.bytedesk.ticket.ticket.enums.TicketStatusEnum;

class TicketServiceWorkflowCloseTest {

    @Test
        @SuppressWarnings("unchecked")
    void closeWorkflowTaskShouldKeepClosedStatusInsteadOfNodeCompleteStatus() throws Exception {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        TicketRequest request = new TicketRequest();
        Task task = mock(Task.class);
        JSONObject nodeData = new JSONObject();
        JSONObject actionConfig = new JSONObject();

        nodeData.put("ticketStatusOnComplete", TicketStatusEnum.RESOLVED.name());

        when(task.getId()).thenReturn("task-1");
        when(task.getAssignee()).thenReturn("agent-1");
        when(task.getTaskDefinitionKey()).thenReturn("processTicket");
        when(fixture.taskService.addComment(any(), any(), any(), any())).thenReturn(mock(Comment.class));

        invokeCloseWorkflowTask(fixture.service, ticket, task, request, "agent-1",
                buildRuntimeContext(nodeData, actionConfig));

        ArgumentCaptor<Map<String, Object>> variablesCaptor = (ArgumentCaptor<Map<String, Object>>) (ArgumentCaptor<?>) ArgumentCaptor
                .forClass(Map.class);
        verify(fixture.taskService).complete(org.mockito.Mockito.eq("task-1"), variablesCaptor.capture());

        assertThat(ticket.getStatus()).isEqualTo(TicketStatusEnum.CLOSED.name());
        assertThat(variablesCaptor.getValue())
                .containsEntry("status", TicketStatusEnum.CLOSED.name())
                .containsEntry("closedBy", "agent-1");
        verify(fixture.ticketSLAService).completeResolution(ticket, "agent-1");
    }

    private static TicketEntity buildTicket() {
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
                .status(TicketStatusEnum.PROCESSING.name())
                .build();
        ticket.setCreatedAt(ZonedDateTime.parse("2026-09-09T10:00:00Z"));
        return ticket;
    }

    private static Object buildRuntimeContext(JSONObject nodeData, JSONObject actionConfig) throws Exception {
        Class<?> runtimeContextClass = Class.forName(
                "com.bytedesk.ticket.ticket.TicketService$TicketWorkflowRuntimeContext");
        Constructor<?> constructor = runtimeContextClass.getDeclaredConstructor(
                JSONObject.class,
                JSONObject.class,
                JSONObject.class,
                JSONObject.class,
                String.class,
                String.class);
        constructor.setAccessible(true);
        return constructor.newInstance(null, null, nodeData, actionConfig, "CLOSE", "close");
    }

    private static void invokeCloseWorkflowTask(TicketService service, TicketEntity ticket, Task task,
            TicketRequest request, String operatorUid, Object runtimeContext) throws Exception {
        Class<?> runtimeContextClass = runtimeContext.getClass();
        Method method = TicketService.class.getDeclaredMethod(
                "closeWorkflowTask",
                TicketEntity.class,
                Task.class,
                TicketRequest.class,
                String.class,
                runtimeContextClass);
        method.setAccessible(true);
        method.invoke(service, ticket, task, request, operatorUid, runtimeContext);
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