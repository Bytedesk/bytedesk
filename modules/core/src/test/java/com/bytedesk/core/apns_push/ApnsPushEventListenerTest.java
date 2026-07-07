package com.bytedesk.core.apns_push;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.message.event.MessageJsonEvent;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;

@ExtendWith(MockitoExtension.class)
class ApnsPushEventListenerTest {

    @Mock
    private ApnsPushService apnsPushService;

    private ApnsPushEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new ApnsPushEventListener(apnsPushService);
    }

    @Test
    void shouldNotPushToThreadUserWhenVisitorMessagesAssignedAgentThread() {
        MessageProtobuf message = buildMessage(
                ThreadTypeEnum.AGENT,
                UserProtobuf.builder().uid("visitor-1").type(UserTypeEnum.VISITOR.name()).build(),
                UserProtobuf.builder().uid("visitor-1").type(UserTypeEnum.VISITOR.name()).build(),
                UserProtobuf.builder().uid("agent-entity-1").type(UserTypeEnum.AGENT.name()).build());

        listener.onMessageJsonEvent(new MessageJsonEvent(this, message.toJson()));

        verify(apnsPushService, never()).pushMessageToUser(any(), any());
    }

    @Test
    void shouldPushToThreadUserWhenAgentRepliesInAssignedAgentThread() {
        MessageProtobuf message = buildMessage(
                ThreadTypeEnum.AGENT,
                UserProtobuf.builder().uid("visitor-1").type(UserTypeEnum.VISITOR.name()).build(),
                UserProtobuf.builder().uid("agent-user-1").type(UserTypeEnum.AGENT.name()).build(),
                UserProtobuf.builder().uid("agent-entity-1").type(UserTypeEnum.AGENT.name()).build());

        listener.onMessageJsonEvent(new MessageJsonEvent(this, message.toJson()));

        verify(apnsPushService).pushMessageToUser(eq("visitor-1"), any(MessageProtobuf.class));
    }

    private MessageProtobuf buildMessage(ThreadTypeEnum threadType, UserProtobuf threadUser, UserProtobuf sender,
            UserProtobuf threadAgent) {
        return MessageProtobuf.builder()
                .uid("msg-1")
                .type(MessageTypeEnum.TEXT)
                .content("hello")
                .createdAt(ZonedDateTime.now())
                .channel(ChannelEnum.WEB)
                .thread(ThreadProtobuf.builder()
                        .uid("thread-1")
                        .topic("topic-1")
                        .type(threadType)
                        .status(ThreadProcessStatusEnum.CHATTING)
                        .user(threadUser)
                        .agent(threadAgent != null ? threadAgent.toJson() : null)
                        .build())
                .user(sender)
                .build();
    }
}