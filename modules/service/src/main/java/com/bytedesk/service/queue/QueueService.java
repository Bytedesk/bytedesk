package com.bytedesk.service.queue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.queue.exception.QueueFullException;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class QueueService {
    
    private final QueueMemberRestService queueMemberRestService;

    public final QueueRestService queueRestService;

    private final UidUtils uidUtils;

    @Transactional
    public QueueMemberEntity enqueueRobot(ThreadEntity threadEntity, UserProtobuf agent, VisitorRequest visitorRequest) {
        return enqueueToQueue(threadEntity, agent, null, QueueTypeEnum.ROBOT);
    }

    @Transactional
    public QueueMemberEntity enqueueAgent(ThreadEntity threadEntity, UserProtobuf agent, VisitorRequest visitorRequest) {
        return enqueueToQueue(threadEntity, agent, null, QueueTypeEnum.AGENT);
    }

    @Transactional
    public QueueMemberEntity enqueueWorkgroup(ThreadEntity threadEntity, UserProtobuf agent, 
        WorkgroupEntity workgroupEntity, VisitorRequest visitorRequest) {
        return enqueueToQueue(threadEntity, agent, workgroupEntity, QueueTypeEnum.WORKGROUP);
    }

    @Transactional
    public QueueMemberEntity enqueueWorkflow(ThreadEntity threadEntity, UserProtobuf workflow, VisitorRequest visitorRequest) {
        return enqueueToQueue(threadEntity, workflow, null, QueueTypeEnum.WORKFLOW);
    }

    /**
     * 统一的入队方法
     */
    @Transactional
    private QueueMemberEntity enqueueToQueue(ThreadEntity threadEntity, UserProtobuf agent, 
            WorkgroupEntity workgroupEntity, QueueTypeEnum queueType) {
        
        // 1. 检查是否已存在队列成员
        Optional<QueueMemberEntity> memberOptional = queueMemberRestService.findByThreadUid(threadEntity.getUid());
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
        QueueMemberEntity updatedMember = queueMemberRestService.save(member);
        if (updatedMember == null) {
            throw new RuntimeException("Failed to save queue member");
        }
        return updatedMember;
    }

    /**
     * 通用的队列获取或创建方法
     */
    @Transactional
    private QueueEntity getOrCreateQueue(String queueTopic, String nickname, String type, String orgUid) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        
        return retryOperation(() -> 
            queueRestService.findByTopicAndDay(queueTopic, today)
                .orElseGet(() -> {
                    QueueRequest request = QueueRequest.builder()
                        .nickname(nickname)
                        .type(type)
                        .topic(queueTopic)
                        .day(today)
                        .status(QueueStatusEnum.ACTIVE.name())
                        .orgUid(orgUid)
                        .build();
                    return queueRestService.createQueue(request);
                })
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

}