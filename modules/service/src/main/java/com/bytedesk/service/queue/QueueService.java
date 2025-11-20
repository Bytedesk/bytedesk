package com.bytedesk.service.queue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.thread.event.ThreadAcceptEvent;
import com.bytedesk.core.thread.event.ThreadAddTopicEvent;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.queue.exception.QueueFullException;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.queue.notification.QueueNotificationService;
import com.bytedesk.service.queue_member.mq.QueueMemberMessageService;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class QueueService {
    
    private final QueueMemberRestService queueMemberRestService;

    private final AgentRestService agentRestService;

    private final ThreadRestService threadRestService;

    private final QueueMemberMessageService queueMemberMessageService;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    private final QueueRepository queueRepository;

    private final UidUtils uidUtils;

    private final QueueNotificationService queueNotificationService;

    private Optional<QueueMemberEntity> findByThreadUid(String threadUid) {
        return queueMemberRestService.findByThreadUid(threadUid);
    }

    @Transactional
    public QueueMemberEntity enqueueRobot(ThreadEntity threadEntity, UserProtobuf agent, VisitorRequest visitorRequest) {
        return enqueueToQueue(threadEntity, agent, null, QueueTypeEnum.ROBOT);
    }

    @Transactional
    public QueueMemberEntity enqueueAgent(ThreadEntity threadEntity, AgentEntity agentEntity,
            VisitorRequest visitorRequest) {
        return enqueueAgentWithResult(threadEntity, agentEntity, visitorRequest).queueMember();
    }

    @Transactional
    public QueueEnqueueResult enqueueAgentWithResult(ThreadEntity threadEntity, AgentEntity agentEntity,
            VisitorRequest visitorRequest) {
        UserProtobuf agent = agentEntity.toUserProtobuf();
        boolean alreadyQueued = findByThreadUid(threadEntity.getUid()).isPresent();
        QueueMemberEntity queueMemberEntity = enqueueToQueue(threadEntity, agent, null, QueueTypeEnum.AGENT);
        if (!alreadyQueued) {
            queueNotificationService.publishQueueJoinNotice(agentEntity, queueMemberEntity);
        }
        return new QueueEnqueueResult(queueMemberEntity, alreadyQueued);
    }

    // @Transactional
    // public QueueMemberEntity enqueueWorkgroup(ThreadEntity threadEntity, UserProtobuf agent, 
    //     WorkgroupEntity workgroupEntity, VisitorRequest visitorRequest) {
    //     return enqueueWorkgroupWithResult(threadEntity, agent, workgroupEntity, visitorRequest).queueMember();
    // }

    // @Transactional
    // public QueueMemberEntity enqueueWorkgroup(ThreadEntity threadEntity, AgentEntity agentEntity, 
    //     WorkgroupEntity workgroupEntity, VisitorRequest visitorRequest) {
    //     return enqueueWorkgroupWithResult(threadEntity, agentEntity, workgroupEntity, visitorRequest).queueMember();
    // }

    @Transactional
    public QueueEnqueueResult enqueueWorkgroupWithResult(ThreadEntity threadEntity, UserProtobuf agent,
            WorkgroupEntity workgroupEntity, VisitorRequest visitorRequest) {
        boolean alreadyQueued = findByThreadUid(threadEntity.getUid()).isPresent();
        QueueMemberEntity queueMemberEntity = enqueueToQueue(threadEntity, agent, workgroupEntity, QueueTypeEnum.WORKGROUP);
        return new QueueEnqueueResult(queueMemberEntity, alreadyQueued);
    }

    @Transactional
    public QueueEnqueueResult enqueueWorkgroupWithResult(ThreadEntity threadEntity, AgentEntity agentEntity,
            WorkgroupEntity workgroupEntity, VisitorRequest visitorRequest) {
        UserProtobuf agent = agentEntity != null ? agentEntity.toUserProtobuf() : null;
        QueueEnqueueResult result = enqueueWorkgroupWithResult(threadEntity, agent, workgroupEntity, visitorRequest);
        if (agentEntity != null && !result.alreadyQueued()) {
            queueNotificationService.publishQueueJoinNotice(agentEntity, result.queueMember());
        }
        return result;
    }

    @Transactional
    public Optional<QueueAssignmentResult> assignNextAgentQueueMember(String agentUid) {
        if (!StringUtils.hasText(agentUid)) {
            return Optional.empty();
        }
        Optional<AgentEntity> agentOptional = agentRestService.findByUid(agentUid);
        if (!agentOptional.isPresent()) {
            log.warn("Skip auto-assign: agent {} not found", agentUid);
            return Optional.empty();
        }
        AgentEntity agent = agentOptional.get();
        QueueEntity agentQueue = getAgentOrRobotQueue(agent.toUserProtobuf(), agent.getOrgUid());
        if (agentQueue == null) {
            return Optional.empty();
        }
        return assignNextAgentQueueMember(agent, agentQueue);
    }

    private Optional<QueueAssignmentResult> assignNextAgentQueueMember(AgentEntity agent, QueueEntity agentQueue) {
        while (true) {
            Optional<QueueMemberEntity> candidateOptional = queueMemberRestService
                    .findEarliestAgentQueueMemberForUpdate(agentQueue.getUid());
            if (!candidateOptional.isPresent()) {
                return Optional.empty();
            }
            QueueMemberEntity member = candidateOptional.get();
            ThreadEntity thread = member.getThread();
            if (thread == null) {
                cleanupQueueMember(member, "missing thread");
                continue;
            }
            if (!ThreadProcessStatusEnum.QUEUING.name().equals(thread.getStatus())) {
                cleanupQueueMember(member, "threadStatus=" + thread.getStatus());
                continue;
            }
            ThreadEntity savedThread = finalizeThreadAssignment(agent, thread);
            finalizeQueueMemberAssignment(agent, member);
            publishAssignmentEvents(savedThread);
            log.info("Auto-assigned thread {} to agent {}", savedThread.getUid(), agent.getUid());
            return Optional.of(new QueueAssignmentResult(agent.getUid(), savedThread.getUid(), member.getUid()));
        }
    }

    private ThreadEntity finalizeThreadAssignment(AgentEntity agent, ThreadEntity thread) {
        UserProtobuf agentProtobuf = agent.toUserProtobuf();
        thread.setStatus(ThreadProcessStatusEnum.CHATTING.name());
        thread.setAgent(agentProtobuf.toJson());
        if (agent.getMember() != null && agent.getMember().getUser() != null) {
            thread.setOwner(agent.getMember().getUser());
        }
        return threadRestService.save(thread);
    }

    private void finalizeQueueMemberAssignment(AgentEntity agent, QueueMemberEntity member) {
        member.agentAutoAcceptThread();
        QueueMemberEntity savedMember = queueMemberRestService.save(member);
        Map<String, Object> updates = new HashMap<>();
        updates.put("agentAutoAcceptThread", true);
        updates.put("status", savedMember.getStatus());
        queueMemberMessageService.sendUpdateMessage(savedMember, updates);
        queueNotificationService.publishQueueAssignmentNotice(agent, savedMember);
    }

    private void cleanupQueueMember(QueueMemberEntity member, String reason) {
        member.setDeleted(true);
        ThreadEntity thread = member.getThread();
        if (thread != null && ThreadProcessStatusEnum.QUEUING.name().equals(thread.getStatus())) {
            thread.setStatus(ThreadProcessStatusEnum.CLOSED.name());
            threadRestService.save(thread);
        }
        QueueMemberEntity savedMember = queueMemberRestService.save(member);
        queueNotificationService.publishQueueLeaveNotice(savedMember);
        log.debug("Cleaned queue member {} during auto-assign: {}", member.getUid(), reason);
    }

    private void publishAssignmentEvents(ThreadEntity thread) {
        try {
            bytedeskEventPublisher.publishEvent(new ThreadAddTopicEvent(this, thread));
            bytedeskEventPublisher.publishEvent(new ThreadAcceptEvent(this, thread));
        } catch (Exception e) {
            log.warn("Failed to publish auto-assign events for thread {}: {}", thread.getUid(), e.getMessage());
        }
    }

    // @Transactional
    // public QueueMemberEntity enqueueWorkflow(ThreadEntity threadEntity, UserProtobuf workflow, VisitorRequest visitorRequest) {
    //     return enqueueToQueue(threadEntity, workflow, null, QueueTypeEnum.WORKFLOW);
    // }

    /**
     * 统一的入队方法
     */
    @Transactional
    private QueueMemberEntity enqueueToQueue(ThreadEntity threadEntity, UserProtobuf agent, 
            WorkgroupEntity workgroupEntity, QueueTypeEnum queueType) {
        
        // 1. 检查是否已存在队列成员
        Optional<QueueMemberEntity> memberOptional = findByThreadUid(threadEntity.getUid());
        if (memberOptional.isPresent()) {
            return handleExistingMember(memberOptional.get(), agent, threadEntity, queueType);
        }

        // 2. 创建新的队列成员
        return createNewQueueMember(threadEntity, agent, workgroupEntity, queueType);
    }

    /**
     * 处理已存在的队列成员
     */
    private QueueMemberEntity handleExistingMember(QueueMemberEntity member, UserProtobuf agent, 
            ThreadEntity threadEntity, QueueTypeEnum queueType) {
        
        if (queueType == QueueTypeEnum.WORKGROUP) {
            // 更新工作组队列的 agent/robot 队列
            QueueEntity agentOrRobotQueue = getAgentOrRobotQueue(agent, threadEntity.getOrgUid());
            validateQueue(agentOrRobotQueue, "Agent queue is full or not active");
            
            if (agent.isAgent()) {
                member.setAgentQueue(agentOrRobotQueue);
            } else {
                member.setRobotQueue(agentOrRobotQueue);
            }
        }
        
        return saveQueueMember(member);
    }

    /**
     * 创建新的队列成员
     */
    private QueueMemberEntity createNewQueueMember(ThreadEntity threadEntity, UserProtobuf agent, 
            WorkgroupEntity workgroupEntity, QueueTypeEnum queueType) {
        
        QueueEntity primaryQueue;
        String nickname;
        
        switch (queueType) {
            case ROBOT:
            case AGENT:
            case WORKFLOW:
            case UNIFIED:
                nickname = agent.getNickname();
                primaryQueue = getQueue(threadEntity, nickname);
                break;
            case WORKGROUP:
                nickname = workgroupEntity.getNickname();
                primaryQueue = getQueue(threadEntity, nickname);
                break;
            default:
                throw new IllegalArgumentException("Unsupported queue type: " + queueType);
        }
        
        validateQueue(primaryQueue, "Queue is full or not active");
        
        // 创建队列成员
        var memberBuilder = QueueMemberEntity.builder()
            .uid(uidUtils.getUid())
            .thread(threadEntity)
            .queueNumber(primaryQueue.getNextNumber())
            .visitorEnqueueAt(BdDateUtils.now())
            .orgUid(threadEntity.getOrgUid());
        
        // 根据队列类型设置相应的队列
        switch (queueType) {
            case ROBOT:
            case WORKFLOW:
            case UNIFIED:
                memberBuilder.robotQueue(primaryQueue);
                break;
            case AGENT:
                memberBuilder.agentQueue(primaryQueue);
                break;
            case WORKGROUP:
                memberBuilder.workgroupQueue(primaryQueue);
                // 同时设置 agent/robot 队列
                QueueEntity agentOrRobotQueue = getAgentOrRobotQueue(agent, threadEntity.getOrgUid());
                validateQueue(agentOrRobotQueue, "Agent queue is full or not active");
                
                if (agent.getType().equals(ThreadTypeEnum.AGENT.name())) {
                    memberBuilder.agentQueue(agentOrRobotQueue);
                } else {
                    memberBuilder.robotQueue(agentOrRobotQueue);
                }
                break;
        }
        
        return saveQueueMember(memberBuilder.build());
    }

    /**
     * 验证队列是否可以入队
     */
    private void validateQueue(QueueEntity queue, String errorMessage) {
        if (!queue.canEnqueue()) {
            throw new QueueFullException(errorMessage);
        }
    }

    /**
     * 保存队列成员并验证结果
     */
    private QueueMemberEntity saveQueueMember(QueueMemberEntity member) {
        try {
            QueueMemberEntity updatedMember = queueMemberRestService.save(member);
            if (updatedMember == null) {
                throw new RuntimeException("Failed to save queue member");
            }
            return updatedMember;
        } catch (DataIntegrityViolationException ex) {
            String threadUid = member.getThread() != null ? member.getThread().getUid() : null;
            if (threadUid != null) {
                Optional<QueueMemberEntity> existingMember = queueMemberRestService.findByThreadUid(threadUid);
                if (existingMember.isPresent()) {
                    log.debug("Queue member already exists for thread {}, returning existing record", threadUid);
                    return existingMember.get();
                }
            }
            throw ex;
        }
    }

    /**
     * 通用的队列获取或创建方法
     */
    @Transactional
    private QueueEntity getOrCreateQueue(String queueTopic, String nickname, String type, String orgUid) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        
        return retryOperation(() ->
            findLatestQueue(queueTopic, today)
                .orElseGet(() -> createQueueEntity(queueTopic, nickname, type, today, orgUid))
        );
    }

    /**
     * 重试操作的通用方法
     */
    private <T> T retryOperation(java.util.function.Supplier<T> operation) {
        while (true) {
            try {
                return operation.get();
            } catch (Exception e) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Queue operation interrupted", ie);
                }
            }
        }
    }

    /**
     * 根据线程实体获取或创建队列
     */
    @Transactional
    private QueueEntity getQueue(ThreadEntity threadEntity, String queueNickname) {
        String threadTopic = threadEntity.getTopic();
        String queueTopic = TopicUtils.getQueueTopicFromThreadTopic(threadTopic);
        return getOrCreateQueue(queueTopic, queueNickname, threadEntity.getType(), threadEntity.getOrgUid());
    }

    /**
     * 根据用户获取或创建 agent/robot 队列
     */
    @Transactional
    private QueueEntity getAgentOrRobotQueue(UserProtobuf user, String orgUid) {
        Assert.notNull(user, "User cannot be null");
        String queueTopic = TopicUtils.getQueueTopicFromUid(user.getUid());
        return getOrCreateQueue(queueTopic, user.getNickname(), user.getType(), orgUid);
    }

    public record QueueEnqueueResult(QueueMemberEntity queueMember, boolean alreadyQueued) { }

    public record QueueAssignmentResult(String agentUid, String threadUid, String queueMemberUid) { }

    private Optional<QueueEntity> findLatestQueue(String queueTopic, String day) {
        return queueRepository.findFirstByTopicAndDayAndDeletedFalseOrderByCreatedAtDesc(queueTopic, day);
    }

    private QueueEntity createQueueEntity(String queueTopic, String nickname, String type, String day, String orgUid) {
        QueueEntity queue = QueueEntity.builder()
                .uid(uidUtils.getUid())
                .nickname(nickname)
                .type(type)
                .topic(queueTopic)
                .day(day)
                .status(QueueStatusEnum.ACTIVE.name())
                .orgUid(orgUid)
                .build();
        try {
            return queueRepository.save(queue);
        } catch (DataIntegrityViolationException e) {
            return queueRepository.findByTopicAndDayAndDeletedFalse(queueTopic, day)
                    .orElseThrow(() -> new RuntimeException("Queue creation failed for topic " + queueTopic, e));
        }
    }

}