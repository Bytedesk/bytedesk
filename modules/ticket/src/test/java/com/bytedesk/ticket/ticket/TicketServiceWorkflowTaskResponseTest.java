package com.bytedesk.ticket.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;

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
import com.bytedesk.ticket.ticket.dto.TicketWorkflowTaskResponse;
import com.bytedesk.ticket.ticket.enums.TicketStatusEnum;

class TicketServiceWorkflowTaskResponseTest {

    @Test
    void buildWorkflowTaskResponseShouldUseTicketAssigneeWhenTaskAssigneeBlank() throws Exception {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicketWithAssignee();
        Task task = mock(Task.class);

        when(task.getId()).thenReturn("task-1");
        when(task.getName()).thenReturn("Wait Claim");
        when(task.getTaskDefinitionKey()).thenReturn("waitClaim");
        when(task.getAssignee()).thenReturn(null);

        TicketWorkflowTaskResponse response = invokeBuildWorkflowTaskResponse(
                fixture.service,
                ticket,
                task,
                "member-other-1",
                new JSONObject());

        assertThat(response.getAssignee()).isEqualTo("member-ticket-1");
        assertThat(response.getActionable()).isFalse();
        assertThat(response.getActions()).isEmpty();
    }

        @Test
        void queryWorkflowActionsShouldAssertTicketVisibleBeforeReadingTasks() {
                Fixture fixture = new Fixture();
                TicketEntity ticket = buildTicketWithAssignee();
                TicketRequest request = new TicketRequest();
                request.setUid(ticket.getUid());
                request.setAssigneeUid("member-ticket-1");
                TaskQuery taskQuery = mock(TaskQuery.class);

                when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(Optional.of(ticket));
                when(fixture.taskService.createTaskQuery()).thenReturn(taskQuery);
                when(taskQuery.processInstanceId(ticket.getProcessInstanceId())).thenReturn(taskQuery);
                when(taskQuery.active()).thenReturn(taskQuery);
                when(taskQuery.list()).thenReturn(List.of());

                assertThat(fixture.service.queryWorkflowActions(request)).isEmpty();
                // 无 channel 的请求视为非管理后台渠道（adminPrivilege=false），管理员同受可见性限制
                verify(fixture.ticketRestService).assertTicketVisibleIfAuthenticated(eq(ticket), eq(false));
        }

        @Test
        void queryWorkflowActionsShouldReturnEmptyForClosedTicket() {
                Fixture fixture = new Fixture();
                TicketEntity ticket = TicketEntity.builder()
                                .uid("ticket-3")
                                .orgUid("org-1")
                                .processEntityUid("process-3")
                                .processInstanceId("process-instance-3")
                                .status(TicketStatusEnum.CLOSED.name())
                                .build();
                TicketRequest request = new TicketRequest();
                request.setUid(ticket.getUid());
                request.setAssigneeUid("member-ticket-1");

                when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(Optional.of(ticket));

                assertThat(fixture.service.queryWorkflowActions(request)).isEmpty();
                verify(fixture.ticketRestService).assertTicketVisibleIfAuthenticated(eq(ticket), eq(false));
                // 终态工单不应再查询活动任务
                verify(fixture.taskService, never()).createTaskQuery();
        }

        @Test
        void queryTicketActivityHistoryShouldAssertTicketVisibleBeforeReadingHistory() {
                Fixture fixture = new Fixture();
                TicketEntity ticket = buildTicketWithoutProcessInstance();
                TicketRequest request = new TicketRequest();
                request.setUid(ticket.getUid());

                when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(Optional.of(ticket));

                assertThat(fixture.service.queryTicketActivityHistory(request)).isEmpty();
                verify(fixture.ticketRestService).assertTicketVisibleIfAuthenticated(eq(ticket), eq(false));
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

    private static TicketEntity buildTicketWithAssignee() {
        UserProtobuf assignee = UserProtobuf.builder()
                .uid("member-ticket-1")
                .nickname("Assigned Agent")
                .build();
        TicketEntity ticket = TicketEntity.builder()
                .uid("ticket-1")
                .orgUid("org-1")
                .processEntityUid("process-1")
                .processInstanceId("process-instance-1")
                .status(TicketStatusEnum.ASSIGNED.name())
                .build();
        ticket.setAssignee(assignee.toJson());
        return ticket;
    }

        private static TicketEntity buildTicketWithoutProcessInstance() {
                return TicketEntity.builder()
                                .uid("ticket-2")
                                .orgUid("org-1")
                                .processEntityUid("process-2")
                                .status(TicketStatusEnum.NEW.name())
                                .build();
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