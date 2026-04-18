package com.bytedesk.service.queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageRepository;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserTypeEnum;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRepository;
import com.bytedesk.core.thread.ThreadRequest;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.workgroup.WorkgroupRepository;

@ExtendWith(MockitoExtension.class)
class QueueRestServiceTest {

        private static final String AGENT_UID_PATTERN = "%\"uid\":\"agent-1\"%";

    @Mock
    private QueueRepository queueRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UidUtils uidUtils;

    @Mock
    private AuthService authService;

    @Mock
    private ThreadRestService threadRestService;

    @Mock
    private AgentRestService agentRestService;

    @Mock
    private WorkgroupRepository workgroupRepository;

    @Mock
    private QueueMemberRestService queueMemberRestService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private MessageRestService messageRestService;

    @Mock
    private ThreadRepository threadRepository;

    @Mock
    private QueueService queueService;

    private QueueRestService queueRestService;

    @BeforeEach
    void setUp() {
        queueRestService = new QueueRestService(
                queueRepository,
                modelMapper,
                uidUtils,
                authService,
                threadRestService,
                agentRestService,
                workgroupRepository,
                queueMemberRestService,
                messageRepository,
                messageRestService,
                threadRepository,
                queueService);
    }

    @Test
    void queryUnrepliedShouldSelfHealStaleAgentRepliedThreads() {
        UserEntity user = new UserEntity();
        user.setUid("user-1");
        OrganizationEntity organization = new OrganizationEntity();
        organization.setUid("org-1");
        user.setCurrentOrganization(organization);

        AgentEntity agent = AgentEntity.builder().uid("agent-1").build();

        ZonedDateTime visitorCreatedAt = ZonedDateTime.parse("2026-04-09T10:01:00+08:00");
        ZonedDateTime agentCreatedAt = ZonedDateTime.parse("2026-04-09T10:05:00+08:00");

        MessageEntity visitorMessage = new MessageEntity();
        visitorMessage.setUid("msg-visitor");
        visitorMessage.setCreatedAt(visitorCreatedAt);
        ThreadEntity thread = new ThreadEntity();
        thread.setUid("thread-1");
        thread.setTopic("topic-1");
        visitorMessage.setThread(thread);
        visitorMessage.setUser(UserProtobuf.builder()
                .uid("visitor-1")
                .type(UserTypeEnum.VISITOR.name())
                .build()
                .toJson());
        visitorMessage.setAgentReplied(false);

        MessageEntity latestAgentMessage = new MessageEntity();
        latestAgentMessage.setUid("msg-agent");
        latestAgentMessage.setCreatedAt(agentCreatedAt);
        ThreadEntity siblingThread = new ThreadEntity();
        siblingThread.setUid("thread-2");
        siblingThread.setTopic("topic-1");
        latestAgentMessage.setThread(siblingThread);
        latestAgentMessage.setUser(UserProtobuf.builder()
                .uid("agent-1")
                .type(UserTypeEnum.AGENT.name())
                .build()
                .toJson());
        latestAgentMessage.setAgentReplied(true);

        MessageEntity siblingVisitorMessage = new MessageEntity();
        siblingVisitorMessage.setUid("msg-visitor-2");
        siblingVisitorMessage.setCreatedAt(visitorCreatedAt.plusMinutes(1));
        siblingVisitorMessage.setThread(siblingThread);
        siblingVisitorMessage.setUser(UserProtobuf.builder()
                .uid("visitor-2")
                .type(UserTypeEnum.VISITOR.name())
                .build()
                .toJson());
        siblingVisitorMessage.setAgentReplied(false);

        when(authService.getUser()).thenReturn(user);
        when(agentRestService.findByUserUidAndOrgUid("user-1", "org-1")).thenReturn(Optional.of(agent));
        when(messageRepository.countUnrepliedVisitorThreadsByAgentUid(AGENT_UID_PATTERN)).thenReturn(1L, 0L);
        when(messageRepository.pageUnrepliedVisitorThreadUidsByAgentUid(AGENT_UID_PATTERN, 10, 0))
                .thenReturn(Collections.singletonList(new Object[] { "thread-1", Timestamp.from(visitorCreatedAt.toInstant()) }));
        when(threadRepository.findByUidInAndDeletedFalse(List.of("thread-1"))).thenReturn(List.of(thread));
        when(messageRestService.findLatestByThreadTopic("topic-1")).thenReturn(Optional.of(latestAgentMessage));
        when(messageRestService.findByThreadTopicBetweenCreatedAt("topic-1", visitorCreatedAt, agentCreatedAt))
                .thenReturn(List.of(visitorMessage, siblingVisitorMessage, latestAgentMessage));
        when(messageRestService.save(any(MessageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ThreadRequest request = ThreadRequest.builder().pageNumber(0).pageSize(10).build();
        var result = queueRestService.queryUnreplied(request);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        assertThat(visitorMessage.getAgentReplied()).isTrue();
                assertThat(siblingVisitorMessage.getAgentReplied()).isTrue();
        assertThat(visitorMessage.getAgentRepliedAt()).isEqualTo(agentCreatedAt);
        assertThat(visitorMessage.getAgentRepliedByUid()).isEqualTo("agent-1");

        verify(messageRestService).save(visitorMessage);
                verify(messageRestService).save(siblingVisitorMessage);
        verify(threadRestService, never()).convertToResponse(any(ThreadEntity.class));
    }

    @Test
    void markUnrepliedThreadAsRepliedShouldUpdateSameTopicVisitorMessages() {
        UserEntity user = new UserEntity();
        user.setUid("user-1");
        OrganizationEntity organization = new OrganizationEntity();
        organization.setUid("org-1");
        user.setCurrentOrganization(organization);

        AgentEntity agent = AgentEntity.builder().uid("agent-1").build();

        ThreadEntity thread = new ThreadEntity();
        thread.setUid("thread-1");
        thread.setTopic("topic-1");
        thread.setAgent(UserProtobuf.builder().uid("agent-1").type(UserTypeEnum.AGENT.name()).build().toJson());

        MessageEntity visitorMessage = new MessageEntity();
        visitorMessage.setUid("msg-visitor");
        visitorMessage.setThread(thread);
        visitorMessage.setCreatedAt(ZonedDateTime.now().minusMinutes(2));
        visitorMessage.setUser(UserProtobuf.builder().uid("visitor-1").type(UserTypeEnum.VISITOR.name()).build().toJson());
        visitorMessage.setAgentReplied(false);

        MessageEntity siblingVisitorMessage = new MessageEntity();
        siblingVisitorMessage.setUid("msg-visitor-2");
        siblingVisitorMessage.setThread(thread);
        siblingVisitorMessage.setCreatedAt(ZonedDateTime.now().minusMinutes(1));
        siblingVisitorMessage.setUser(UserProtobuf.builder().uid("visitor-2").type(UserTypeEnum.VISITOR.name()).build().toJson());
        siblingVisitorMessage.setAgentReplied(false);

        MessageEntity agentMessage = new MessageEntity();
        agentMessage.setUid("msg-agent");
        agentMessage.setThread(thread);
        agentMessage.setCreatedAt(ZonedDateTime.now().minusSeconds(30));
        agentMessage.setUser(UserProtobuf.builder().uid("agent-1").type(UserTypeEnum.AGENT.name()).build().toJson());
        agentMessage.setAgentReplied(true);

        when(authService.getUser()).thenReturn(user);
        when(agentRestService.findByUserUidAndOrgUid("user-1", "org-1")).thenReturn(Optional.of(agent));
        when(threadRepository.findByUid("thread-1")).thenReturn(Optional.of(thread));
        when(messageRestService.findByThreadTopicBetweenCreatedAt(org.mockito.ArgumentMatchers.eq("topic-1"), any(), any()))
                .thenReturn(List.of(visitorMessage, siblingVisitorMessage, agentMessage));
        when(messageRestService.save(any(MessageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int updated = queueRestService.markUnrepliedThreadAsReplied(ThreadRequest.builder().uid("thread-1").build());

        assertThat(updated).isEqualTo(2);
        assertThat(visitorMessage.getAgentReplied()).isTrue();
        assertThat(visitorMessage.getAgentRepliedByUid()).isEqualTo("agent-1");
        assertThat(visitorMessage.getAgentRepliedAt()).isNotNull();
        assertThat(siblingVisitorMessage.getAgentReplied()).isTrue();
        assertThat(siblingVisitorMessage.getAgentRepliedByUid()).isEqualTo("agent-1");

        verify(messageRestService).save(visitorMessage);
        verify(messageRestService).save(siblingVisitorMessage);
    }

    @Test
    void markAllUnrepliedThreadsAsRepliedShouldDrainCurrentAgentRows() {
        UserEntity user = new UserEntity();
        user.setUid("user-1");
        OrganizationEntity organization = new OrganizationEntity();
        organization.setUid("org-1");
        user.setCurrentOrganization(organization);

        AgentEntity agent = AgentEntity.builder().uid("agent-1").build();

        ThreadEntity thread = new ThreadEntity();
        thread.setUid("thread-1");
        thread.setTopic("topic-1");
        thread.setAgent(UserProtobuf.builder().uid("agent-1").type(UserTypeEnum.AGENT.name()).build().toJson());

        MessageEntity visitorMessage = new MessageEntity();
        visitorMessage.setUid("msg-visitor");
        visitorMessage.setThread(thread);
        visitorMessage.setCreatedAt(ZonedDateTime.now().minusMinutes(3));
        visitorMessage.setUser(UserProtobuf.builder().uid("visitor-1").type(UserTypeEnum.VISITOR.name()).build().toJson());
        visitorMessage.setAgentReplied(false);

        when(authService.getUser()).thenReturn(user);
        when(agentRestService.findByUserUidAndOrgUid("user-1", "org-1")).thenReturn(Optional.of(agent));
        when(messageRepository.pageUnrepliedVisitorThreadUidsByAgentUid(AGENT_UID_PATTERN, 100, 0))
                .thenReturn(Collections.singletonList(new Object[] { "thread-1", Timestamp.from(visitorMessage.getCreatedAt().toInstant()) }))
                .thenReturn(List.of());
        when(threadRepository.findByUidInAndDeletedFalse(List.of("thread-1"))).thenReturn(List.of(thread));
        when(messageRestService.findByThreadTopicBetweenCreatedAt(org.mockito.ArgumentMatchers.eq("topic-1"), any(), any()))
                .thenReturn(List.of(visitorMessage));
        when(messageRestService.save(any(MessageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        int updated = queueRestService.markAllUnrepliedThreadsAsReplied(ThreadRequest.builder().build());

        assertThat(updated).isEqualTo(1);
        assertThat(visitorMessage.getAgentReplied()).isTrue();
        assertThat(visitorMessage.getAgentRepliedByUid()).isEqualTo("agent-1");

        verify(messageRestService).save(visitorMessage);
    }

    @Test
    void queryUnrepliedShouldKeepThreadsWhoseLatestMessageIsStillVisitor() {
        UserEntity user = new UserEntity();
        user.setUid("user-1");
        OrganizationEntity organization = new OrganizationEntity();
        organization.setUid("org-1");
        user.setCurrentOrganization(organization);

        AgentEntity agent = AgentEntity.builder().uid("agent-1").build();

        ZonedDateTime visitorCreatedAt = ZonedDateTime.parse("2026-04-09T10:01:00+08:00");

        MessageEntity latestVisitorMessage = new MessageEntity();
        latestVisitorMessage.setUid("msg-visitor-latest");
        latestVisitorMessage.setCreatedAt(visitorCreatedAt.plusMinutes(1));
        ThreadEntity thread = new ThreadEntity();
        thread.setUid("thread-1");
        thread.setTopic("topic-1");
        latestVisitorMessage.setThread(thread);
        latestVisitorMessage.setUser(UserProtobuf.builder()
                .uid("visitor-1")
                .type(UserTypeEnum.VISITOR.name())
                .build()
                .toJson());
        latestVisitorMessage.setAgentReplied(false);

        ThreadResponse response = ThreadResponse.builder().uid("thread-1").build();

        when(authService.getUser()).thenReturn(user);
        when(agentRestService.findByUserUidAndOrgUid("user-1", "org-1")).thenReturn(Optional.of(agent));
        when(messageRepository.countUnrepliedVisitorThreadsByAgentUid(AGENT_UID_PATTERN)).thenReturn(1L);
        when(messageRepository.pageUnrepliedVisitorThreadUidsByAgentUid(AGENT_UID_PATTERN, 10, 0))
                .thenReturn(Collections.singletonList(new Object[] { "thread-1", Timestamp.from(visitorCreatedAt.toInstant()) }));
        when(threadRepository.findByUidInAndDeletedFalse(List.of("thread-1"))).thenReturn(List.of(thread));
        when(messageRestService.findLatestByThreadTopic("topic-1")).thenReturn(Optional.of(latestVisitorMessage));
        when(threadRepository.findByUidInAndDeletedFalse(List.of("thread-1"))).thenReturn(List.of(thread));
        when(queueMemberRestService.findByThreadUids(List.of("thread-1"))).thenReturn(List.of());
        when(threadRestService.convertToResponse(thread)).thenReturn(response);

        ThreadRequest request = ThreadRequest.builder().pageNumber(0).pageSize(10).build();
        var result = queueRestService.queryUnreplied(request);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUid()).isEqualTo("thread-1");
        assertThat(result.getTotalElements()).isEqualTo(1L);

        verify(messageRestService, never()).save(any(MessageEntity.class));
    }

    @Test
    void queryUnrepliedShouldSelfHealWhenLatestHumanReplyIsMember() {
        UserEntity user = new UserEntity();
        user.setUid("user-1");
        OrganizationEntity organization = new OrganizationEntity();
        organization.setUid("org-1");
        user.setCurrentOrganization(organization);

        AgentEntity agent = AgentEntity.builder().uid("agent-1").build();

        ZonedDateTime visitorCreatedAt = ZonedDateTime.parse("2026-04-09T10:01:00+08:00");
        ZonedDateTime memberCreatedAt = ZonedDateTime.parse("2026-04-09T10:05:00+08:00");

        ThreadEntity thread = new ThreadEntity();
        thread.setUid("thread-1");
        thread.setTopic("topic-1");
        thread.setType("AGENT");

        MessageEntity visitorMessage = new MessageEntity();
        visitorMessage.setUid("msg-visitor");
        visitorMessage.setCreatedAt(visitorCreatedAt);
        visitorMessage.setThread(thread);
        visitorMessage.setUser(UserProtobuf.builder()
                .uid("visitor-1")
                .type(UserTypeEnum.VISITOR.name())
                .build()
                .toJson());
        visitorMessage.setAgentReplied(false);

        MessageEntity latestMemberMessage = new MessageEntity();
        latestMemberMessage.setUid("msg-member");
        latestMemberMessage.setCreatedAt(memberCreatedAt);
        latestMemberMessage.setThread(thread);
        latestMemberMessage.setUser(UserProtobuf.builder()
                .uid("member-1")
                .type(UserTypeEnum.MEMBER.name())
                .build()
                .toJson());
        latestMemberMessage.setAgentReplied(true);

        when(authService.getUser()).thenReturn(user);
        when(agentRestService.findByUserUidAndOrgUid("user-1", "org-1")).thenReturn(Optional.of(agent));
        when(messageRepository.countUnrepliedVisitorThreadsByAgentUid(AGENT_UID_PATTERN)).thenReturn(1L, 0L);
        when(messageRepository.pageUnrepliedVisitorThreadUidsByAgentUid(AGENT_UID_PATTERN, 10, 0))
                .thenReturn(Collections.singletonList(new Object[] { "thread-1", Timestamp.from(visitorCreatedAt.toInstant()) }));
        when(threadRepository.findByUidInAndDeletedFalse(List.of("thread-1"))).thenReturn(List.of(thread));
        when(messageRestService.findLatestByThreadTopic("topic-1")).thenReturn(Optional.of(latestMemberMessage));
        when(messageRestService.findByThreadTopicBetweenCreatedAt("topic-1", visitorCreatedAt, memberCreatedAt))
                .thenReturn(List.of(visitorMessage, latestMemberMessage));
        when(messageRestService.save(any(MessageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ThreadRequest request = ThreadRequest.builder().pageNumber(0).pageSize(10).build();
        var result = queueRestService.queryUnreplied(request);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        assertThat(visitorMessage.getAgentReplied()).isTrue();
        assertThat(visitorMessage.getAgentRepliedAt()).isEqualTo(memberCreatedAt);
        assertThat(visitorMessage.getAgentRepliedByUid()).isEqualTo("member-1");

        verify(messageRestService).save(visitorMessage);
        verify(threadRestService, never()).convertToResponse(any(ThreadEntity.class));
    }

    @Test
    void queryUnrepliedShouldSelfHealWhenLatestReplyIsRobot() {
        UserEntity user = new UserEntity();
        user.setUid("user-1");
        OrganizationEntity organization = new OrganizationEntity();
        organization.setUid("org-1");
        user.setCurrentOrganization(organization);

        AgentEntity agent = AgentEntity.builder().uid("agent-1").build();

        ZonedDateTime visitorCreatedAt = ZonedDateTime.parse("2026-04-09T10:01:00+08:00");
        ZonedDateTime robotCreatedAt = ZonedDateTime.parse("2026-04-09T10:05:00+08:00");

        ThreadEntity thread = new ThreadEntity();
        thread.setUid("thread-1");
        thread.setTopic("topic-1");
        thread.setType("AGENT");

        MessageEntity visitorMessage = new MessageEntity();
        visitorMessage.setUid("msg-visitor");
        visitorMessage.setCreatedAt(visitorCreatedAt);
        visitorMessage.setThread(thread);
        visitorMessage.setUser(UserProtobuf.builder()
                .uid("visitor-1")
                .type(UserTypeEnum.VISITOR.name())
                .build()
                .toJson());
        visitorMessage.setAgentReplied(false);

        MessageEntity latestRobotMessage = new MessageEntity();
        latestRobotMessage.setUid("msg-robot");
        latestRobotMessage.setCreatedAt(robotCreatedAt);
        latestRobotMessage.setType("ROBOT");
        latestRobotMessage.setThread(thread);
        latestRobotMessage.setUser(UserProtobuf.builder()
                .uid("robot-1")
                .type(UserTypeEnum.ROBOT.name())
                .build()
                .toJson());
        latestRobotMessage.setAgentReplied(true);

        when(authService.getUser()).thenReturn(user);
        when(agentRestService.findByUserUidAndOrgUid("user-1", "org-1")).thenReturn(Optional.of(agent));
        when(messageRepository.countUnrepliedVisitorThreadsByAgentUid(AGENT_UID_PATTERN)).thenReturn(1L, 0L);
        when(messageRepository.pageUnrepliedVisitorThreadUidsByAgentUid(AGENT_UID_PATTERN, 10, 0))
                .thenReturn(Collections.singletonList(new Object[] { "thread-1", Timestamp.from(visitorCreatedAt.toInstant()) }));
        when(threadRepository.findByUidInAndDeletedFalse(List.of("thread-1"))).thenReturn(List.of(thread));
        when(messageRestService.findLatestByThreadTopic("topic-1")).thenReturn(Optional.of(latestRobotMessage));
        when(messageRestService.findByThreadTopicBetweenCreatedAt("topic-1", visitorCreatedAt, robotCreatedAt))
                .thenReturn(List.of(visitorMessage, latestRobotMessage));
        when(messageRestService.save(any(MessageEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ThreadRequest request = ThreadRequest.builder().pageNumber(0).pageSize(10).build();
        var result = queueRestService.queryUnreplied(request);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
        assertThat(visitorMessage.getAgentReplied()).isTrue();
        assertThat(visitorMessage.getAgentRepliedAt()).isEqualTo(robotCreatedAt);
        assertThat(visitorMessage.getAgentRepliedByUid()).isEqualTo("robot-1");

        verify(messageRestService).save(visitorMessage);
        verify(threadRestService, never()).convertToResponse(any(ThreadEntity.class));
    }

        @Test
        void queryUnrepliedShouldUseEntityCreatedAtForWaitingStart() {
                UserEntity user = new UserEntity();
                user.setUid("user-1");
                OrganizationEntity organization = new OrganizationEntity();
                organization.setUid("org-1");
                user.setCurrentOrganization(organization);

                AgentEntity agent = AgentEntity.builder().uid("agent-1").build();

                ZonedDateTime actualVisitorCreatedAt = ZonedDateTime.parse("2026-04-10T11:18:34+08:00");
                Timestamp skewedTimestamp = Timestamp.valueOf("2026-04-10 03:18:34");

                ThreadEntity thread = new ThreadEntity();
                thread.setUid("thread-1");
                thread.setTopic("topic-1");
                thread.setType("AGENT");

                MessageEntity visitorMessage = new MessageEntity();
                visitorMessage.setUid("msg-visitor");
                visitorMessage.setCreatedAt(actualVisitorCreatedAt);
                visitorMessage.setThread(thread);
                visitorMessage.setType("TEXT");
                visitorMessage.setUser(UserProtobuf.builder()
                                .uid("visitor-1")
                                .type(UserTypeEnum.VISITOR.name())
                                .build()
                                .toJson());
                visitorMessage.setAgentReplied(false);

                ThreadResponse response = ThreadResponse.builder().uid("thread-1").build();

                when(authService.getUser()).thenReturn(user);
                when(agentRestService.findByUserUidAndOrgUid("user-1", "org-1")).thenReturn(Optional.of(agent));
                when(messageRepository.countUnrepliedVisitorThreadsByAgentUid(AGENT_UID_PATTERN)).thenReturn(1L);
                when(messageRepository.pageUnrepliedVisitorThreadUidsByAgentUid(AGENT_UID_PATTERN, 10, 0))
                                .thenReturn(Collections.singletonList(new Object[] { "thread-1", skewedTimestamp }));
                when(threadRepository.findByUidInAndDeletedFalse(List.of("thread-1"))).thenReturn(List.of(thread));
                when(messageRestService.findLatestByThreadTopic("topic-1")).thenReturn(Optional.of(visitorMessage));
                when(messageRestService.findByThreadUid("thread-1")).thenReturn(List.of(visitorMessage));
                when(queueMemberRestService.findByThreadUids(List.of("thread-1"))).thenReturn(List.of());
                when(threadRestService.convertToResponse(thread)).thenReturn(response);

                ThreadRequest request = ThreadRequest.builder().pageNumber(0).pageSize(10).build();
                var result = queueRestService.queryUnreplied(request);

                assertThat(result.getContent()).hasSize(1);
                assertThat(result.getContent().get(0).getQueueMeta()).isNotNull();
                assertThat(result.getContent().get(0).getQueueMeta().getEnqueuedAt())
                                .isEqualTo(actualVisitorCreatedAt.toInstant().toEpochMilli());
        }
}