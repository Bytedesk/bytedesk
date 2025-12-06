package com.bytedesk.service.queue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

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
import com.bytedesk.service.workgroup.WorkgroupRepository;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class QueueService {

    private final QueueMemberRestService queueMemberRestService;

    private final QueueRepository queueRepository;

    private final WorkgroupRepository workgroupRepository;

    private final UidUtils uidUtils;

    @Transactional
    public QueueMemberEntity enqueueRobot(ThreadEntity threadEntity, UserProtobuf agent,
            VisitorRequest visitorRequest) {
        return enqueueToQueue(threadEntity, agent, null, QueueTypeEnum.ROBOT);
    }

    @Transactional
    public QueueMemberEntity enqueueAgent(ThreadEntity threadEntity, UserProtobuf agent,
            VisitorRequest visitorRequest) {
        return enqueueToQueue(threadEntity, agent, null, QueueTypeEnum.AGENT);
    }

    @Transactional
    public QueueMemberEntity enqueueWorkgroup(ThreadEntity threadEntity, UserProtobuf agent,
            WorkgroupEntity workgroupEntity, VisitorRequest visitorRequest) {
        return enqueueToQueue(threadEntity, agent, workgroupEntity, QueueTypeEnum.WORKGROUP);
    }

    @Transactional
    public QueueMemberEntity enqueueWorkflow(ThreadEntity threadEntity, UserProtobuf workflow,
            VisitorRequest visitorRequest) {
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
            if (agent == null) {
                log.debug("Existing workgroup queue member stays unassigned - threadUid: {}", threadEntity.getUid());
                return saveQueueMember(member);
            }

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
                memberBuilder.robotQueue(primaryQueue);
                break;
            case AGENT:
                memberBuilder.agentQueue(primaryQueue);
                break;
            case WORKFLOW:
                memberBuilder.workflowQueue(primaryQueue);
                break;
            case WORKGROUP:
                memberBuilder.workgroupQueue(primaryQueue);
                // 同时设置 agent/robot 队列（仅当 agent 不为 null 时）
                // agent 为 null 表示所有客服满员，进入排队等待分配
                if (agent != null) {
                    QueueEntity agentOrRobotQueue = getAgentOrRobotQueue(agent, threadEntity.getOrgUid());
                    validateQueue(agentOrRobotQueue, "Agent queue is full or not active");

                    if (ThreadTypeEnum.AGENT.name().equalsIgnoreCase(agent.getType())) {
                        memberBuilder.agentQueue(agentOrRobotQueue);
                    } else {
                        memberBuilder.robotQueue(agentOrRobotQueue);
                    }
                } else {
                    // 当 agent 为 null 时，表示所有客服满员，进入排队等待分配。
                }
                break;
        }

        return saveQueueMember(memberBuilder.build());
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
    private QueueEntity getAgentOrRobotQueue(UserProtobuf agent, String orgUid) {
        Assert.notNull(agent, "User cannot be null");
        String queueTopic = TopicUtils.getQueueTopicFromUid(agent.getUid());
        return getOrCreateQueue(queueTopic, agent.getNickname(), agent.getType(), orgUid);
    }

    /**
     * 通用的队列获取或创建方法
     */
    @Transactional
    private QueueEntity getOrCreateQueue(String queueTopic, String nickname, String type, String orgUid) {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        return retryOperation(() -> findByTopicAndDay(queueTopic, today)
                .orElseGet(() -> createQueueEntity(queueTopic, nickname, type, today, orgUid)));
    }

    /**
     * 根据队列主题和日期查询队列
     * 
     * @param queueTopic 队列主题
     * @param day        日期（格式：yyyy-MM-dd）
     * @return 队列实体（可选）
     */
    public Optional<QueueEntity> findByTopicAndDay(String queueTopic, String day) {
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
     * 验证队列是否可以入队
     */
    private void validateQueue(QueueEntity queue, String errorMessage) {
        if (!queue.canEnqueue()) {
            throw new QueueFullException(errorMessage);
        }
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
     * 获取客服的完整排队人数统计
     * 包括：
     * 1. 直接在客服队列（agentQueue）中排队的人数（一对一会话）
     * 2. 客服所在工作组队列中等待分配的排队人数（工作组会话，agentQueue 为 null）
     * 
     * @param agentUid 客服UID
     * @return 完整排队人数统计结果
     */
    public AgentQueuingCount getAgentTotalQueuingCount(String agentUid) {
        if (!StringUtils.hasText(agentUid)) {
            return new AgentQueuingCount(0, 0, 0);
        }

        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        
        // 1. 统计客服队列中的排队人数（一对一会话）
        String agentQueueTopic = TopicUtils.getQueueTopicFromUid(agentUid);
        Optional<QueueEntity> agentQueueOpt = findByTopicAndDay(agentQueueTopic, today);
        int agentDirectQueuingCount = 0;
        if (agentQueueOpt.isPresent()) {
            agentDirectQueuingCount = queueMemberRestService.countQueuingByAgentQueueUid(agentQueueOpt.get().getUid());
        }

        // 2. 统计客服所在工作组中未分配客服的排队人数
        List<WorkgroupEntity> workgroups = workgroupRepository.findByAgentUid(agentUid);
        int workgroupUnassignedQueuingCount = 0;
        
        if (!workgroups.isEmpty()) {
            List<String> workgroupQueueUids = new ArrayList<>();
            for (WorkgroupEntity workgroup : workgroups) {
                String workgroupQueueTopic = TopicUtils.getQueueTopicFromUid(workgroup.getUid());
                Optional<QueueEntity> workgroupQueueOpt = findByTopicAndDay(workgroupQueueTopic, today);
                if (workgroupQueueOpt.isPresent()) {
                    workgroupQueueUids.add(workgroupQueueOpt.get().getUid());
                }
            }
            
            if (!workgroupQueueUids.isEmpty()) {
                workgroupUnassignedQueuingCount = queueMemberRestService
                    .countUnassignedQueuingByWorkgroupQueueUids(workgroupQueueUids);
            }
        }

        int totalQueuingCount = agentDirectQueuingCount + workgroupUnassignedQueuingCount;
        
        // log.debug("客服排队统计 - agentUid: {}, 一对一排队: {}, 工作组未分配排队: {}, 总计: {}",
        //         agentUid, agentDirectQueuingCount, workgroupUnassignedQueuingCount, totalQueuingCount);
        
        return new AgentQueuingCount(agentDirectQueuingCount, workgroupUnassignedQueuingCount, totalQueuingCount);
    }

    /**
     * 客服排队人数统计结果
     */
    public record AgentQueuingCount(
        int directQueuingCount,           // 一对一排队人数
        int workgroupUnassignedCount,     // 工作组未分配排队人数
        int totalQueuingCount             // 总排队人数
    ) {}

    /**
     * 获取客服的完整队列统计信息
     * 包括今日服务人数、排队人数、接待人数、留言数、转人工数等
     * 
     * @param agentUid 客服UID
     * @return 完整队列统计响应
     */
    public AgentQueueStatsResponse getAgentQueueStats(String agentUid) {
        if (!StringUtils.hasText(agentUid)) {
            return AgentQueueStatsResponse.builder()
                    .agentUid(agentUid)
                    .build();
        }

        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String agentQueueTopic = TopicUtils.getQueueTopicFromUid(agentUid);
        Optional<QueueEntity> agentQueueOpt = findByTopicAndDay(agentQueueTopic, today);

        // 获取排队统计
        AgentQueuingCount queuingCount = getAgentTotalQueuingCount(agentUid);

        // 如果客服队列不存在，返回基础统计
        if (agentQueueOpt.isEmpty()) {
            return AgentQueueStatsResponse.builder()
                    .agentUid(agentUid)
                    .queuingCount(queuingCount.totalQueuingCount())
                    .directQueuingCount(queuingCount.directQueuingCount())
                    .workgroupUnassignedCount(queuingCount.workgroupUnassignedCount())
                    .build();
        }

        QueueEntity agentQueue = agentQueueOpt.get();

        int totalCount = agentQueue.getTotalCount();
        int chattingCount = agentQueue.getChattingCount();
        int offlineCount = agentQueue.getOfflineCount();
        int closedCount = agentQueue.getClosedCount();
        int leaveMsgCount = agentQueue.getMessageLeaveCount();
        int robotToAgentCount = agentQueue.getRobotToAgentCount();
        int robotingCount = agentQueue.getRobotingCount();
        int agentServedCount = agentQueue.getAgentServedCount();
        List<Integer> threadsCountByHour = new ArrayList<>(agentQueue.getThreadsCountByHour());

        // log.debug("客服队列统计 - agentUid: {}, 总人数: {}, 排队: {}, 接待: {}, 留言: {}, 转人工: {}",
        //         agentUid, totalCount, queuingCount.totalQueuingCount(), chattingCount, leaveMsgCount, robotToAgentCount);

        return AgentQueueStatsResponse.builder()
                .agentUid(agentUid)
                .totalCount(totalCount)
                .queuingCount(queuingCount.totalQueuingCount())
                .directQueuingCount(queuingCount.directQueuingCount())
                .workgroupUnassignedCount(queuingCount.workgroupUnassignedCount())
                .chattingCount(chattingCount)
                .offlineCount(offlineCount)
                .closedCount(closedCount)
                .leaveMsgCount(leaveMsgCount)
                .robotToAgentCount(robotToAgentCount)
                .robotingCount(robotingCount)
                .agentServedCount(agentServedCount)
                .threadsCountByHour(threadsCountByHour)
                .build();
    }

}