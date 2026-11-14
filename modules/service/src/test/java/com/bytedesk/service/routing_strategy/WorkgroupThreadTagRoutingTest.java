package com.bytedesk.service.routing_strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;

import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.enums.VisitorCallTypeEnum;
import com.bytedesk.core.member.MemberEntity;
import com.bytedesk.core.message.IMessageSendService;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRepository;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadTransferStatusEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.topic_subscription.TopicSubscriptionRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.core.push.service.PushReachProperties;
import com.bytedesk.service.agent.AgentCapacityService;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.presence.PresenceFacadeService;
import com.bytedesk.service.queue.QueueEntity;
import com.bytedesk.service.queue.QueueService;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.routing_pool.RoutingPoolRestService;
import com.bytedesk.service.routing_pool.RoutingPoolResponse;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.visitor_thread.VisitorThreadService;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRestService;
import com.bytedesk.service.workgroup_settings.WorkgroupSettingsEntity;
import com.bytedesk.service.worktime_settings.WorktimeService;
import com.bytedesk.service.worktime_settings.WorktimeSettingsResolver;

@ExtendWith(MockitoExtension.class)
class WorkgroupThreadTagRoutingTest {

    @Mock private WorkgroupRestService workgroups;
    @Mock private ThreadRestService threads;
    @Mock private VisitorThreadService visitorThreads;
    @Mock private QueueService queues;
    @Mock private QueueMemberRestService members;
    @Mock private MessageRestService messages;
    @Mock private IMessageSendService send;
    @Mock private AgentCapacityService capacity;
    @Mock private PresenceFacadeService presence;
    @Mock private WorkgroupTagRoutingService tags;
    @Mock private WorkgroupRoutingTransaction transaction;
    @Mock private WorktimeService worktime;
    @Mock private WorktimeSettingsResolver worktimeResolver;
    @Mock private BytedeskEventPublisher publisher;
    @Mock private TopicSubscriptionRestService subscriptions;
    @Mock private RoutingPoolRestService routingPools;
    @InjectMocks private WorkgroupThreadRoutingStrategy strategy;

    private MockedStatic<UidUtils> uidStatic;
    private MockedStatic<ApplicationContextHolder> contextStatic;
    private ThreadEntity thread;
    private WorkgroupEntity workgroup;
    private AgentEntity agent;
    private VisitorRequest request;
    private QueueMemberEntity member;

    @BeforeEach
    void setup() {
        UidUtils uid = mock(UidUtils.class);
        when(uid.getUid()).thenReturn("message-1");
        uidStatic = Mockito.mockStatic(UidUtils.class);
        uidStatic.when(UidUtils::getInstance).thenReturn(uid);
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        contextStatic = Mockito.mockStatic(ApplicationContextHolder.class);
        contextStatic.when(() -> ApplicationContextHolder.getBean(ModelMapper.class)).thenReturn(mapper);
        thread = ThreadEntity.builder().uid("thread-1").orgUid("org-1").type("WORKGROUP")
                .topic(TopicUtils.formatOrgWorkgroupThreadTopic("wg-1", "visitor-1"))
                .user(UserProtobuf.builder().uid("visitor-1").nickname("visitor").type("VISITOR").build().toJson())
                .routingTagSnapshot(List.of("tag-1")).tagRoutingEvaluated(false).build();
        agent = AgentEntity.builder().uid("agent-1").orgUid("org-1").nickname("agent")
                .member(MemberEntity.builder().user(UserEntity.builder().uid("user-1").nickname("agent").build()).build())
                .build();
        workgroup = WorkgroupEntity.builder().uid("wg-1").orgUid("org-1").agents(List.of(agent))
                .settings(WorkgroupSettingsEntity.builder().tagRoutingEnabled(true).build()).build();
        request = VisitorRequest.builder().visitorUid("visitor-1").uid("visitor-1")
                .sid("wg-1").type("1").orgUid("org-1").build();
        member = QueueMemberEntity.builder().uid("member-1").thread(thread)
                .workgroupQueue(QueueEntity.builder().uid("queue-1").build()).build();
    }

    @AfterEach
    void cleanup() {
        contextStatic.close();
        uidStatic.close();
    }

    @Test
    void firstHumanReceptionUsesTagBeforeRoutingModeAndCommitsMarker() {
        prepareHumanRouting();
        assertNotNull(strategy.createWorkgroupThread(request));
        assertEquals("agent-1", thread.getAgentProtobuf().getUid());
        assertEquals(true, thread.getTagRoutingEvaluated());
        verify(capacity, never()).findAvailableAgentWithCapacity(any(), any(), any());
        verify(members).save(member);
        verify(members, never()).saveAsyncBestEffort(member);
    }

    @Test
    void buttonTransferUsesFrozenSnapshotAndDoesNotOverwriteIt() {
        thread.setRoboting();
        request.setForceAgent(true);
        request.setRoutingTags(List.of("changed-url-tag"));
        prepareHumanRouting();
        when(messages.findLatestByThreadUid("thread-1")).thenReturn(Optional.empty());
        assertNotNull(strategy.createWorkgroupThread(request));
        assertEquals(List.of("tag-1"), thread.getRoutingTagSnapshot());
        assertEquals(List.of("tag-1"), request.getRoutingTags());
        assertEquals("agent-1", thread.getAgentProtobuf().getUid());
    }

    @Test
    void keywordPendingQueueUsesTagAndRepeatedRequestDoesNotReassign() {
        thread.setQueuing().setTransferStatus(ThreadTransferStatusEnum.TRANSFER_PENDING.name());
        request.setForceAgent(true);
        prepareHumanRouting();
        when(messages.findLatestByThreadUid("thread-1")).thenReturn(Optional.empty());
        assertNotNull(strategy.createWorkgroupThread(request));
        assertEquals(ThreadTransferStatusEnum.TRANSFER_ACCEPTED.name(), thread.getTransferStatus());
        when(visitorThreads.reInitWorkgroupThreadExtra(request, thread, workgroup)).thenReturn(thread);
        assertNotNull(strategy.createWorkgroupThread(request));
        verify(tags).selectAgent(thread, workgroup, VisitorCallTypeEnum.TEXT);
        verify(queues).enqueueWorkgroup(eq(thread), any(), eq(workgroup), eq(request));
    }

    @Test
    void ordinaryQueueRefreshDoesNotRunTagSelectionEvenIfPendingWasAbsent() {
        thread.setQueuing();
        when(workgroups.findByUid("wg-1")).thenReturn(Optional.of(workgroup));
        when(threads.findFirstByTopicNotClosed(thread.getTopic())).thenReturn(Optional.of(thread));
        when(threads.findByUid("thread-1")).thenReturn(Optional.of(thread));
        when(worktime.isInServiceTime(any())).thenReturn(false);
        installTransaction();
        assertNotNull(strategy.createWorkgroupThread(request));
        verify(tags, never()).selectAgent(any(), any(), any());
    }

    @Test
    void noTagMatchUsesOriginalRoutingMode() {
        prepareHumanRouting();
        when(tags.selectAgent(thread, workgroup, VisitorCallTypeEnum.TEXT)).thenReturn(Optional.empty());
        when(capacity.findAvailableAgentWithCapacity(workgroup, thread, List.of(agent))).thenReturn(agent);
        assertNotNull(strategy.createWorkgroupThread(request));
        assertEquals("agent-1", thread.getAgentProtobuf().getUid());
        verify(capacity).findAvailableAgentWithCapacity(workgroup, thread, List.of(agent));
    }

    @Test
    void fullQueueKeepsOriginalRoutingAndDoesNotReevaluateTagsOrTransferEvents() {
        thread.setQueuing().setTransferStatus(ThreadTransferStatusEnum.TRANSFER_PENDING.name());
        request.setForceAgent(true);
        prepareHumanRouting();
        when(tags.selectAgent(thread, workgroup, VisitorCallTypeEnum.TEXT)).thenReturn(Optional.empty());
        when(queues.enqueueWorkgroup(thread, null, workgroup, request)).thenReturn(member);
        when(messages.findLatestByThreadUid("thread-1")).thenReturn(Optional.empty());
        assertNotNull(strategy.createWorkgroupThread(request));
        assertEquals(true, thread.getTagRoutingEvaluated());
        assertNotNull(strategy.createWorkgroupThread(request));
        verify(tags).selectAgent(thread, workgroup, VisitorCallTypeEnum.TEXT);
        verify(queues).enqueueWorkgroup(thread, null, workgroup, request);
    }

    @Test
    void keywordManualModeCreatesManualPoolWithoutSelectingTagAgent() {
        thread.setQueuing().setTransferStatus(ThreadTransferStatusEnum.TRANSFER_PENDING.name());
        request.setForceAgent(true);
        workgroup.getSettings().setRoutingMode("MANUAL");
        when(workgroups.findByUid("wg-1")).thenReturn(Optional.of(workgroup));
        when(threads.findFirstByTopicNotClosed(thread.getTopic())).thenReturn(Optional.of(thread));
        when(threads.findByUid("thread-1")).thenReturn(Optional.of(thread));
        when(threads.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(messages.findLatestByThreadUid("thread-1")).thenReturn(Optional.empty());
        when(visitorThreads.reInitWorkgroupThreadExtra(request, thread, workgroup)).thenReturn(thread);
        when(routingPools.createSystemRoutingPool(any())).thenReturn(RoutingPoolResponse.builder().uid("pool-1").build());
        when(queues.enqueueWorkgroup(thread, null, workgroup, request)).thenReturn(member);
        when(presence.getAvailableAgentsForCallType(workgroup, VisitorCallTypeEnum.TEXT)).thenReturn(List.of());
        installTransaction();
        assertNotNull(strategy.createWorkgroupThread(request));
        assertEquals(true, thread.getTagRoutingEvaluated());
        verify(tags, never()).selectAgent(any(), any(), any());
        verify(capacity, never()).findAvailableAgentWithCapacity(any(), any(), any());
        verify(routingPools).createSystemRoutingPool(any());
    }

    @Test
    void noOnlineAgentsReturnsOfflineOutcomeIncludingCompletedSessionRefresh() {
        workgroup.setMessageLeaveAgent(agent);
        when(workgroups.findByUid("wg-1")).thenReturn(Optional.of(workgroup));
        when(threads.findFirstByTopicNotClosed(thread.getTopic())).thenReturn(Optional.of(thread));
        when(threads.findByUid("thread-1")).thenReturn(Optional.of(thread));
        when(threads.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(worktime.isInServiceTime(any())).thenReturn(true);
        when(presence.getAvailableAgentsForCallType(workgroup, VisitorCallTypeEnum.TEXT)).thenReturn(List.of());
        when(visitorThreads.reInitWorkgroupThreadExtra(request, thread, workgroup)).thenReturn(thread);
        when(queues.enqueueWorkgroup(any(), any(), any(), any())).thenReturn(member);
        when(members.save(member)).thenReturn(member);
        installTransaction();
        assertNotNull(strategy.createWorkgroupThread(request));
        assertTrue(thread.isOffline());
        assertEquals(true, thread.getTagRoutingEvaluated());
        assertNotNull(strategy.createWorkgroupThread(request));
        verify(tags, never()).selectAgent(any(), any(), any());
        assertTrue(thread.isOffline());
    }

    @Test
    void robotReceptionDoesNotUseTagsOrCompleteHumanDecision() {
        when(threads.findByUid("thread-1")).thenReturn(Optional.of(thread));
        when(threads.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(queues.enqueueWorkgroup(eq(thread), any(), eq(workgroup), eq(request))).thenReturn(member);
        RobotEntity robot = RobotEntity.builder().uid("robot-1").orgUid("org-1").nickname("robot").build();
        assertNotNull(strategy.routeToRobot(request, thread, robot, workgroup));
        assertEquals(false, thread.getTagRoutingEvaluated());
        verify(tags, never()).selectAgent(any(), any(), any());
    }

    private void prepareHumanRouting() {
        when(workgroups.findByUid("wg-1")).thenReturn(Optional.of(workgroup));
        when(threads.findFirstByTopicNotClosed(thread.getTopic())).thenReturn(Optional.of(thread));
        when(threads.findByUid("thread-1")).thenReturn(Optional.of(thread));
        when(threads.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(worktime.isInServiceTime(any())).thenReturn(true);
        when(presence.getAvailableAgentsForCallType(workgroup, VisitorCallTypeEnum.TEXT)).thenReturn(List.of(agent));
        when(tags.selectAgent(thread, workgroup, VisitorCallTypeEnum.TEXT)).thenReturn(Optional.of(agent));
        when(queues.enqueueWorkgroup(eq(thread), any(), eq(workgroup), eq(request))).thenReturn(member);
        when(members.save(member)).thenReturn(member);
        installTransaction();
    }

    private void installTransaction() {
        ReflectionTestUtils.setField(strategy, "pushReachProperties", new PushReachProperties());
        ThreadRepository repository = mock(ThreadRepository.class);
        when(repository.findByUidForRouting("thread-1")).thenReturn(Optional.of(thread));
        PlatformTransactionManager manager = mock(PlatformTransactionManager.class);
        when(manager.getTransaction(any())).thenReturn(new SimpleTransactionStatus());
        ReflectionTestUtils.setField(strategy, "routingTransaction", new WorkgroupRoutingTransaction(repository, manager));
    }
}
