package com.bytedesk.ticket.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.TaskQuery;
import org.junit.jupiter.api.Test;

import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.ticket.service.TicketNotificationService;
import com.bytedesk.ticket.ticket.assignment.AssignmentResolutionResult;
import com.bytedesk.ticket.ticket.assignment.AssignmentSource;
import com.bytedesk.ticket.ticket.assignment.TicketAssignmentService;
import com.bytedesk.ticket.ticket.enums.TicketStatusEnum;
import com.bytedesk.ticket.ticket.enums.TicketTypeEnum;
import com.bytedesk.ticket.ticket.event.TicketCreateEvent;

/**
 * 建单事件与分配通知互斥回归（规划 8.2 第 9 条）：
 * 自动分配成功 → 创建监听器不再补发；未分配 → 补发一次。
 */
class TicketCreateNotificationFlowTest {

        @Test
        void autoAssignedTicketSkipsDuplicateNewTicketNotification() throws Exception {
                Fixture f = new Fixture();
                TicketEntity ticket = f.persistedTicket();
                // 已落库处理人（member uid）且分配成功
                ticket.setAssignee(com.bytedesk.core.rbac.user.UserProtobuf.builder()
                                .uid("member-assignee").build().toJson());
                when(f.ticketAssignmentService.autoAssign(any(TicketEntity.class), anyString()))
                                .thenReturn(AssignmentResolutionResult.resolved("member-assignee",
                                                AssignmentSource.GLOBAL_STRATEGY, "roundRobin", "ok", "pool"));

                f.listener().handleTicketCreateEvent(new TicketCreateEvent(ticket));

                // 互斥：监听器不再补发 notifyNewTicket（通知已由 applyAssignment 发出）
                verify(f.ticketNotificationService, times(0)).notifyNewTicket(any(TicketEntity.class));
                // 分配调用发生且流程实例已回写
                verify(f.ticketAssignmentService, times(1)).autoAssign(any(TicketEntity.class), anyString());
                assertThat(ticket.getProcessInstanceId()).isEqualTo("proc-1");
        }

        @Test
        void unresolvedAssignmentStillNotifiesOnce() throws Exception {
                Fixture f = new Fixture();
                TicketEntity ticket = f.persistedTicket();
                when(f.ticketAssignmentService.autoAssign(any(TicketEntity.class), anyString()))
                                .thenReturn(AssignmentResolutionResult.unresolved(AssignmentSource.GLOBAL_STRATEGY,
                                                "no candidates"));

                f.listener().handleTicketCreateEvent(new TicketCreateEvent(ticket));

                // 未成功分配 → 补发一次新工单通知（不多发）
                verify(f.ticketNotificationService, times(1)).notifyNewTicket(any(TicketEntity.class));
        }

        @Test
        void duplicateCreateEventWithExistingProcessSkipsEverything() throws Exception {
                Fixture f = new Fixture();
                TicketEntity committed = f.persistedTicket();
                committed.setProcessInstanceId("proc-existing");
                committed.setAssignee(com.bytedesk.core.rbac.user.UserProtobuf.builder()
                                .uid("member-assignee").build().toJson());
                // 重查返回已带流程实例的提交版
                when(f.ticketRepository.findByUid(committed.getUid())).thenReturn(Optional.of(committed));

                f.listener().handleTicketCreateEvent(new TicketCreateEvent(committed));

                // 重复事件：不启动流程、不通知
                verify(f.ticketNotificationService, times(0)).notifyNewTicket(any(TicketEntity.class));
                verify(f.ticketAssignmentService, times(0)).autoAssign(any(TicketEntity.class), anyString());
        }

        private static final class Fixture {
                final TicketRepository ticketRepository = mock(TicketRepository.class);
                final TicketRestService ticketRestService = mock(TicketRestService.class);
                final TicketNotificationService ticketNotificationService = mock(TicketNotificationService.class);
                final TicketAssignmentService ticketAssignmentService = mock(TicketAssignmentService.class);
                final TicketSLAService ticketSLAService = mock(TicketSLAService.class);
                final RuntimeService runtimeService = mock(RuntimeService.class);
                final TaskService taskService = mock(TaskService.class);

                TicketEventListener listener() {
                        return new TicketEventListener(
                                        runtimeService,
                                        ticketRestService,
                                        taskService,
                                        mock(ThreadRestService.class),
                                        ticketNotificationService,
                                        ticketAssignmentService,
                                        ticketRepository,
                                        ticketSLAService);
                }

                TicketEntity persistedTicket() {
                        TicketEntity ticket = TicketEntity.builder()
                                        .uid("ticket-flow-1")
                                        .orgUid("org-1")
                                        .type(TicketTypeEnum.EXTERNAL.name())
                                        .status(TicketStatusEnum.NEW.name())
                                        .workgroupUid("wg-1")
                                        .reporter(com.bytedesk.core.rbac.user.UserProtobuf.builder()
                                                        .uid("visitor-1").build().toJson())
                                        .build();
                    when(ticketRepository.findByUid(ticket.getUid())).thenReturn(Optional.of(ticket));
                    when(ticketRestService.findByUid(ticket.getUid())).thenReturn(Optional.of(ticket));
                    // save 直接返回受管实体
                    when(ticketRestService.save(any(TicketEntity.class)))
                                    .thenAnswer(invocation -> invocation.getArgument(0));
                    stubProcessStart();
                    return ticket;
                }

                /** 流程启动链路 mock：builder → processInstance(proc-1) → 无需完成任务（task=null 走告警分支） */
                private void stubProcessStart() {
                        org.flowable.engine.runtime.ProcessInstanceBuilder builder =
                                        mock(org.flowable.engine.runtime.ProcessInstanceBuilder.class);
                        when(runtimeService.createProcessInstanceBuilder()).thenReturn(builder);
                        when(builder.processDefinitionKey(anyString())).thenReturn(builder);
                        when(builder.tenantId(anyString())).thenReturn(builder);
                        when(builder.name(any())).thenReturn(builder);
                        when(builder.businessKey(anyString())).thenReturn(builder);
                        when(builder.variables(any())).thenReturn(builder);
                        ProcessInstance processInstance = mock(ProcessInstance.class);
                        when(processInstance.getId()).thenReturn("proc-1");
                        when(processInstance.getBusinessKey()).thenReturn("ticket-flow-1");
                        when(builder.start()).thenReturn(processInstance);
                        TaskQuery taskQuery = mock(TaskQuery.class);
                        when(taskService.createTaskQuery()).thenReturn(taskQuery);
                        when(taskQuery.processInstanceId(anyString())).thenReturn(taskQuery);
                        when(taskQuery.taskAssignee(anyString())).thenReturn(taskQuery);
                        when(taskQuery.singleResult()).thenReturn(null);
                }
        }
}
