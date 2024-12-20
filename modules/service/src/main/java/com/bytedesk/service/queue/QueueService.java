package com.bytedesk.service.queue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentService;
import com.bytedesk.service.queue.exception.QueueFullException;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRepository;
import com.bytedesk.service.queue_member.QueueMemberStatusEnum;
import com.bytedesk.service.visitor.VisitorRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class QueueService {

    private final QueueRepository queueRepository;
    
    private final QueueMemberRepository memberRepository;

    private final AgentService agentService;

    public final QueueRestService queueRestService;

    private final UidUtils uidUtils;

    @Transactional
    public QueueMemberEntity enqueue(ThreadEntity threadEntity, AgentEntity agentEntity, VisitorRequest visitorRequest) {
        // 1. 获取或创建队列
        QueueEntity queue = getOrCreateQueue(threadEntity);
        if (!queue.canEnqueue()) {
            throw new QueueFullException("Queue is full or not active");
        }
        
        // 2. 创建队列成员
        QueueMemberEntity member = createQueueMemberEntity(threadEntity, agentEntity, visitorRequest, queue);
        
        // 3. 更新队列统计
        updateQueueStats(queue);

        return member;
    }

    private QueueEntity getOrCreateQueue(ThreadEntity threadEntity) {
        String threadTopic = threadEntity.getTopic();
        String queueTopic = TopicUtils.getQueueTopicFromThreadTopic(threadTopic);
        // 按照ISO 8601标准格式化日期，即yyyy-MM-dd格式
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        return queueRestService.findByQueueTopicAndDay(queueTopic, today)
            .orElseGet(() -> {
                QueueEntity queue = QueueEntity.builder()
                    .day(today)
                    .queueTopic(queueTopic)
                    .queueType(threadEntity.getType())
                    .status(QueueStatusEnum.ACTIVE.name())
                    .build();
                queue.setUid(uidUtils.getUid());
                queue.setOrgUid(threadEntity.getOrgUid());
                return queueRepository.save(queue);
            });
    }

    public QueueMemberEntity createQueueMemberEntity(ThreadEntity threadEntity, AgentEntity agentEntity, VisitorRequest visitorRequest, QueueEntity queue) {
        // 
        QueueMemberEntity member = QueueMemberEntity.builder()
            .queueUid(queue.getUid())
            .threadUid(threadEntity.getUid())
            .visitorUid(visitorRequest.getUid())
            .agentUid(agentEntity.getUid())
            .queueNumber(queue.getNextNumber())
            .enqueueTime(LocalDateTime.now())
            .status(QueueMemberStatusEnum.WAITING.name())
            .build();
        member.setUid(uidUtils.getUid());
        member.setOrgUid(threadEntity.getOrgUid());
        // 
        QueueMemberEntity savedMember = memberRepository.save(member);
        if (savedMember == null) {
            throw new RuntimeException("Failed to create queue member");
        }
        return savedMember;
    }

    private void updateQueueStats(QueueEntity queue) {
        int waiting = memberRepository.countByQueueUidAndStatus(
            queue.getUid(), QueueMemberStatusEnum.WAITING.name());
        int serving = memberRepository.countByQueueUidAndStatus(
            queue.getUid(), QueueMemberStatusEnum.SERVING.name());
        int finished = memberRepository.countByQueueUidAndEndStatusIsTrue(queue.getUid());
        int avgWait = calculateAverageWaitTime(queue.getUid());
        
        queue.updateStats(waiting, serving, finished, avgWait);
        queueRepository.save(queue);
    }

    private int calculateAverageWaitTime(String queueUid) {
        Double avgWaitTime = memberRepository.calculateAverageWaitTime(queueUid);
        return avgWaitTime != null ? avgWaitTime.intValue() : 0;
    }
    
    @Transactional
    public void dequeue(String threadTopic, QueueStatusEnum status) {
        // 1. 查找队列成员
        QueueMemberEntity member = memberRepository.findByThreadUid(threadTopic)
            .orElseThrow(() -> new RuntimeException("Queue member not found"));

        // 2. 更新状态
        member.updateStatus(status.name(), null);
        memberRepository.save(member);

        // 3. 更新队列统计
        QueueEntity queue = queueRepository.findByUid(member.getQueueUid())
            .orElseThrow(() -> new RuntimeException("Queue not found"));
        updateQueueStats(queue);
    }

    
    public List<String> getQueuedThreads(QueueStatusEnum status) {
        // return queueRepository.findByStatus(status.name())
        //     .stream()
        //     .map(QueueEntity::getThreadUid)
        //     .toList();
        return new ArrayList<>();
    }

    
    public int getQueuePosition(String threadTopic) {
        QueueMemberEntity member = memberRepository.findByThreadUid(threadTopic)
            .orElseThrow(() -> new RuntimeException("Queue member not found"));
        return memberRepository.countByQueueUidAndPriorityGreaterThan(
            member.getQueueUid(), member.getPriority());
    }

    
    @Transactional
    public void updateStatus(String threadUid, QueueStatusEnum status) {
        // QueueEntity queueItem = queueRepository.findByThreadUid(threadUid)
        //     .orElseThrow(() -> new RuntimeException("Queue item not found"));
        // queueItem.setStatus(status.name());
        // // if (status == QueueStatusEnum.COMPLETED || 
        // //     status == QueueStatusEnum.CANCELLED ||
        // //     status == QueueStatusEnum.TIMEOUT) {
        // //     queueItem.setEndTime(LocalDateTime.now());
        // // }
        // queueRepository.save(queueItem);
    }

    
    public int getEstimatedWaitTime(String threadUid) {
        QueueMemberEntity member = memberRepository.findByThreadUid(threadUid)
            .orElseThrow(() -> new RuntimeException("Queue member not found"));
            
        // 1. 获取前面等待数量
        int queuePosition = memberRepository.countByQueueUidAndPriorityGreaterThan(
            member.getQueueUid(), member.getPriority());
        
        // 2. 获取平均会话时长(10分钟)
        int avgThreadDuration = 10;
        
        // 3. 获取在线客服数量
        List<String> onlineAgents = agentService.getOnlineAgents();
        if (onlineAgents.isEmpty()) {
            return -1;
        }
        
        // 4. 计算预计等待时间
        return (queuePosition * avgThreadDuration) / onlineAgents.size();
    }

    @Transactional
    public void checkQueueTimeout() {
        // 1. 获取所有等待中的成员
        List<QueueMemberEntity> waitingMembers = 
            memberRepository.findByStatus(QueueMemberStatusEnum.WAITING.name());
        
        // 2. 检查超时
        LocalDateTime now = LocalDateTime.now();
        for (QueueMemberEntity member : waitingMembers) {
            if (member.getEnqueueTime().plusMinutes(30).isBefore(now)) {
                member.updateStatus(QueueMemberStatusEnum.TIMEOUT.name(), null);
                memberRepository.save(member);
                
                // 更新队列统计
                QueueEntity queue = queueRepository.findByUid(member.getQueueUid())
                    .orElseThrow(() -> new RuntimeException("Queue not found"));
                updateQueueStats(queue);
                
                log.info("Thread {} queue timeout", member.getThreadUid());
            }
        }
    }

    
    @Transactional
    public void processQueue() {
        try {
            // 1. 获取等待中的成员(按优先级排序)
            List<QueueMemberEntity> waitingMembers = 
                memberRepository.findByStatusOrderByPriorityDesc(
                    QueueMemberStatusEnum.WAITING.name());
            if (waitingMembers.isEmpty()) {
                return;
            }
            
            // 2. 获取可用客服
            List<AgentEntity> availableAgents = agentService.getAvailableAgents();
            if (availableAgents.isEmpty()) {
                return;
            }

            // 3. 依次处理排队成员
            for (QueueMemberEntity member : waitingMembers) {
                // ThreadEntity thread = threadRepository.findByUid(member.getThreadUid())
                //     .orElseThrow(() -> new RuntimeException("Thread not found"));
                    
                try {
                    // 分配客服
                    AgentEntity agent = AgentEntity.builder().build(); // assignmentService.selectAgent(availableAgents, thread);
                    // assignmentService.assignToAgent(thread, agent);
                    
                    // 更新排队状态
                    member.updateStatus(QueueMemberStatusEnum.COMPLETED.name(), agent.getUid());
                    memberRepository.save(member);
                    
                    // 更新队列统计
                    QueueEntity queue = queueRepository.findByUid(member.getQueueUid())
                        .orElseThrow(() -> new RuntimeException("Queue not found"));
                    updateQueueStats(queue);
                    
                    // 更新可用客服列表
                    availableAgents.remove(agent);
                    if (availableAgents.isEmpty()) {
                        break;
                    }
                } catch (Exception e) {
                    log.error("Failed to process queue member", e);
                }
            }
        } catch (Exception e) {
            log.error("Failed to process queue", e);
        }
    }

    
    public QueueStats getQueueStats() {
        QueueStats stats = new QueueStats();
        
        // 统计各状态会话数
        stats.setTotalThreads((int) queueRepository.count());
        // stats.setWaitingThreads(queueRepository.countByStatus(QueueStatusEnum.WAITING));
        // stats.setProcessingThreads(queueRepository.countByStatus(QueueStatusEnum.PROCESSING));
        
        // 计算平均等待时间
        // Double avgWaitTime = queueRepository.calculateAverageWaitTime();
        // stats.setAvgWaitTime(avgWaitTime != null ? avgWaitTime.intValue() : 0);
        
        // 计算最长等待时间
        // Integer maxWaitTime = queueRepository.calculateMaxWaitTime();
        // stats.setMaxWaitTime(maxWaitTime != null ? maxWaitTime : 0);
        
        // 计算超时率和取消率
        // long timeoutCount = queueRepository.countByStatus(QueueStatusEnum.TIMEOUT);
        // long cancelCount = queueRepository.countByStatus(QueueStatusEnum.CANCELLED);
        // stats.setTimeoutRate(stats.getTotalThreads() > 0 ? (double) timeoutCount / stats.getTotalThreads() : 0);
        // stats.setCancelRate(stats.getTotalThreads() > 0 ? (double) cancelCount / stats.getTotalThreads() : 0);
        
        return stats;
    }
} 