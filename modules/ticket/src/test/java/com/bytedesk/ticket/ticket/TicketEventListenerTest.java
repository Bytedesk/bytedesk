package com.bytedesk.ticket.ticket;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.notification.NotificationRequest;
import com.bytedesk.core.notification.NotificationService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRepository;
import com.bytedesk.ticket.service.TicketNotificationService;

class TicketEventListenerTest {

    @Test
    void notifyNewTicketShouldDispatchNotificationToReporterAndWorkgroupUsers() {
        NotificationService notificationService = mock(NotificationService.class);
        WorkgroupRepository workgroupRepository = mock(WorkgroupRepository.class);
        TicketNotificationService ticketNotificationService = new TicketNotificationService(notificationService, workgroupRepository);

        AgentEntity agentA = buildAgent("agent-a", "user-a");
        AgentEntity agentB = buildAgent("agent-b", "user-b");
        WorkgroupEntity workgroup = WorkgroupEntity.builder()
                .uid("wg-1")
                .orgUid("org-1")
                .agents(List.of(agentA, agentB))
                .build();
        TicketEntity ticket = buildTicket("wg-1");

        when(workgroupRepository.findByUid("wg-1")).thenReturn(Optional.of(workgroup));

                ticketNotificationService.notifyNewTicket(ticket);

                verify(notificationService, times(3)).dispatchSystemNotificationToUser(any(NotificationRequest.class));
    }

    private static TicketEntity buildTicket(String workgroupUid) {
        UserProtobuf reporter = UserProtobuf.builder().uid("visitor-1").nickname("Visitor").build();
        return TicketEntity.builder()
                .uid("ticket-1")
                .orgUid("org-1")
                .workgroupUid(workgroupUid)
                .title("Payment issue")
                .ticketNumber("TK-1001")
                .status(TicketStatusEnum.NEW.name())
                .priority(TicketPriorityEnum.HIGH.name())
                .type(TicketTypeEnum.EXTERNAL.name())
                .reporter(reporter.toJson())
                .build();
    }

    private static AgentEntity buildAgent(String agentUid, String userUid) {
                UserEntity user = UserEntity.builder().uid(userUid).username(userUid).build();
        MemberEntity member = MemberEntity.builder().uid("member-" + userUid).orgUid("org-1").user(user).build();
        return AgentEntity.builder()
                .uid(agentUid)
                .orgUid("org-1")
                .member(member)
                .nickname(agentUid)
                .build();
    }
}