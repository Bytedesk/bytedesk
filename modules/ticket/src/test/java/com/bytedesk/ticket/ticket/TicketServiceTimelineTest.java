package com.bytedesk.ticket.ticket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.task.Comment;
import org.junit.jupiter.api.Test;

import com.bytedesk.core.member.MemberRestService;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserRestService;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.topic_subscription.TopicSubscriptionRestService;
import com.bytedesk.service.visitor.VisitorRestService;
import com.bytedesk.ticket.constant.I18TicketConsts;
import com.bytedesk.ticket.process.ProcessRepository;
import com.bytedesk.ticket.service.TicketNotificationService;
import com.bytedesk.ticket.ticket.assignment.TicketAssignmentService;
import com.bytedesk.ticket.ticket.dto.TicketTimelineStepResponse;

class TicketServiceTimelineTest {

    @Test
    void queryTicketTimelineShouldReturnCreateAndBusinessCommentsOnly() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        TicketRequest request = new TicketRequest();
        request.setUid(ticket.getUid());

        Comment claimed = mockComment("comment-1", "CLAIMED", "agent-1", "工单已认领",
                Date.from(ticket.getCreatedAt().plusMinutes(1).toInstant()));
        Comment completed = mockComment("comment-2", "COMPLETE", "agent-1", "处理完成",
                Date.from(ticket.getCreatedAt().plusMinutes(2).toInstant()));
        Comment ignored = mockComment("comment-3", "NOTE", "agent-1", "仅备注",
                Date.from(ticket.getCreatedAt().plusMinutes(3).toInstant()));

        when(fixture.ticketRestService.findByUid(ticket.getUid())).thenReturn(Optional.of(ticket));
        when(fixture.taskService.getProcessInstanceComments(ticket.getProcessInstanceId()))
                .thenReturn(List.of(completed, ignored, claimed));

        List<TicketTimelineStepResponse> steps = fixture.service.queryTicketTimeline(request);

        assertThat(steps).hasSize(3);
        assertThat(steps).extracting(s -> s.getActionKey())
                .containsExactly("CREATE", "CLAIMED", "COMPLETE");
        assertThat(steps).extracting(s -> s.getTitleKey())
                .containsExactly(
                        I18TicketConsts.I18N_TICKET_ACTION_CREATE,
                        "ticket.status.claimed",
                        I18TicketConsts.I18N_TICKET_ACTION_COMPLETE);
        assertThat(steps.get(0).getAssignee()).isEqualTo("reporter-1");
        assertThat(steps.get(0).getAssigneeName()).isEqualTo("Reporter");
        assertThat(steps.get(1).getDescription()).isEqualTo("工单已认领");
        assertThat(steps.get(2).getTitle()).isEqualTo("已解决");
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
                .build();
        ticket.setCreatedAt(ZonedDateTime.parse("2026-09-09T10:00:00Z"));
        return ticket;
    }

    private static Comment mockComment(String id, String type, String userId, String message, Date time) {
        Comment comment = mock(Comment.class);
        when(comment.getId()).thenReturn(id);
        when(comment.getType()).thenReturn(type);
        when(comment.getUserId()).thenReturn(userId);
        when(comment.getFullMessage()).thenReturn(message);
        when(comment.getTime()).thenReturn(time);
        return comment;
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