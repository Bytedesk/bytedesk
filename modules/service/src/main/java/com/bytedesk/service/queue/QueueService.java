package com.bytedesk.service.queue;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.service.queue.exception.QueueFullException;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.visitor.VisitorRequest;
import com.bytedesk.service.workgroup.WorkgroupEntity;

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
        // 1. 获取或创建队列
        QueueEntity queue = getQueue(threadEntity, agent.getNickname());
        if (!queue.canEnqueue()) {
            throw new QueueFullException("Queue is full or not active");
        }
        // 
        Optional<QueueMemberEntity> memberOptional = queueMemberRestService.findByThreadUid(threadEntity.getUid());
        if (memberOptional.isPresent()) {
            return memberOptional.get();
        }
        // 创建队列成员实体并保存到数据库
        QueueMemberEntity member = QueueMemberEntity.builder()
            .uid(uidUtils.getUid())
            .robotQueue(queue)
            .thread(threadEntity)
            .queueNumber(queue.getNextNumber())
            .visitorEnqueueAt(ZonedDateTime.now())
            .orgUid(threadEntity.getOrgUid())
            .build();
        // 
        QueueMemberEntity updatedMember = queueMemberRestService.save(member);
        if (updatedMember == null) {
            throw new RuntimeException("Failed to update queue member");
        }
        // 更新工作组队列
        return updatedMember;
    }

    @Transactional
    public QueueMemberEntity enqueueAgent(ThreadEntity threadEntity, UserProtobuf agent, VisitorRequest visitorRequest) {
        // 1. 获取或创建队列
        QueueEntity queue = getQueue(threadEntity, agent.getNickname());
        if (!queue.canEnqueue()) {
            throw new QueueFullException("Queue is full or not active");
        }
        // 2. 创建队列成员
        Optional<QueueMemberEntity> memberOptional = queueMemberRestService.findByThreadUid(threadEntity.getUid());
        if (memberOptional.isPresent()) {
            return memberOptional.get();
        }
        // 创建队列成员实体并保存到数据库
        QueueMemberEntity member = QueueMemberEntity.builder()
            .uid(uidUtils.getUid())
            .agentQueue(queue)
            .thread(threadEntity)
            .queueNumber(queue.getNextNumber())
            .visitorEnqueueAt(ZonedDateTime.now())
            .orgUid(threadEntity.getOrgUid())
            .build();
        // 
        QueueMemberEntity updatedMember = queueMemberRestService.save(member);
        if (updatedMember == null) {
            throw new RuntimeException("Failed to update queue member");
        }
        // 更新工作组队列
        return updatedMember;
    }

    @Transactional
    public QueueMemberEntity enqueueWorkgroup(ThreadEntity threadEntity, UserProtobuf agent, 
        WorkgroupEntity workgroupEntity, VisitorRequest visitorRequest) {
        // 1. 获取或创建工作组队列
        QueueEntity workgroupQueue = getQueue(threadEntity, workgroupEntity.getNickname());
        if (!workgroupQueue.canEnqueue()) {
            throw new QueueFullException("Queue is full or not active");
        }
        
        // 2. 获取或创建客服队列
        QueueEntity agentQueue = getAgentOrRobotQueue(agent, threadEntity.getOrgUid());
        if (!agentQueue.canEnqueue()) {
            throw new QueueFullException("Agent queue is full or not active");
        }

        // 3. 创建工作组队列成员
        Optional<QueueMemberEntity> memberOptional = queueMemberRestService.findByThreadUid(threadEntity.getUid());
        if (memberOptional.isPresent()) {
            QueueMemberEntity member = memberOptional.get();
            // 更新工作组队列
            if (agent.isAgent()) {
                member.setAgentQueue(agentQueue);
            } else {
                member.setRobotQueue(agentQueue);
            }
            QueueMemberEntity updatedMember = queueMemberRestService.save(member);
            if (updatedMember == null) {
                throw new RuntimeException("Failed to update queue member");
            }
            // 更新工作组队列
            return updatedMember;
        }
        // 创建队列成员实体并保存到数据库
        QueueMemberEntity member = QueueMemberEntity.builder()
            .uid(uidUtils.getUid())
            .workgroupQueue(workgroupQueue)            
            .thread(threadEntity)
            .queueNumber(workgroupQueue.getNextNumber())
            .visitorEnqueueAt(ZonedDateTime.now())
            .orgUid(threadEntity.getOrgUid())
            .build();
        // 
        if (agent.getType().equals(ThreadTypeEnum.AGENT.name())) {
            member.setAgentQueue(agentQueue);
        } else {
            member.setRobotQueue(agentQueue);
        }
        // 
        QueueMemberEntity updatedMember = queueMemberRestService.save(member);
        if (updatedMember == null) {
            throw new RuntimeException("Failed to update queue member");
        }
        // 更新工作组队列
        return updatedMember;
    }

    @Transactional
    private QueueEntity getQueue(ThreadEntity threadEntity, String queueNickname) {
        String threadTopic = threadEntity.getTopic();
        String queueTopic = TopicUtils.getQueueTopicFromThreadTopic(threadTopic);
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        // 
        while (true) {
            try {
                return queueRestService.findByTopicAndDay(queueTopic, today)
                    .orElseGet(() -> {
                        QueueRequest request = QueueRequest.builder()
                            .nickname(queueNickname)
                            .type(threadEntity.getType())
                            .topic(queueTopic)
                            .day(today)
                            .status(QueueStatusEnum.ACTIVE.name())
                            .orgUid(threadEntity.getOrgUid())
                            .build();
                        return queueRestService.createQueue(request);
                    });
            } catch (Exception e) {
                // 如果发生任何异常，等待短暂时间后重试
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Queue operation interrupted", ie);
                }
            }
        }
    }

    // agent or robot
    @Transactional
    private QueueEntity getAgentOrRobotQueue(UserProtobuf user, String orgUid) {
        Assert.notNull(user, "User cannot be null");
        String queueTopic = TopicUtils.getQueueTopicFromUid(user.getUid());
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        
        while (true) {
            try {
                return queueRestService.findByTopicAndDay(queueTopic, today)
                    .orElseGet(() -> {
                        QueueRequest request = QueueRequest.builder()
                            .nickname(user.getNickname())
                            .type(user.getType()) 
                            .topic(queueTopic)
                            .day(today)
                            .status(QueueStatusEnum.ACTIVE.name())
                            .orgUid(orgUid)
                            .build();
                        return queueRestService.createQueue(request);
                    });
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

    // @Transactional
    // public QueueMemberEntity getQueueMember(ThreadEntity threadEntity, UserProtobuf agent, QueueEntity queue, QueueEntity workgroupQueue) {
    //     // 
    //     Optional<QueueMemberEntity> memberOptional = queueMemberRestService.findByThreadUid(threadEntity.getUid());
    //     if (memberOptional.isPresent()) {
    //         return memberOptional.get();
    //     }
    //     // 创建队列成员实体并保存到数据库
    //     QueueMemberEntity member = QueueMemberEntity.builder()
    //         .uid(uidUtils.getUid())
    //         // .queue(queue)
    //         .workgroupQueue(workgroupQueue)
    //         .thread(threadEntity)
    //         .queueNumber(queue.getNextNumber())
    //         .enqueueTime(ZonedDateTime.now())
    //         .orgUid(threadEntity.getOrgUid())
    //         .build();
    //     // 
    //     return queueMemberRestService.save(member);
    // }

    // private void updateQueueStats(QueueEntity queue) {
    //     // int waiting = queueMemberRepository.countByQueueUidAndStatus(
    //     //     queue.getUid(), QueueMemberStatusEnum.WAITING.name());
    //     // int serving = queueMemberRepository.countByQueueUidAndStatus(
    //     //     queue.getUid(), QueueMemberStatusEnum.SERVING.name());
    //     // int finished = queueMemberRepository.countByQueueUidAndEndStatusIsTrue(queue.getUid());
    //     // int avgWait = calculateAverageWaitTime(queue.getUid());
        
    //     // queue.updateStats(waiting, serving, finished, avgWait);
    //     // queueRepository.save(queue);
    // }

    // private Integer calculateAverageWaitTime(String queueUid) {
    //     Double avgWaitTime = queueMemberRepository.calculateAverageWaitTime(queueUid);
    //     return avgWaitTime != null ? avgWaitTime.intValue() : 0;
    // }
    
    // public List<String> getQueuedThreads(QueueStatusEnum status) {
    //     // return queueRepository.findByStatus(status.name())
    //     //     .stream()
    //     //     .map(QueueEntity::getThreadUid)
    //     //     .toList();
    //     return new ArrayList<>();
    // }
    
    // public int getQueuePosition(String threadTopic) {
    //     QueueMemberEntity member = queueMemberRepository.findByThreadUid(threadTopic)
    //         .orElseThrow(() -> new RuntimeException("Queue member not found"));
    //     return queueMemberRepository.countByQueueUidAndPriorityGreaterThan(
    //         member.getQueueUid(), member.getPriority());
    // }
    
    // @Transactional
    // public void updateStatus(String threadUid, QueueStatusEnum status) {
    //     // QueueEntity queueItem = queueRepository.findByThreadUid(threadUid)
    //     //     .orElseThrow(() -> new RuntimeException("Queue item not found"));
    //     // queueItem.setStatus(status.name());
    //     // // if (status == QueueStatusEnum.COMPLETED || 
    //     // //     status == QueueStatusEnum.CANCELLED ||
    //     // //     status == QueueStatusEnum.TIMEOUT) {
    //     // //     queueItem.setEndTime(ZonedDateTime.now());
    //     // // }
    //     // queueRepository.save(queueItem);
    // }

    
    // public int getEstimatedWaitTime(String threadUid) {
    //     QueueMemberEntity member = queueMemberRepository.findByThreadUid(threadUid)
    //         .orElseThrow(() -> new RuntimeException("Queue member not found"));
            
    //     // 1. 获取前面等待数量
    //     int queuePosition = queueMemberRepository.countByQueueUidAndPriorityGreaterThan(
    //         member.getQueueUid(), member.getPriority());
        
    //     // 2. 获取平均会话时长(10分钟)
    //     int avgThreadDuration = 10;
        
    //     // 3. 获取在线客服数量
    //     List<String> onlineAgents = agentService.getOnlineAgents();
    //     if (onlineAgents.isEmpty()) {
    //         return -1;
    //     }
        
    //     // 4. 计算预计等待时间
    //     return (queuePosition * avgThreadDuration) / onlineAgents.size();
    // }

    // @Transactional
    // public void checkQueueTimeout() {
    //     // 1. 获取所有等待中的成员
    //     // List<QueueMemberEntity> waitingMembers = 
    //     //     queueMemberRepository.findByStatus(QueueMemberStatusEnum.WAITING.name());
        
    //     // 2. 检查超时
    //     // ZonedDateTime now = ZonedDateTime.now();
    //     // for (QueueMemberEntity member : waitingMembers) {
    //     //     if (member.getEnqueueTime().plusMinutes(30).isBefore(now)) {
    //     //         member.updateStatus(QueueMemberStatusEnum.TIMEOUT.name(), null);
    //     //         queueMemberRepository.save(member);
                
    //     //         // 更新队列统计
    //     //         QueueEntity queue = queueRepository.findByUid(member.getQueue().getUid())
    //     //             .orElseThrow(() -> new RuntimeException("Queue not found"));
    //     //         updateQueueStats(queue);
                
    //     //         log.info("Thread {} queue timeout", member.getThreadUid());
    //     //     }
    //     // }
    // }

    
    // @Transactional
    // public void processQueue() {
    //     try {
    //         // 1. 获取等待中的成员(按优先级排序)
    //         List<QueueMemberEntity> waitingMembers = new ArrayList<>();
    //         // List<QueueMemberEntity> waitingMembers = 
    //         //     queueMemberRepository.findByStatusOrderByPriorityDesc(
    //         //         QueueMemberStatusEnum.WAITING.name());
    //         // if (waitingMembers.isEmpty()) {
    //         //     return;
    //         // }
            
    //         // 2. 获取可用客服
    //         List<AgentEntity> availableAgents = agentService.getAvailableAgents();
    //         if (availableAgents.isEmpty()) {
    //             return;
    //         }

    //         // 3. 依次处理排队成员
    //         for (QueueMemberEntity member : waitingMembers) {
    //             // ThreadEntity thread = threadRepository.findByUid(member.getThreadUid())
    //             //     .orElseThrow(() -> new RuntimeException("Thread not found"));
                    
    //             try {
    //                 // 分配客服
    //                 AgentEntity agent = AgentEntity.builder().build(); // assignmentService.selectAgent(availableAgents, thread);
    //                 // assignmentService.assignToAgent(thread, agent);
                    
    //                 // 更新排队状态
    //                 // member.updateStatus(QueueMemberStatusEnum.COMPLETED.name(), agent.getUid());
    //                 // queueMemberRepository.save(member);
                    
    //                 // 更新队列统计
    //                 QueueEntity queue = queueRepository.findByUid(member.getQueue().getUid())
    //                     .orElseThrow(() -> new RuntimeException("Queue not found"));
    //                 updateQueueStats(queue);
                    
    //                 // 更新可用客服列表
    //                 availableAgents.remove(agent);
    //                 if (availableAgents.isEmpty()) {
    //                     break;
    //                 }
    //             } catch (Exception e) {
    //                 log.error("Failed to process queue member", e);
    //             }
    //         }
    //     } catch (Exception e) {
    //         log.error("Failed to process queue", e);
    //     }
    // }

    
    // public QueueStats getQueueStats() {
    //     QueueStats stats = new QueueStats();
        
    //     // 统计各状态会话数
    //     stats.setTotalThreads((int) queueRepository.count());
    //     // stats.setWaitingThreads(queueRepository.countByStatus(QueueStatusEnum.WAITING));
    //     // stats.setProcessingThreads(queueRepository.countByStatus(QueueStatusEnum.PROCESSING));
        
    //     // 计算平均等待时间
    //     // Double avgWaitTime = queueRepository.calculateAverageWaitTime();
    //     // stats.setAvgWaitTime(avgWaitTime != null ? avgWaitTime.intValue() : 0);
        
    //     // 计算最长等待时间
    //     // Integer maxWaitTime = queueRepository.calculateMaxWaitTime();
    //     // stats.setMaxWaitTime(maxWaitTime != null ? maxWaitTime : 0);
        
    //     // 计算超时率和取消率
    //     // long timeoutCount = queueRepository.countByStatus(QueueStatusEnum.TIMEOUT);
    //     // long cancelCount = queueRepository.countByStatus(QueueStatusEnum.CANCELLED);
    //     // stats.setTimeoutRate(stats.getTotalThreads() > 0 ? (double) timeoutCount / stats.getTotalThreads() : 0);
    //     // stats.setCancelRate(stats.getTotalThreads() > 0 ? (double) cancelCount / stats.getTotalThreads() : 0);
        
    //     return stats;
    // }
}