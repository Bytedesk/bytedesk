package com.bytedesk.service.agent;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.ObjectProvider;

import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.message.MessageProtobuf;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.message.event.MessageJsonEvent;
import com.bytedesk.core.push.service.PushMobileService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadProtobuf;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;

@ExtendWith(MockitoExtension.class)
class AgentPushApnsEventListenerTest {

    @Mock
    private PushMobileService mobilePushService;

    @Mock
    private ObjectProvider<PushMobileService> mobilePushProvider;

    @Mock
    private AgentRestService agentRestService;

    private AgentPushApnsEventListener listener;

    @BeforeEach
    void setUp() {
        lenient().when(mobilePushProvider.getIfAvailable()).thenReturn(mobilePushService);
        listener = new AgentPushApnsEventListener(mobilePushProvider, agentRestService);
    }

    @Test
    void shouldPushVisitorMessageToAgentMemberUserInWorkgroupThread() {
        AgentEntity agent = AgentEntity.builder()
                .uid("agent-entity-1")
                .member(MemberEntity.builder()
                        .uid("member-1")
                        .user(UserEntity.builder().uid("agent-user-1").username("agent-user").build())
                        .build())
                .build();
        when(agentRestService.findByUid("agent-entity-1")).thenReturn(Optional.of(agent));

        MessageProtobuf message = buildMessage(
                ThreadTypeEnum.WORKGROUP,
                UserProtobuf.builder().uid("visitor-1").type(UserTypeEnum.VISITOR.name()).build(),
                UserProtobuf.builder().uid("visitor-1").type(UserTypeEnum.VISITOR.name()).build(),
                UserProtobuf.builder().uid("agent-entity-1").type(UserTypeEnum.AGENT.name()).build());

        listener.onMessageJsonEvent(new MessageJsonEvent(this, message.toJson()));

        verify(mobilePushService).pushMessageToUser(eq("agent-user-1"), any(MessageProtobuf.class));
    }

    @Test
    void shouldNotPushWhenSenderIsAgent() {
        MessageProtobuf message = buildMessage(
                ThreadTypeEnum.WORKGROUP,
                UserProtobuf.builder().uid("visitor-1").type(UserTypeEnum.VISITOR.name()).build(),
                UserProtobuf.builder().uid("agent-user-1").type(UserTypeEnum.AGENT.name()).build(),
                UserProtobuf.builder().uid("agent-entity-1").type(UserTypeEnum.AGENT.name()).build());

        listener.onMessageJsonEvent(new MessageJsonEvent(this, message.toJson()));

        verify(agentRestService, never()).findByUid(any());
        verify(mobilePushService, never()).pushMessageToUser(any(), any());
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