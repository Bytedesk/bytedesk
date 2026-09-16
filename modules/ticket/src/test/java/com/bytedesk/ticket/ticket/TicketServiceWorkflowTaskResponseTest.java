package com.bytedesk.ticket.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.Task;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationContext;

import com.alibaba.fastjson2.JSONObject;
import com.bytedesk.core.member.MemberRestService;
import com.bytedesk.core.utils.ApplicationContextHolder;
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

        @Test
        void queryWorkflowActionsShouldHealStaleTaskAssigneeForProcessingNode() {
                Fixture fixture = new Fixture();
                TicketEntity ticket = buildTicketWithAssignee();
                ticket.setStatus(TicketStatusEnum.REOPENED.name());
                TicketRequest request = new TicketRequest();
                request.setUid(ticket.getUid());
                request.setAssigneeUid("member-ticket-1");

                Task task = mock(Task.class);
                TaskQuery taskQuery = mock(TaskQuery.class);
                when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(Optional.of(ticket));
                when(fixture.processRepository.findByUid(ticket.getProcessEntityUid())).thenReturn(Optional.empty());
                when(fixture.taskService.createTaskQuery()).thenReturn(taskQuery);
                when(taskQuery.processInstanceId(ticket.getProcessInstanceId())).thenReturn(taskQuery);
                when(taskQuery.active()).thenReturn(taskQuery);
                when(taskQuery.list()).thenReturn(List.of(task));
                when(task.getId()).thenReturn("task-1");
                when(task.getName()).thenReturn("工单处理中");
                when(task.getTaskDefinitionKey()).thenReturn("processTicket");
                // 陈旧归属：任务仍指向转派前的原处理人
                when(task.getAssignee()).thenReturn("member-admin-1");
                when(task.getDelegationState()).thenReturn(null);

                List<TicketWorkflowTaskResponse> responses = fixture.service.queryWorkflowActions(request);

                // 存量自愈：处理人节点陈旧归属被重置回当前处理人（member-ticket-1）
                verify(fixture.taskService).setAssignee("task-1", "member-ticket-1");
                assertThat(responses).hasSize(1);
        }

        @Test
        void queryWorkflowActionsShouldNotHealWhenTaskAssigneeConsistent() {
                Fixture fixture = new Fixture();
                TicketEntity ticket = buildTicketWithAssignee();
                TicketRequest request = new TicketRequest();
                request.setUid(ticket.getUid());
                request.setAssigneeUid("member-ticket-1");

                Task task = mock(Task.class);
                TaskQuery taskQuery = mock(TaskQuery.class);
                when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(Optional.of(ticket));
                when(fixture.processRepository.findByUid(ticket.getProcessEntityUid())).thenReturn(Optional.empty());
                when(fixture.taskService.createTaskQuery()).thenReturn(taskQuery);
                when(taskQuery.processInstanceId(ticket.getProcessInstanceId())).thenReturn(taskQuery);
                when(taskQuery.active()).thenReturn(taskQuery);
                when(taskQuery.list()).thenReturn(List.of(task));
                when(task.getId()).thenReturn("task-1");
                when(task.getName()).thenReturn("工单处理中");
                when(task.getTaskDefinitionKey()).thenReturn("processTicket");
                when(task.getAssignee()).thenReturn("member-ticket-1");
                when(task.getDelegationState()).thenReturn(null);

                assertThat(fixture.service.queryWorkflowActions(request)).hasSize(1);
                // 归属一致时不产生任何写操作（幂等）
                verify(fixture.taskService, never()).setAssignee(anyString(), anyString());
        }

        @Test
        void queryWorkflowActionsShouldNotHealUnassignedTask() {
                Fixture fixture = new Fixture();
                TicketEntity ticket = buildTicketWithAssignee();
                TicketRequest request = new TicketRequest();
                request.setUid(ticket.getUid());
                request.setAssigneeUid("member-ticket-1");

                Task task = mock(Task.class);
                TaskQuery taskQuery = mock(TaskQuery.class);
                when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(Optional.of(ticket));
                when(fixture.processRepository.findByUid(ticket.getProcessEntityUid())).thenReturn(Optional.empty());
                when(fixture.taskService.createTaskQuery()).thenReturn(taskQuery);
                when(taskQuery.processInstanceId(ticket.getProcessInstanceId())).thenReturn(taskQuery);
                when(taskQuery.active()).thenReturn(taskQuery);
                when(taskQuery.list()).thenReturn(List.of(task));
                when(task.getId()).thenReturn("task-1");
                when(task.getName()).thenReturn("工单处理中");
                when(task.getTaskDefinitionKey()).thenReturn("processTicket");
                // 未分配任务不视为陈旧，保持待认领语义
                when(task.getAssignee()).thenReturn(null);
                when(task.getDelegationState()).thenReturn(null);

                assertThat(fixture.service.queryWorkflowActions(request)).hasSize(1);
                verify(fixture.taskService, never()).setAssignee(anyString(), anyString());
        }

        @Test
        void executeWorkflowActionTransferShouldSyncAssigneeUidVariable() {
                initConvertUtilsContext();
                Fixture fixture = new Fixture();
                TicketEntity ticket = buildTicketWithAssignee();
                TicketRequest request = new TicketRequest();
                request.setUid(ticket.getUid());
                request.setOrgUid(ticket.getOrgUid());
                request.setActionKey("TRANSFER");
                request.setAssigneeUid("member-ticket-1");
                request.setTargetAssigneeUid("member-target-1");

                Task task = mock(Task.class);
                TaskQuery taskQuery = mock(TaskQuery.class);
                when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(Optional.of(ticket));
                when(fixture.processRepository.findByUid(ticket.getProcessEntityUid())).thenReturn(Optional.empty());
                when(fixture.taskService.createTaskQuery()).thenReturn(taskQuery);
                when(taskQuery.processInstanceId(ticket.getProcessInstanceId())).thenReturn(taskQuery);
                when(taskQuery.active()).thenReturn(taskQuery);
                when(taskQuery.list()).thenReturn(List.of(task));
                when(task.getId()).thenReturn("task-1");
                when(task.getName()).thenReturn("工单处理中");
                when(task.getTaskDefinitionKey()).thenReturn("processTicket");
                when(task.getAssignee()).thenReturn("member-ticket-1");
                when(task.getDelegationState()).thenReturn(null);
                when(fixture.memberRestService.findByUid("member-target-1"))
                                .thenReturn(Optional.of(com.bytedesk.core.member.MemberEntity.builder()
                                                .uid("member-target-1").nickname("Target Agent").build()));
                when(fixture.taskService.addComment(anyString(), anyString(), anyString(), anyString()))
                                .thenReturn(mock(Comment.class));

                TicketResponse response = fixture.service.executeWorkflowAction(request);

                assertThat(response).isNotNull();
                verify(fixture.taskService).setAssignee("task-1", "member-target-1");
                // 转派后流程变量 assigneeUid 必须同步为目标处理人，否则回退重建任务携带陈旧归属
                @SuppressWarnings("unchecked")
                ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
                verify(fixture.runtimeService).setVariables(eq(ticket.getProcessInstanceId()), captor.capture());
                assertThat(captor.getValue())
                                .containsEntry(TicketConsts.TICKET_VARIABLE_ASSIGNEE_UID, "member-target-1")
                                .containsEntry(TicketConsts.TICKET_VARIABLE_STATUS, TicketStatusEnum.TRANSFERRED.name());
        }

        @Test
        void executeWorkflowActionTransferDepartmentShouldClearAssigneeVariablesWhenNoTarget() {
                initConvertUtilsContext();
                Fixture fixture = new Fixture();
                TicketEntity ticket = buildTicketWithAssignee();
                TicketRequest request = new TicketRequest();
                request.setUid(ticket.getUid());
                request.setOrgUid(ticket.getOrgUid());
                request.setActionKey("TRANSFER_DEPARTMENT");
                request.setAssigneeUid("member-ticket-1");
                request.setTargetDepartmentUid("dept-2");

                Task task = mock(Task.class);
                TaskQuery taskQuery = mock(TaskQuery.class);
                when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(Optional.of(ticket));
                when(fixture.processRepository.findByUid(ticket.getProcessEntityUid())).thenReturn(Optional.empty());
                when(fixture.taskService.createTaskQuery()).thenReturn(taskQuery);
                when(taskQuery.processInstanceId(ticket.getProcessInstanceId())).thenReturn(taskQuery);
                when(taskQuery.active()).thenReturn(taskQuery);
                when(taskQuery.list()).thenReturn(List.of(task));
                when(task.getId()).thenReturn("task-1");
                when(task.getName()).thenReturn("工单处理中");
                when(task.getTaskDefinitionKey()).thenReturn("processTicket");
                when(task.getAssignee()).thenReturn(null);
                when(task.getDelegationState()).thenReturn(null);
                when(fixture.taskService.addComment(anyString(), anyString(), anyString(), anyString()))
                                .thenReturn(mock(Comment.class));

                TicketResponse response = fixture.service.executeWorkflowAction(request);

                assertThat(response).isNotNull();
                verify(fixture.taskService).unclaim("task-1");
                assertThat(ticket.getAssignee()).isNull();
                // 仅转部门不指定处理人：处理人相关流程变量同步清空，避免陈旧 assigneeUid 被回退捡回
                @SuppressWarnings("unchecked")
                ArgumentCaptor<Map<String, Object>> captor = ArgumentCaptor.forClass(Map.class);
                verify(fixture.runtimeService).setVariables(eq(ticket.getProcessInstanceId()), captor.capture());
                assertThat(captor.getValue())
                                .containsEntry(TicketConsts.TICKET_VARIABLE_ASSIGNEE_UID, "")
                                .containsEntry(TicketConsts.TICKET_VARIABLE_ASSIGNEE, "")
                                .containsEntry(TicketConsts.TICKET_VARIABLE_DEPARTMENT_UID, "dept-2");
        }

    private static void initConvertUtilsContext() {
        // TicketConvertUtils.convertToResponse 静态依赖 ApplicationContextHolder 获取 ModelMapper；
        // 分类/设置/流程名增强均有 try/catch 兜底，仅需提供 ModelMapper 即可在纯单测中完成转换
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