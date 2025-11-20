package com.bytedesk.service.queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import jakarta.persistence.EntityManager;

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRepository;
import com.bytedesk.service.queue_member.QueueMemberResponse;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.queue_member.QueueMemberStatusEnum;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.queue.notification.QueueNotificationService;

@ExtendWith(MockitoExtension.class)
class QueueMemberRestServiceTest {

        @Mock
        private QueueMemberRepository queueMemberRepository;

        @Mock
        private UidUtils uidUtils;

        @Mock
        private ThreadRestService threadRestService;

        @Mock
        private QueueAuditLogger queueAuditLogger;

        @Mock
        private QueueNotificationService queueNotificationService;

        @Mock
        private EntityManager entityManager;

        private ModelMapper modelMapper;

        private QueueMemberRestService queueMemberRestService;

        @BeforeEach
        void setUp() {
                modelMapper = new ModelMapper();
                queueMemberRestService = new QueueMemberRestService(
                                queueMemberRepository,
                                modelMapper,
                                uidUtils,
                                entityManager,
                                threadRestService,
                                queueAuditLogger,
                                queueNotificationService);
        }

        @Test
        void enqueueAssignsSequentialQueueNumbers() {
                QueueEntity agentQueue = QueueEntity.builder()
                                .uid("queue-1")
                                .nickname("Agent Queue")
                                .topic("queue/topic/agent-1")
                                .day("2025-11-20")
                                .status(QueueStatusEnum.ACTIVE.name())
                                .build();
                agentQueue.setOrgUid("org-1");

                ThreadEntity threadOne = buildThread("thread-1");
                ThreadEntity threadTwo = buildThread("thread-2");

                when(queueMemberRepository.findIdleBefore(any())).thenReturn(Collections.emptyList());
                when(queueMemberRepository.findFirstByThreadUidAndDeletedFalseAndStatusInForUpdate(eq("thread-1"),
                                anyList()))
                                .thenReturn(Optional.empty());
                when(queueMemberRepository.findFirstByThreadUidAndDeletedFalseAndStatusInForUpdate(eq("thread-2"),
                                anyList()))
                                .thenReturn(Optional.empty());
                when(queueMemberRepository.findMaxQueueNumberForQueue(eq(agentQueue), anyString()))
                                .thenReturn(0, 1);
                when(queueMemberRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
                when(uidUtils.getUid()).thenReturn("member-1", "member-2");

                QueueMemberEntity first = queueMemberRestService.enqueue(
                                threadOne,
                                agentQueue,
                                QueueTypeEnum.AGENT,
                                member -> member.setAgentQueue(agentQueue));
                QueueMemberEntity second = queueMemberRestService.enqueue(
                                threadTwo,
                                agentQueue,
                                QueueTypeEnum.AGENT,
                                member -> member.setAgentQueue(agentQueue));

                assertEquals(1, first.getQueueNumber(), "First visitor should receive queue number 1");
                assertEquals("member-1", first.getUid());
                assertEquals(2, second.getQueueNumber(), "Second visitor should increment queue number");
                assertEquals("member-2", second.getUid());

                verify(queueMemberRepository, times(2)).save(any());
                verify(queueMemberRepository, atLeastOnce()).findIdleBefore(any());
                verify(queueAuditLogger, times(2)).logQueueJoin(any(QueueMemberEntity.class), any(ThreadEntity.class),
                                eq(agentQueue), eq(QueueTypeEnum.AGENT));
        }

        @Test
        void enqueueReturnsExistingMemberWhenDuplicateThread() {
                QueueEntity agentQueue = QueueEntity.builder()
                                .uid("queue-dup")
                                .nickname("Agent Queue")
                                .topic("queue/topic/agent-dup")
                                .day("2025-11-20")
                                .status(QueueStatusEnum.ACTIVE.name())
                                .build();
                agentQueue.setOrgUid("org-1");

                ThreadEntity threadEntity = buildThread("thread-dup");
                QueueMemberEntity existing = QueueMemberEntity.builder()
                                .uid("existing-member")
                                .queueNumber(5)
                                .status(QueueMemberStatusEnum.QUEUING.name())
                                .thread(threadEntity)
                                .agentQueue(agentQueue)
                                .build();

                when(queueMemberRepository.findIdleBefore(any())).thenReturn(Collections.emptyList());
                when(queueMemberRepository.findFirstByThreadUidAndDeletedFalseAndStatusInForUpdate(eq("thread-dup"),
                                anyList()))
                                .thenReturn(Optional.of(existing));

                QueueMemberEntity result = queueMemberRestService.enqueue(
                                threadEntity,
                                agentQueue,
                                QueueTypeEnum.AGENT,
                                member -> member.setAgentQueue(agentQueue));

                assertThat(result).isSameAs(existing);
                verify(queueMemberRepository, never()).save(any());
                verify(queueMemberRepository, never()).findMaxQueueNumberForQueue(any(), anyString());
                verify(queueAuditLogger, never()).logQueueJoin(any(), any(), any(), any());
        }

        @Test
        void findAgentQueueMembersReturnsPagedSnapshot() {
                Pageable pageable = PageRequest.of(0, 10);
                QueueMemberEntity entityOne = buildQueueMemberEntity("member-1", 1);
                QueueMemberEntity entityTwo = buildQueueMemberEntity("member-2", 2);
                Page<QueueMemberEntity> entityPage = new PageImpl<>(Arrays.asList(entityOne, entityTwo), pageable, 2);

                when(queueMemberRepository.findByAgentQueue_UidAndDeletedFalseAndStatusOrderByQueueNumberAsc(
                                eq("queue-uid"),
                                eq(QueueMemberStatusEnum.QUEUING.name()),
                                eq(pageable)))
                                .thenReturn(entityPage);

                QueueMemberResponse responseOne = QueueMemberResponse.builder().uid("member-1").queueNumber(1).build();
                QueueMemberResponse responseTwo = QueueMemberResponse.builder().uid("member-2").queueNumber(2).build();

                try (MockedStatic<ServiceConvertUtils> mocked = Mockito.mockStatic(ServiceConvertUtils.class)) {
                        mocked.when(() -> ServiceConvertUtils.convertToQueueMemberResponse(entityOne))
                                        .thenReturn(responseOne);
                        mocked.when(() -> ServiceConvertUtils.convertToQueueMemberResponse(entityTwo))
                                        .thenReturn(responseTwo);

                        Page<QueueMemberResponse> result = queueMemberRestService.findAgentQueueMembers("queue-uid",
                                        pageable);

                        assertThat(result.getContent()).containsExactly(responseOne, responseTwo);
                        assertThat(result.getTotalElements()).isEqualTo(2);
                }
        }

        @Test
        void enqueueClearsPersistenceContextAfterCollision() {
                QueueEntity agentQueue = QueueEntity.builder()
                                .uid("queue-collision")
                                .nickname("Agent Queue")
                                .topic("queue/topic/agent-collision")
                                .day("2025-11-20")
                                .status(QueueStatusEnum.ACTIVE.name())
                                .build();
                agentQueue.setOrgUid("org-1");

                ThreadEntity threadEntity = buildThread("thread-collision");
                DataIntegrityViolationException collision = new DataIntegrityViolationException(
                                "duplicate key value violates unique constraint UK7aviqofcxw7ae3fped747qrl7");

                when(queueMemberRepository.findIdleBefore(any())).thenReturn(Collections.emptyList());
                when(queueMemberRepository.findFirstByThreadUidAndDeletedFalseAndStatusInForUpdate(eq("thread-collision"),
                                anyList())).thenReturn(Optional.empty());
                when(queueMemberRepository.findMaxQueueNumberForQueue(eq(agentQueue), anyString()))
                                .thenReturn(0, 0, 1);
                when(queueMemberRepository.save(any())).thenThrow(collision).thenAnswer(invocation -> invocation.getArgument(0));
                when(uidUtils.getUid()).thenReturn("member-collision-1", "member-collision-2");
                when(entityManager.contains(any())).thenReturn(true);

                QueueMemberEntity result = queueMemberRestService.enqueue(
                                threadEntity,
                                agentQueue,
                                QueueTypeEnum.AGENT,
                                member -> member.setAgentQueue(agentQueue));

                assertThat(result.getUid()).isEqualTo("member-collision-2");
                verify(entityManager, atLeastOnce()).detach(any());
                verify(entityManager).clear();
                verify(queueMemberRepository, times(2)).save(any());
                verify(queueAuditLogger, times(1)).logQueueJoin(any(QueueMemberEntity.class), eq(threadEntity),
                                eq(agentQueue), eq(QueueTypeEnum.AGENT));
        }

        private ThreadEntity buildThread(String uid) {
                ThreadEntity thread = new ThreadEntity();
                thread.setUid(uid);
                thread.setTopic("thread/topic/" + uid);
                thread.setOrgUid("org-1");
                thread.setStatus(ThreadProcessStatusEnum.QUEUING.name());
                thread.setType(ThreadTypeEnum.AGENT.name());
                thread.setCreatedAt(ZonedDateTime.now());
                return thread;
        }

        private QueueMemberEntity buildQueueMemberEntity(String uid, int queueNumber) {
                QueueMemberEntity entity = QueueMemberEntity.builder()
                                .uid(uid)
                                .queueNumber(queueNumber)
                                .status(QueueMemberStatusEnum.QUEUING.name())
                                .build();
                QueueEntity queueEntity = QueueEntity.builder()
                                .uid("queue-uid")
                                .status(QueueStatusEnum.ACTIVE.name())
                                .topic("queue/topic/agent-1")
                                .day("2025-11-20")
                                .build();
                entity.setAgentQueue(queueEntity);
                entity.setThread(buildThread("thread-" + uid));
                return entity;
        }
}
