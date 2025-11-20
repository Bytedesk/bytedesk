package com.bytedesk.service.queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.queue_member.QueueMemberStatusEnum;
import com.bytedesk.service.queue_member.mq.QueueMemberMessageService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.queue.notification.QueueNotificationService;

@ExtendWith(MockitoExtension.class)
class QueueServiceTest {

    @Mock
    private QueueMemberRestService queueMemberRestService;

    @Mock
    private AgentRestService agentRestService;

    @Mock
    private ThreadRestService threadRestService;

    @Mock
    private QueueMemberMessageService queueMemberMessageService;

    @Mock
    private BytedeskEventPublisher bytedeskEventPublisher;

    @Mock
    private QueueRepository queueRepository;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private QueueNotificationService queueNotificationService;

    private QueueService queueService;

    @BeforeEach
    void setUp() {
        queueService = new QueueService(
                queueMemberRestService,
                agentRestService,
                threadRestService,
                queueMemberMessageService,
                bytedeskEventPublisher,
                queueRepository,
                uidUtils,
                queueNotificationService);
    }

    @Test
    void assignNextAgentQueueMemberReturnsEmptyWhenAgentMissing() {
        when(agentRestService.findByUid("agent-missing")).thenReturn(Optional.empty());

        Optional<QueueService.QueueAssignmentResult> result = queueService.assignNextAgentQueueMember("agent-missing");

        assertThat(result).isEmpty();
        verify(agentRestService).findByUid("agent-missing");
        verifyNoInteractions(queueMemberRestService, queueRepository, threadRestService,
                queueMemberMessageService, bytedeskEventPublisher, uidUtils);
    }

    @Test
    void assignNextAgentQueueMemberAssignsOldestQueueMember() {
        AgentEntity agent = buildAgent("agent-1", "org-1");
        QueueEntity agentQueue = QueueEntity.builder()
                .uid("queue-agent-1")
                .nickname("Agent Queue")
                .status(QueueStatusEnum.ACTIVE.name())
                .build();
        agentQueue.setOrgUid("org-1");

        ThreadEntity thread = buildThread("thread-1", "org/agent/agent-1/visitor-1", "org-1");
        QueueMemberEntity member = QueueMemberEntity.builder()
                .uid("member-1")
                .status(QueueMemberStatusEnum.QUEUING.name())
                .queueNumber(1)
                .build();
        member.setThread(thread);
        member.setAgentQueue(agentQueue);
        member.setOrgUid("org-1");

        when(agentRestService.findByUid("agent-1")).thenReturn(Optional.of(agent));
        when(queueRepository.findFirstByTopicAndDayAndDeletedFalseOrderByCreatedAtDesc(eq("org/queue/agent-1"),
                anyString()))
                .thenReturn(Optional.of(agentQueue));
        when(queueMemberRestService.findEarliestAgentQueueMemberForUpdate("queue-agent-1"))
                .thenReturn(Optional.of(member));
        when(threadRestService.save(any(ThreadEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(queueMemberRestService.save(any(QueueMemberEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Optional<QueueService.QueueAssignmentResult> resultOptional = queueService
                .assignNextAgentQueueMember("agent-1");

        assertThat(resultOptional).isPresent();
        QueueService.QueueAssignmentResult result = resultOptional.get();
        assertEquals("agent-1", result.agentUid());
        assertEquals("thread-1", result.threadUid());
        assertEquals("member-1", result.queueMemberUid());
        assertEquals(ThreadProcessStatusEnum.CHATTING.name(), thread.getStatus(),
                "Thread status should advance to CHATTING");
        assertEquals(QueueMemberStatusEnum.ASSIGNED.name(), member.getStatus(),
                "Queue member should be marked as assigned");

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Map<String, Object>> updatesCaptor = ArgumentCaptor.forClass(Map.class);
        verify(queueMemberMessageService).sendUpdateMessage(eq(member), updatesCaptor.capture());
        Map<String, Object> updates = updatesCaptor.getValue();
        assertThat(updates.get("agentAutoAcceptThread")).isEqualTo(Boolean.TRUE);
        assertThat(updates.get("status")).isEqualTo(QueueMemberStatusEnum.ASSIGNED.name());

        verify(threadRestService).save(thread);
        verify(queueMemberRestService).save(member);
        verify(queueMemberRestService).findEarliestAgentQueueMemberForUpdate("queue-agent-1");
        verify(queueMemberMessageService).sendUpdateMessage(eq(member), anyMap());
        verify(bytedeskEventPublisher, times(2)).publishEvent(any());
    }

    private AgentEntity buildAgent(String uid, String orgUid) {
        UserEntity user = new UserEntity();
        user.setUid("user-" + uid);
        user.setUsername("user-" + uid);

        MemberEntity member = MemberEntity.builder()
                .uid("member-" + uid)
                .user(user)
                .build();

        AgentEntity agent = AgentEntity.builder()
                .uid(uid)
                .nickname("Agent " + uid)
                .status("AVAILABLE")
                .build();
        agent.setOrgUid(orgUid);
        agent.setMember(member);
        return agent;
    }

    private ThreadEntity buildThread(String uid, String topic, String orgUid) {
        ThreadEntity thread = new ThreadEntity();
        thread.setUid(uid);
        thread.setTopic(topic);
        thread.setOrgUid(orgUid);
        thread.setType(ThreadTypeEnum.AGENT.name());
        thread.setStatus(ThreadProcessStatusEnum.QUEUING.name());
        thread.setCreatedAt(ZonedDateTime.now());
        return thread;
    }
}
