/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 09:24:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-30 22:05:03
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserUtils;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.queue.notification.QueueNotificationService;
import com.bytedesk.service.utils.ServiceConvertUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class QueueMemberRestService extends BaseRestServiceWithExport<QueueMemberEntity, QueueMemberRequest, QueueMemberResponse, QueueMemberExcel> {

    private final QueueMemberRepository queueMemberRepository;
    private final ModelMapper modelMapper;
    private final UidUtils uidUtils;
    // private final EntityManager entityManager;
    private static final int IDLE_QUEUE_TIMEOUT_MINUTES = 5; // 超过5分钟未发首条消息视为过期
    private static final String AGENT_QUEUE_THREAD_CACHE = "agent_queue_thread_uid";
    private final ThreadRestService threadRestService;
    private static final List<String> ACTIVE_STATUSES = Collections.singletonList(QueueMemberStatusEnum.QUEUING.name());
    // private static final int MAX_ENQUEUE_RETRIES = 20;
    // private static final long COLLISION_BACKOFF_MILLIS = 25L;
    // private static final String QUEUE_MEMBER_TABLE_NAME = resolveQueueMemberTableName();
    // private static final String QUEUE_NUMBER_UNIQUE_CONSTRAINT = "uk7aviqofcxw7ae3fped747qrl7";
    // private final QueueAuditLogger queueAuditLogger;
    @Lazy
    private final QueueNotificationService queueNotificationService;
    
    
    public Optional<QueueMemberEntity> findActiveByThreadUid(String threadUid) {
        return queueMemberRepository.findFirstByThreadUidAndDeletedFalseAndStatusIn(threadUid, ACTIVE_STATUSES);
    }

    public Optional<QueueMemberEntity> findActiveByThreadUidForUpdate(String threadUid) {
        return queueMemberRepository.findFirstByThreadUidAndDeletedFalseAndStatusInForUpdate(threadUid, ACTIVE_STATUSES);
    }

    public Optional<QueueMemberEntity> findEarliestAgentQueueMember(String agentQueueUid) {
        return queueMemberRepository.findFirstByAgentQueue_UidAndDeletedFalseAndStatusOrderByQueueNumberAsc(agentQueueUid, QueueMemberStatusEnum.QUEUING.name());
    }

    public Optional<QueueMemberEntity> findEarliestAgentQueueMemberForUpdate(String agentQueueUid) {
        if (!StringUtils.hasText(agentQueueUid)) {
            return Optional.empty();
        }
        List<QueueMemberEntity> members = queueMemberRepository.findAgentQueueHeadForUpdate(
                agentQueueUid,
                QueueMemberStatusEnum.QUEUING.name(),
                PageRequest.of(0, 1));
        return members.isEmpty() ? Optional.empty() : Optional.of(members.get(0));
    }

    public Optional<QueueMemberEntity> findEarliestWorkgroupQueueMember(String workgroupQueueUid) {
        return queueMemberRepository.findFirstByWorkgroupQueue_UidAndDeletedFalseAndStatusOrderByQueueNumberAsc(workgroupQueueUid, QueueMemberStatusEnum.QUEUING.name());
    }

    public Optional<QueueMemberEntity> findEarliestRobotQueueMember(String robotQueueUid) {
        return queueMemberRepository.findFirstByRobotQueue_UidAndDeletedFalseAndStatusOrderByQueueNumberAsc(robotQueueUid, QueueMemberStatusEnum.QUEUING.name());
    }

    public Page<QueueMemberResponse> findAgentQueueMembers(String agentQueueUid, Pageable pageable) {
        if (!StringUtils.hasText(agentQueueUid)) {
            return Page.empty(pageable);
        }
        Page<QueueMemberEntity> page = queueMemberRepository
                .findByAgentQueue_UidAndDeletedFalseAndStatusOrderByQueueNumberAsc(agentQueueUid,
                        QueueMemberStatusEnum.QUEUING.name(), pageable);
        return page.map(ServiceConvertUtils::convertToQueueMemberResponse);
    }

    /**
     * Remove stale queue members before enqueuing new visitors so queue numbers remain dense.
     */
    @Transactional
    public int cleanupBeforeEnqueue() {
        return cleanupIdleQueueMembers();
    }

    @Cacheable(value = "queue_member", key = "#uid", unless = "#result == null")
    @Override
    public Optional<QueueMemberEntity> findByUid(String uid) {
        return queueMemberRepository.findByUid(uid);
    }

    @Cacheable(value = "queue_member", key = "#threadUid", unless = "#result == null")
    public Optional<QueueMemberEntity> findByThreadUid(String threadUid) {
        return queueMemberRepository.findByThreadUid(threadUid);
    }

    @CachePut(value = "queue_member", key = "#entity.uid")
    @Override
    protected QueueMemberEntity doSave(QueueMemberEntity entity) {
        return queueMemberRepository.save(entity);
    }
    

    @Override
    public QueueMemberEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            QueueMemberEntity entity) {
        log.error("处理乐观锁异常: {}", e.getMessage());      
        try {
            // 获取最新的实体
            Optional<QueueMemberEntity> latestEntityOpt = queueMemberRepository.findByUid(entity.getUid());
            if (latestEntityOpt.isPresent()) {
                QueueMemberEntity latestEntity = latestEntityOpt.get();
                
                // 根据业务需求合并变更
                // 保留当前实体中的重要计数值
                if (entity.getVisitorMessageCount() > latestEntity.getVisitorMessageCount()) {
                    latestEntity.setVisitorMessageCount(entity.getVisitorMessageCount());
                }
                
                if (entity.getAgentMessageCount() > latestEntity.getAgentMessageCount()) {
                    latestEntity.setAgentMessageCount(entity.getAgentMessageCount());
                }
                
                if (entity.getRobotMessageCount() > latestEntity.getRobotMessageCount()) {
                    latestEntity.setRobotMessageCount(entity.getRobotMessageCount());
                }
                
                if (entity.getSystemMessageCount() > latestEntity.getSystemMessageCount()) {
                    latestEntity.setSystemMessageCount(entity.getSystemMessageCount());
                }
                
                // 保持最新的时间戳
                if (entity.getVisitorLastMessageAt() != null && 
                    (latestEntity.getVisitorLastMessageAt() == null || 
                     entity.getVisitorLastMessageAt().isAfter(latestEntity.getVisitorLastMessageAt()))) {
                    latestEntity.setVisitorLastMessageAt(entity.getVisitorLastMessageAt());
                }
                
                if (entity.getAgentLastResponseAt() != null && 
                    (latestEntity.getAgentLastResponseAt() == null || 
                     entity.getAgentLastResponseAt().isAfter(latestEntity.getAgentLastResponseAt()))) {
                    latestEntity.setAgentLastResponseAt(entity.getAgentLastResponseAt());
                }
                
                // 保存合并后的实体
                return queueMemberRepository.save(latestEntity);
            } else {
                log.warn("无法找到UID为{}的QueueMember实体，将使用原始实体副本", entity.getUid());
                return null;
            }
        } catch (Exception ex) {
            log.error("在处理乐观锁异常时发生错误: {}", ex.getMessage(), ex);
            // 抛出运行时异常以确保事务一致性，避免静默回滚
            throw new RuntimeException("处理乐观锁异常失败: " + ex.getMessage(), ex);
        }
    }

    @Override
    public QueueMemberResponse create(QueueMemberRequest request) {
        QueueMemberEntity counter = modelMapper.map(request, QueueMemberEntity.class);
        counter.setUid(uidUtils.getUid());
        
        QueueMemberEntity savedQueueMember = save(counter);
        if (savedQueueMember == null) {
            throw new RuntimeException("save queue member failed");
        }
        return convertToResponse(savedQueueMember);
    }

    @Override
    public QueueMemberResponse update(QueueMemberRequest request) {
        Optional<QueueMemberEntity> optional = findByUid(request.getUid());
        if (!optional.isPresent()) {
            throw new RuntimeException("queue member not found");
        }
        QueueMemberEntity entity = optional.get();
        // 更新实体属性
        // entity.setQueueNumber(request.getQueueNumber());

        QueueMemberEntity savedCounter = save(entity);
        if (savedCounter == null) {
            throw new RuntimeException("save queue member failed");
        }
        return convertToResponse(savedCounter);
    }

    @CacheEvict(value = "queue_member", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        Optional<QueueMemberEntity> optional = findByUid(uid);
        if (!optional.isPresent()) {
            throw new RuntimeException("queue member not found");
        }
        QueueMemberEntity entity = optional.get();
        entity.setDeleted(true);
        save(entity);
    }

    @Override
    public void delete(QueueMemberRequest entity) {
        deleteByUid(entity.getUid());
    }
    public void deleteAll() {
        queueMemberRepository.deleteAll();
    }

    @Override
    public QueueMemberResponse convertToResponse(QueueMemberEntity entity) {
        return ServiceConvertUtils.convertToQueueMemberResponse(entity);
    }

    @Override
    public QueueMemberExcel convertToExcel(QueueMemberEntity entity) {
        QueueMemberResponse response = convertToResponse(entity);
        // QueueMemberExcel excel = new QueueMemberExcel();
        QueueMemberExcel excel = modelMapper.map(response, QueueMemberExcel.class);
        // 基本信息
        if (response.getQueue() != null) {
            excel.setQueueNickname(response.getQueue().getNickname());
        }
        
        // 访客信息
        if (response.getThread() != null) {
            excel.setVisitorNickname(response.getThread().getUser().getNickname());
            excel.setAgentNickname(response.getThread().getAgentProtobuf().getNickname());
            excel.setRobotNickname(response.getThread().getRobotProtobuf().getNickname());
            // excel.setWorkgroupName(response.getThread().getWorkgroup().getNickname());
            excel.setStatus(ThreadProcessStatusEnum.fromValue(response.getThread().getStatus()).toChineseDisplay());
            excel.setChannel(ChannelEnum.fromValue(response.getThread().getChannel()).toChineseDisplay());
        }
        
        // 人工客服相关
        if (response.getAgentAcceptType() != null) {
            excel.setAgentAcceptType(QueueMemberAcceptTypeEnum.fromValue(response.getAgentAcceptType()).toChineseDisplay());
        } else {
            excel.setAgentAcceptType(null);
        }
        excel.setAgentOffline(ServiceConvertUtils.booleanToString(response.getAgentOffline()));
        
        // 机器人相关
        if (response.getRobotAcceptType() != null) {
            excel.setRobotAcceptType(QueueMemberAcceptTypeEnum.fromValue(response.getRobotAcceptType()).toChineseDisplay());
        } else {
            excel.setRobotAcceptType(null);
        }
        excel.setRobotToAgent(ServiceConvertUtils.booleanToString(response.getRobotToAgent()));
        
        // 消息统计
        // excel.setVisitorMessageCount(response.getVisitorMessageCount());
        // excel.setSystemMessageCount(response.getSystemMessageCount());
        
        // 评价与服务质量
        excel.setRated(ServiceConvertUtils.booleanToString(response.getRated()));
        excel.setRateScore(response.getRateScore());
        // excel.setRateLevel(response.getRateLevel());
        excel.setResolved(ServiceConvertUtils.booleanToString(response.getResolved()));
        // excel.setQualityChecked(booleanToString(response.getQualityChecked()));
        // excel.setQualityCheckResult(response.getQualityCheckResult());
        
        // 留言与小结
        excel.setMessageLeave(ServiceConvertUtils.booleanToString(response.getMessageLeave()));
        // excel.setLeaveMsg(booleanToString(response.getLeaveMsg()));
        // excel.setLeaveMsgAt(formatZonedDateTime(response.getLeaveMsgAt()));
        excel.setSummarized(ServiceConvertUtils.booleanToString(response.getSummarized()));
        
        // 交互状态
        // excel.setTransferStatus(response.getTransferStatus());
        // excel.setInviteStatus(response.getInviteStatus());
        // excel.setIntentionType(response.getIntentionType());
        // excel.setEmotionType(response.getEmotionType());
        
        return excel;
    }

    @Override
    protected Specification<QueueMemberEntity> createSpecification(QueueMemberRequest request) {
        return QueueMemberSpecification.search(request, authService);
    }

    @Override
    protected Page<QueueMemberEntity> executePageQuery(Specification<QueueMemberEntity> spec, Pageable pageable) {
        return queueMemberRepository.findAll(spec, pageable);
    }

    @Cacheable(value = AGENT_QUEUE_THREAD_CACHE, key = "#agent.uid", unless = "#result == null")
    public String ensureAgentQueueThreadUid(AgentEntity agent) {
        ThreadResponse response = createAgentQueueThread(agent);
        return response != null ? response.getUid() : null;
    }

    /**
     * 访客主动退出排队：标记离开时间并软删除队列成员记录
     */
    public void visitorExitQueue(String threadUid) {
        Optional<QueueMemberEntity> optional = findByThreadUid(threadUid);
        if (!optional.isPresent()) {
            return;
        }
        QueueMemberEntity entity = optional.get();
        entity.setVisitorLeavedAt(BdDateUtils.now());
        entity.setDeleted(true); // 不要删除，仅修改status状态
        entity.setStatus(QueueMemberStatusEnum.CANCELLED.name());
        QueueMemberEntity saved = save(entity);
        queueNotificationService.publishQueueLeaveNotice(saved);
    }

    /**
     * 扫描超时(未发送首条消息)的排队成员并标记删除
     */
    public int cleanupIdleQueueMembers() {
        java.time.ZonedDateTime threshold = BdDateUtils.now().minusMinutes(IDLE_QUEUE_TIMEOUT_MINUTES);
        java.util.List<QueueMemberEntity> idleList = queueMemberRepository.findIdleBefore(threshold);
        int removed = 0;
        for (QueueMemberEntity qm : idleList) {
            // 只处理仍处于排队状态的线程
            if (qm.getThread() != null && qm.getThread().isQueuing()
                    && QueueMemberStatusEnum.QUEUING.name().equals(qm.getStatus())) {
                // qm.setDeleted(true); // 不要删除，仅修改status状态
                qm.setVisitorLeavedAt(BdDateUtils.now());
                qm.setStatus(QueueMemberStatusEnum.TIMEOUT.name());
                QueueMemberEntity saved = save(qm);
                queueNotificationService.publishQueueTimeoutNotice(saved);
                removed++;
            }
        }
        return removed;
    }


    /** 客服排队会话：org/queue/{agent_uid} */
    public ThreadResponse createAgentQueueThread(AgentEntity agent) {
        //
        String topic = TopicUtils.getOrgQueueTopic(agent.getUid());
        //
        Optional<ThreadEntity> threadOptional = threadRestService.findFirstByTopic(topic);
        if (threadOptional.isPresent()) {
            return threadRestService.convertToResponse(threadOptional.get());
        }
        // 排队助手-用户信息，头像、昵称等
        UserProtobuf user = UserUtils.getQueueAssistantUser();
        //
        ThreadEntity assistantThread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .type(ThreadTypeEnum.ASSISTANT.name())
                .topic(topic)
                .hide(true)
                .status(ThreadProcessStatusEnum.NEW.name())
                .channel(ChannelEnum.SYSTEM.name())
                .level(LevelEnum.AGENT.name())
                .user(user.toJson())
                .userUid(agent.getUid())
                .owner(agent.getMember().getUser())
                .build();

        ThreadEntity updateThread = threadRestService.save(assistantThread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }

        return threadRestService.convertToResponse(updateThread);
    }

}
