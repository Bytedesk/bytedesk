package com.bytedesk.service.queue_member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.service.agent.AgentCapacityService;
import com.bytedesk.service.presence.PresenceFacadeService;
import com.bytedesk.service.queue.QueueService;
import com.bytedesk.service.workgroup.WorkgroupRestService;

@ExtendWith(MockitoExtension.class)
class QueueMemberEventListenerTest {

    @Mock
    private QueueMemberRestService queueMemberRestService;

    @Mock
    private ThreadRestService threadRestService;

    @Mock
    private IMessageSendService messageSendService;

    @Mock
    private WorkgroupRestService workgroupRestService;

    @Mock
    private PresenceFacadeService presenceFacadeService;

    @Mock
    private QueueService queueService;

    @Mock
    private AgentCapacityService agentCapacityService;

    @Mock
    private BytedeskEventPublisher bytedeskEventPublisher;

    @Mock
    private MessageRestService messageRestService;

    private QueueMemberEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new QueueMemberEventListener(
                queueMemberRestService,
                threadRestService,
                messageSendService,
                workgroupRestService,
                presenceFacadeService,
                queueService,
                agentCapacityService,
                bytedeskEventPublisher,
                messageRestService);
    }

    @Test
    void markVisitorMessagesRepliedByAgentShouldFallbackWithoutQueueMember() {
        ZonedDateTime threadCreatedAt = ZonedDateTime.parse("2026-04-09T10:00:00+08:00");
        ZonedDateTime visitorCreatedAt = threadCreatedAt.plusMinutes(1);
        ZonedDateTime agentCreatedAt = threadCreatedAt.plusMinutes(5);

        ThreadEntity thread = new ThreadEntity();
        thread.setUid("thread-1");
                thread.setTopic("topic-1");
        thread.setCreatedAt(threadCreatedAt);

        MessageEntity visitorMessage = new MessageEntity();
        visitorMessage.setUid("msg-visitor");
        visitorMessage.setThread(thread);
        visitorMessage.setCreatedAt(visitorCreatedAt);
        visitorMessage.setUser(UserProtobuf.builder()
                .uid("visitor-1")
                .type(UserTypeEnum.VISITOR.name())
                .build()
                .toJson());
        visitorMessage.setAgentReplied(false);

        MessageEntity agentMessage = new MessageEntity();
        agentMessage.setUid("msg-agent");
        agentMessage.setThread(thread);
        agentMessage.setCreatedAt(agentCreatedAt);
        agentMessage.setUser(UserProtobuf.builder()
                .uid("agent-1")
                .type(UserTypeEnum.AGENT.name())
                .build()
                .toJson());
        agentMessage.setAgentReplied(true);

        when(messageRestService.findByThreadTopicBetweenCreatedAt("topic-1", threadCreatedAt, agentCreatedAt))
                .thenReturn(List.of(visitorMessage, agentMessage));
        when(messageRestService.save(any(MessageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReflectionTestUtils.invokeMethod(listener,
                "markVisitorMessagesRepliedByAgent",
                thread,
                agentMessage,
                null,
                null);

        assertThat(visitorMessage.getAgentReplied()).isTrue();
        assertThat(visitorMessage.getAgentRepliedAt()).isEqualTo(agentCreatedAt);
        assertThat(visitorMessage.getAgentRepliedByUid()).isEqualTo("agent-1");

                verify(messageRestService).findByThreadTopicBetweenCreatedAt("topic-1", threadCreatedAt, agentCreatedAt);
        verify(messageRestService).save(visitorMessage);
        verify(messageRestService, never()).save(agentMessage);
    }

        @Test
        void markVisitorMessagesRepliedByAgentShouldMarkSameTopicVisitorThreads() {
                ZonedDateTime threadCreatedAt = ZonedDateTime.parse("2026-04-09T10:00:00+08:00");
                ZonedDateTime visitorCreatedAt = threadCreatedAt.plusMinutes(1);
                ZonedDateTime agentCreatedAt = threadCreatedAt.plusMinutes(5);

                ThreadEntity thread = new ThreadEntity();
                thread.setUid("thread-1");
                thread.setTopic("topic-1");
                thread.setCreatedAt(threadCreatedAt);

                ThreadEntity siblingThread = new ThreadEntity();
                siblingThread.setUid("thread-2");
                siblingThread.setTopic("topic-1");
                siblingThread.setCreatedAt(threadCreatedAt.plusMinutes(2));

                MessageEntity visitorMessage = new MessageEntity();
                visitorMessage.setUid("msg-visitor-1");
                visitorMessage.setThread(thread);
                visitorMessage.setCreatedAt(visitorCreatedAt);
                visitorMessage.setUser(UserProtobuf.builder().uid("visitor-1").type(UserTypeEnum.VISITOR.name()).build().toJson());
                visitorMessage.setAgentReplied(false);

                MessageEntity siblingVisitorMessage = new MessageEntity();
                siblingVisitorMessage.setUid("msg-visitor-2");
                siblingVisitorMessage.setThread(siblingThread);
                siblingVisitorMessage.setCreatedAt(visitorCreatedAt.plusMinutes(1));
                siblingVisitorMessage.setUser(UserProtobuf.builder().uid("visitor-2").type(UserTypeEnum.VISITOR.name()).build().toJson());
                siblingVisitorMessage.setAgentReplied(false);

                MessageEntity agentMessage = new MessageEntity();
                agentMessage.setUid("msg-agent");
                agentMessage.setThread(thread);
                agentMessage.setCreatedAt(agentCreatedAt);
                agentMessage.setUser(UserProtobuf.builder().uid("agent-1").type(UserTypeEnum.AGENT.name()).build().toJson());
                agentMessage.setAgentReplied(true);

                when(messageRestService.findByThreadTopicBetweenCreatedAt("topic-1", threadCreatedAt, agentCreatedAt))
                                .thenReturn(List.of(visitorMessage, siblingVisitorMessage, agentMessage));
                when(messageRestService.save(any(MessageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

                ReflectionTestUtils.invokeMethod(listener,
                                "markVisitorMessagesRepliedByAgent",
                                thread,
                                agentMessage,
                                null,
                                null);

                assertThat(visitorMessage.getAgentReplied()).isTrue();
                assertThat(siblingVisitorMessage.getAgentReplied()).isTrue();
                assertThat(siblingVisitorMessage.getAgentRepliedAt()).isEqualTo(agentCreatedAt);
                assertThat(siblingVisitorMessage.getAgentRepliedByUid()).isEqualTo("agent-1");

                verify(messageRestService).save(visitorMessage);
                verify(messageRestService).save(siblingVisitorMessage);
        }

    @Test
    void markVisitorMessagesRepliedByMemberShouldAlsoMarkVisitorMessagesReplied() {
        ZonedDateTime threadCreatedAt = ZonedDateTime.parse("2026-04-09T10:00:00+08:00");
        ZonedDateTime visitorCreatedAt = threadCreatedAt.plusMinutes(1);
        ZonedDateTime memberCreatedAt = threadCreatedAt.plusMinutes(5);

        ThreadEntity thread = new ThreadEntity();
        thread.setUid("thread-1");
        thread.setTopic("topic-1");
        thread.setType("AGENT");
        thread.setCreatedAt(threadCreatedAt);

        MessageEntity visitorMessage = new MessageEntity();
        visitorMessage.setUid("msg-visitor");
        visitorMessage.setThread(thread);
        visitorMessage.setCreatedAt(visitorCreatedAt);
        visitorMessage.setUser(UserProtobuf.builder()
                .uid("visitor-1")
                .type(UserTypeEnum.VISITOR.name())
                .build()
                .toJson());
        visitorMessage.setAgentReplied(false);

        MessageEntity memberMessage = new MessageEntity();
        memberMessage.setUid("msg-member");
        memberMessage.setThread(thread);
        memberMessage.setCreatedAt(memberCreatedAt);
        memberMessage.setUser(UserProtobuf.builder()
                .uid("member-1")
                .type(UserTypeEnum.MEMBER.name())
                .build()
                .toJson());
        memberMessage.setAgentReplied(true);

        when(messageRestService.findByThreadTopicBetweenCreatedAt("topic-1", threadCreatedAt, memberCreatedAt))
                .thenReturn(List.of(visitorMessage, memberMessage));
        when(messageRestService.save(any(MessageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReflectionTestUtils.invokeMethod(listener,
                "markVisitorMessagesRepliedByAgent",
                thread,
                memberMessage,
                null,
                null);

        assertThat(visitorMessage.getAgentReplied()).isTrue();
        assertThat(visitorMessage.getAgentRepliedAt()).isEqualTo(memberCreatedAt);
        assertThat(visitorMessage.getAgentRepliedByUid()).isEqualTo("member-1");

        verify(messageRestService).save(visitorMessage);
    }

    @Test
    void markVisitorMessagesRepliedByRobotShouldAlsoMarkVisitorMessagesReplied() {
        ZonedDateTime threadCreatedAt = ZonedDateTime.parse("2026-04-09T10:00:00+08:00");
        ZonedDateTime visitorCreatedAt = threadCreatedAt.plusMinutes(1);
        ZonedDateTime robotCreatedAt = threadCreatedAt.plusMinutes(5);

        ThreadEntity thread = new ThreadEntity();
        thread.setUid("thread-1");
        thread.setTopic("topic-1");
        thread.setType("AGENT");
        thread.setCreatedAt(threadCreatedAt);

        MessageEntity visitorMessage = new MessageEntity();
        visitorMessage.setUid("msg-visitor");
        visitorMessage.setThread(thread);
        visitorMessage.setCreatedAt(visitorCreatedAt);
        visitorMessage.setUser(UserProtobuf.builder()
                .uid("visitor-1")
                .type(UserTypeEnum.VISITOR.name())
                .build()
                .toJson());
        visitorMessage.setAgentReplied(false);

        MessageEntity robotMessage = new MessageEntity();
        robotMessage.setUid("msg-robot");
        robotMessage.setThread(thread);
        robotMessage.setCreatedAt(robotCreatedAt);
        robotMessage.setType(MessageTypeEnum.ROBOT.name());
        robotMessage.setUser(UserProtobuf.builder()
                .uid("robot-1")
                .type(UserTypeEnum.ROBOT.name())
                .build()
                .toJson());
        robotMessage.setAgentReplied(true);

        when(messageRestService.findByThreadTopicBetweenCreatedAt("topic-1", threadCreatedAt, robotCreatedAt))
                .thenReturn(List.of(visitorMessage, robotMessage));
        when(messageRestService.save(any(MessageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReflectionTestUtils.invokeMethod(listener,
                "markVisitorMessagesRepliedByAgent",
                thread,
                robotMessage,
                null,
                null);

        assertThat(visitorMessage.getAgentReplied()).isTrue();
        assertThat(visitorMessage.getAgentRepliedAt()).isEqualTo(robotCreatedAt);
        assertThat(visitorMessage.getAgentRepliedByUid()).isEqualTo("robot-1");

        verify(messageRestService).save(visitorMessage);
    }

        @Test
        void ensureVisitorMessageUnrepliedShouldIgnoreSystemTypeEvenIfSenderIsVisitor() {
                MessageEntity message = new MessageEntity();
                message.setUid("msg-system-visitor");
                message.setType(MessageTypeEnum.SYSTEM.name());
                message.setUser(UserProtobuf.builder()
                                .uid("visitor-1")
                                .type(UserTypeEnum.VISITOR.name())
                                .build()
                                .toJson());
                message.setAgentReplied(true);

                ReflectionTestUtils.invokeMethod(listener, "ensureVisitorMessageUnreplied", message);

                assertThat(message.getAgentReplied()).isTrue();
                verify(messageRestService, never()).save(any(MessageEntity.class));
        }
}