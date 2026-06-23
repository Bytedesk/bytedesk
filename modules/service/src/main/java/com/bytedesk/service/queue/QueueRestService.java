/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:03:55
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-07 17:49:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Description;
import com.bytedesk.core.base.BaseRestServiceWithExport;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.ThreadRequest;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.thread.ThreadQueueMeta;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadRepository;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.message.MessageRepository;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRestService;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRestService;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRepository;
import org.springframework.util.StringUtils;
import org.springframework.data.domain.PageImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
@Description("Queue Management Service - Customer service queue and routing management service")
public class QueueRestService extends BaseRestServiceWithExport<QueueEntity, QueueRequest, QueueResponse, QueueExcel> {

    private final QueueRepository queueRepository;

    private final ModelMapper modelMapper;

    private final UidUtils uidUtils;

    private final AuthService authService;

    private final ThreadRestService threadRestService;

    private final AgentRestService agentRestService;

    private final WorkgroupRepository workgroupRepository;

    private final QueueMemberRestService queueMemberRestService;

    private final MessageRepository messageRepository;

    private final MessageRestService messageRestService;

    private final ThreadRepository threadRepository;

    private final QueueService queueService;

    @Override
    protected Specification<QueueEntity> createSpecification(QueueRequest request) {
        return QueueSpecification.search(request, authService);
    }

    @Override
    protected Page<QueueEntity> executePageQuery(Specification<QueueEntity> spec, Pageable pageable) {
        return queueRepository.findAll(spec, pageable);
    }

    @Override
    public Page<QueueEntity> queryByOrgEntity(QueueRequest request) {
        Pageable pageable = request.getPageable();
        Specification<QueueEntity> specification = QueueSpecification.search(request, authService);
        return queueRepository.findAll(specification, pageable);
    }

    @Override
    public Page<QueueResponse> queryByOrg(QueueRequest request) {
        Page<QueueEntity> page = queryByOrgEntity(request);
        return page.map(this::convertToResponse);
    }

    @Override
    public Page<QueueResponse> queryByUser(QueueRequest request) {
        UserEntity user = authService.getUser();
        // set user uid
        request.setUserUid(user.getUid());
        return queryByOrg(request);
    }

    public Page<ThreadResponse> queryQueuing(ThreadRequest request) {
        UserEntity user = authService.getUser();
        // 设置查询条件：状态为排队中
        request.setStatus(ThreadProcessStatusEnum.QUEUING.name());

        // topicList 在反序列化/手动构造时可能被置空，做一次兜底
        if (request.getTopicList() == null) {
            request.setTopicList(new ArrayList<>());
        }
        
        // 通过user.uid查询相应的agent
        String orgUid = user != null ? user.getOrgUid() : null;
        if (!StringUtils.hasText(orgUid)) {
            orgUid = request.getOrgUid();
        }

        String userUid = user != null ? user.getUid() : request.getUserUid();
        Optional<AgentEntity> agentOptional = (StringUtils.hasText(orgUid) && StringUtils.hasText(userUid))
                ? agentRestService.findByUserUidAndOrgUid(userUid, orgUid)
                : Optional.empty();
        if (agentOptional.isPresent()) {
            AgentEntity agent = agentOptional.get();
            // 将agent.uid添加到topicList中
            request.getTopicList().add(agent.getUid());
            
            // 通过agent.uid查询相应的workgroups
            List<WorkgroupEntity> workgroups = workgroupRepository.findByAgentUid(agent.getUid());
            for (WorkgroupEntity workgroup : workgroups) {
                // 将workgroup.uid添加到topicList中
                request.getTopicList().add(workgroup.getUid());
            }
        }
        log.debug("查询排队中会话，主题列表: {}", request.getTopicList());
        
        // 使用 threadRestService 查询排队中的会话
        Page<ThreadResponse> page = threadRestService.queryByOrg(request);

        List<String> threadUids = page.getContent().stream()
                .map(ThreadResponse::getUid)
                .filter(StringUtils::hasText)
                .toList();

        Map<String, QueueMemberEntity> queueMemberByThreadUid = queueMemberRestService.findByThreadUids(threadUids)
                .stream()
                .filter(qm -> qm.getThread() != null && StringUtils.hasText(qm.getThread().getUid()))
                .collect(Collectors.toMap(qm -> qm.getThread().getUid(), qm -> qm, (a, b) -> a));

        long now = System.currentTimeMillis();

        return page.map(thread -> {
            if (thread == null || !StringUtils.hasText(thread.getUid())) {
                return thread;
            }

            QueueMemberEntity qm = queueMemberByThreadUid.get(thread.getUid());
            if (qm == null) {
                return thread;
            }

            Long enqueuedAt = null;
            if (qm.getVisitorEnqueueAt() != null) {
                enqueuedAt = qm.getVisitorEnqueueAt().toInstant().toEpochMilli();
            }

            Long waitingMs = null;
            if (enqueuedAt != null) {
                waitingMs = Math.max(0, now - enqueuedAt);
            }

            ThreadQueueMeta meta = ThreadQueueMeta.builder()
                    .queueMemberUid(qm.getUid())
                    .position(qm.getQueueNumber())
                    .serverTimestamp(now)
                    .enqueuedAt(enqueuedAt)
                    .waitingMs(waitingMs)
                    .build();

            thread.setQueueMeta(meta);
            return thread;
        });
    }

    /**
     * 查询当前客服待回复会话（含未回复访客消息）。
     *
     * 设计：
     * - 先从 message 表按 thread 聚合出“最早未回复访客消息时间”，并分页取 thread uid。
     * - 再批量加载 thread 实体并转为 ThreadResponse。
     * - 将 waitingMs 写入 ThreadResponse.queueMeta（与排队中 query/queuing 一致的承载方式）。
     */
    public Page<ThreadResponse> queryUnreplied(ThreadRequest request) {
        return queryUnreplied(request, true);
    }

    public int markUnrepliedThreadAsReplied(ThreadRequest request) {
        Optional<AgentEntity> agentOptional = resolveCurrentAgent(request);
        if (agentOptional.isEmpty()) {
            return 0;
        }

        Optional<ThreadEntity> threadOptional = resolveRequestedThread(request);
        if (threadOptional.isEmpty()) {
            return 0;
        }

        ThreadEntity thread = threadOptional.get();
        if (Boolean.TRUE.equals(thread.isDeleted()) || !isThreadAssignedToAgent(thread, agentOptional.get().getUid())) {
            return 0;
        }

        return markThreadUnrepliedMessagesAsReplied(thread, ZonedDateTime.now(), agentOptional.get().getUid());
    }

    public int markAllUnrepliedThreadsAsReplied(ThreadRequest request) {
        Optional<AgentEntity> agentOptional = resolveCurrentAgent(request);
        if (agentOptional.isEmpty()) {
            return 0;
        }

        String agentUid = agentOptional.get().getUid();
        String agentUidPattern = "%\"uid\":\"" + agentUid + "\"%";
        int totalUpdated = 0;

        while (true) {
            List<Object[]> rows = messageRepository.pageUnrepliedVisitorThreadUidsByAgentUid(agentUidPattern, 100, 0);
            if (rows == null || rows.isEmpty()) {
                break;
            }

            List<String> threadUids = rows.stream()
                    .filter(row -> row != null && row.length > 0 && row[0] != null)
                    .map(row -> String.valueOf(row[0]))
                    .filter(StringUtils::hasText)
                    .distinct()
                    .toList();
            if (threadUids.isEmpty()) {
                break;
            }

            Map<String, ThreadEntity> threadByUid = threadRepository.findByUidInAndDeletedFalse(threadUids)
                    .stream()
                    .filter(thread -> thread != null && StringUtils.hasText(thread.getUid()))
                    .collect(Collectors.toMap(ThreadEntity::getUid, thread -> thread, (a, b) -> a));

            int batchUpdated = 0;
            ZonedDateTime repliedAt = ZonedDateTime.now();
            for (String threadUid : threadUids) {
                ThreadEntity thread = threadByUid.get(threadUid);
                if (thread == null) {
                    continue;
                }
                batchUpdated += markThreadUnrepliedMessagesAsReplied(thread, repliedAt, agentUid);
            }

            totalUpdated += batchUpdated;
            if (batchUpdated <= 0) {
                break;
            }
        }

        return totalUpdated;
    }

    private Page<ThreadResponse> queryUnreplied(ThreadRequest request, boolean allowSelfHeal) {
        Optional<AgentEntity> agentOptional = resolveCurrentAgent(request);
        if (agentOptional.isEmpty()) {
            return Page.empty();
        }

        AgentEntity agent = agentOptional.get();
        String agentUid = agent.getUid();
        String agentUidPattern = "%\"uid\":\"" + agentUid + "\"%";

        Pageable pageable = request.getPageable();
        int pageNumber = (pageable == null) ? 0 : pageable.getPageNumber();
        int pageSize = (pageable == null) ? 20 : pageable.getPageSize();
        int offset = Math.max(0, pageNumber) * Math.max(1, pageSize);

        long total = messageRepository.countUnrepliedVisitorThreadsByAgentUid(agentUidPattern);
        if (total <= 0) {
            return Page.empty(pageable);
        }

        List<Object[]> rows = messageRepository.pageUnrepliedVisitorThreadUidsByAgentUid(agentUidPattern, pageSize, offset);
        if (rows == null || rows.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        List<String> threadUids = new ArrayList<>(rows.size());
        Map<String, Long> firstUnrepliedAtByThreadUid = new HashMap<>(rows.size());
        for (Object[] row : rows) {
            if (row == null || row.length < 2) {
                continue;
            }
            String threadUid = (row[0] == null) ? null : String.valueOf(row[0]);
            if (!StringUtils.hasText(threadUid)) {
                continue;
            }

            Long firstAt = null;
            Object tsObj = row[1];
            if (tsObj instanceof Timestamp ts) {
                firstAt = ts.getTime();
            } else if (tsObj instanceof java.util.Date d) {
                firstAt = d.getTime();
            }

            threadUids.add(threadUid);
            if (firstAt != null) {
                firstUnrepliedAtByThreadUid.put(threadUid, firstAt);
            }
        }

        if (threadUids.isEmpty()) {
            return new PageImpl<>(List.of(), pageable, total);
        }

        List<ThreadEntity> threadEntities = threadRepository.findByUidInAndDeletedFalse(threadUids);
        Map<String, ThreadEntity> threadByUid = new HashMap<>();
        for (ThreadEntity t : threadEntities) {
            if (t != null && StringUtils.hasText(t.getUid())) {
                threadByUid.put(t.getUid(), t);
            }
        }

        int healedCount = allowSelfHeal ? healStaleUnrepliedThreads(firstUnrepliedAtByThreadUid, threadByUid) : 0;
        if (healedCount > 0) {
            log.info("queryUnreplied rerun after self-heal: agentUid={}, pageNumber={}, pageSize={}, healedCount={}",
                    agentUid, pageNumber, pageSize, healedCount);
            return queryUnreplied(request, false);
        }

        Map<String, QueueMemberEntity> queueMemberByThreadUid = queueMemberRestService.findByThreadUids(threadUids)
                .stream()
                .filter(qm -> qm.getThread() != null && StringUtils.hasText(qm.getThread().getUid()))
                .collect(Collectors.toMap(qm -> qm.getThread().getUid(), qm -> qm, (a, b) -> a));

        long now = System.currentTimeMillis();
        List<ThreadResponse> responses = new ArrayList<>(threadUids.size());
        for (String threadUid : threadUids) {
            ThreadEntity entity = threadByUid.get(threadUid);
            if (entity == null) {
                continue;
            }

            ThreadResponse resp = threadRestService.convertToResponse(entity);

            Long firstUnrepliedAt = resolveFirstUnrepliedAt(threadUid, firstUnrepliedAtByThreadUid.get(threadUid));
            Long waitingMs = (firstUnrepliedAt == null) ? null : Math.max(0, now - firstUnrepliedAt);

            QueueMemberEntity qm = queueMemberByThreadUid.get(threadUid);
            String queueMemberUid = (qm == null) ? null : qm.getUid();

            ThreadQueueMeta meta = ThreadQueueMeta.builder()
                    .queueMemberUid(queueMemberUid)
                    .serverTimestamp(now)
                    // 复用字段：这里 enqueuedAt 表示“最早未回复访客消息时间”
                    .enqueuedAt(firstUnrepliedAt)
                    .waitingMs(waitingMs)
                    .build();
            resp.setQueueMeta(meta);

            responses.add(resp);
        }

        return new PageImpl<>(responses, pageable, total);
    }

    private Optional<AgentEntity> resolveCurrentAgent(ThreadRequest request) {
        UserEntity user = authService.getUser();
        String orgUid = user != null ? user.getOrgUid() : null;
        if (!StringUtils.hasText(orgUid)) {
            orgUid = request.getOrgUid();
        }
        String userUid = user != null ? user.getUid() : request.getUserUid();
        return (StringUtils.hasText(orgUid) && StringUtils.hasText(userUid))
                ? agentRestService.findByUserUidAndOrgUid(userUid, orgUid)
                : Optional.empty();
    }

    private Optional<ThreadEntity> resolveRequestedThread(ThreadRequest request) {
        if (request == null) {
            return Optional.empty();
        }
        if (StringUtils.hasText(request.getUid())) {
            return threadRepository.findByUid(request.getUid());
        }
        if (StringUtils.hasText(request.getTopic())) {
            return threadRepository.findFirstByTopicAndDeletedOrderByCreatedAtDesc(request.getTopic(), false);
        }
        return Optional.empty();
    }

    private boolean isThreadAssignedToAgent(ThreadEntity thread, String agentUid) {
        if (thread == null || !StringUtils.hasText(agentUid) || !StringUtils.hasText(thread.getAgent())) {
            return false;
        }
        try {
            return agentUid.equals(thread.getAgentProtobuf().getUid());
        } catch (Exception exception) {
            log.warn("failed to parse thread agent protobuf when marking unreplied: threadUid={}", thread.getUid(), exception);
            return false;
        }
    }

    private int markThreadUnrepliedMessagesAsReplied(ThreadEntity thread, ZonedDateTime repliedAt, String repliedByUid) {
        if (thread == null || repliedAt == null || !StringUtils.hasText(repliedByUid)) {
            return 0;
        }

        ZoneId zoneId = repliedAt.getZone() != null ? repliedAt.getZone() : ZoneId.systemDefault();
        ZonedDateTime windowStart = ZonedDateTime.ofInstant(Instant.EPOCH, zoneId);
        List<MessageEntity> messages = StringUtils.hasText(thread.getTopic())
                ? messageRestService.findByThreadTopicBetweenCreatedAt(thread.getTopic(), windowStart, repliedAt)
                : messageRestService.findByThreadUid(thread.getUid());
        if (messages == null || messages.isEmpty()) {
            return 0;
        }

        int updated = 0;
        for (MessageEntity message : messages) {
            if (!isVisitorMessageForUnreplied(message)) {
                continue;
            }
            if (Boolean.TRUE.equals(message.getAgentReplied())) {
                continue;
            }
            if (message.getCreatedAt() == null || message.getCreatedAt().isAfter(repliedAt)) {
                continue;
            }

            message.setAgentReplied(true);
            message.setAgentRepliedAt(repliedAt);
            message.setAgentRepliedByUid(repliedByUid);
            messageRestService.save(message);
            updated++;
        }

        if (updated > 0) {
            log.info("marked unreplied thread as replied: threadUid={}, topic={}, count={}, repliedByUid={}",
                    thread.getUid(), thread.getTopic(), updated, repliedByUid);
        }
        return updated;
    }

    private Long resolveFirstUnrepliedAt(String threadUid, Long fallbackFirstUnrepliedAt) {
        if (!StringUtils.hasText(threadUid)) {
            return fallbackFirstUnrepliedAt;
        }

        List<MessageEntity> messages = messageRestService.findByThreadUid(threadUid);
        if (messages == null || messages.isEmpty()) {
            return fallbackFirstUnrepliedAt;
        }

        for (MessageEntity message : messages) {
            if (!isVisitorMessageForUnreplied(message)) {
                continue;
            }
            if (Boolean.TRUE.equals(message.getAgentReplied())) {
                continue;
            }
            if (message.getCreatedAt() == null) {
                continue;
            }
            return message.getCreatedAt().toInstant().toEpochMilli();
        }

        return fallbackFirstUnrepliedAt;
    }

    private int healStaleUnrepliedThreads(Map<String, Long> firstUnrepliedAtByThreadUid,
            Map<String, ThreadEntity> threadByUid) {
        if (firstUnrepliedAtByThreadUid == null || firstUnrepliedAtByThreadUid.isEmpty() || threadByUid == null) {
            return 0;
        }

        int healedCount = 0;
        for (Map.Entry<String, Long> entry : firstUnrepliedAtByThreadUid.entrySet()) {
            String threadUid = entry.getKey();
            Long firstUnrepliedAt = entry.getValue();
            if (!StringUtils.hasText(threadUid) || firstUnrepliedAt == null) {
                continue;
            }

            ThreadEntity thread = threadByUid.get(threadUid);
            if (thread == null) {
                continue;
            }

            String threadTopic = thread.getTopic();

            Optional<MessageEntity> latestOptional = StringUtils.hasText(threadTopic)
                    ? messageRestService.findLatestByThreadTopic(threadTopic)
                    : messageRestService.findLatestByThreadUid(threadUid);
            if (latestOptional.isEmpty()) {
                continue;
            }

            MessageEntity latestMessage = latestOptional.get();
            if (!isAgentOrRobotReplyMessage(thread, latestMessage) || latestMessage.getCreatedAt() == null) {
                continue;
            }

            ZonedDateTime repliedAt = latestMessage.getCreatedAt();
            long repliedAtMillis = repliedAt.toInstant().toEpochMilli();
            if (repliedAtMillis < firstUnrepliedAt) {
                continue;
            }

            ZoneId zoneId = repliedAt.getZone() != null ? repliedAt.getZone() : ZoneId.systemDefault();
            ZonedDateTime windowStart = ZonedDateTime.ofInstant(Instant.ofEpochMilli(firstUnrepliedAt), zoneId);
            String agentUid = latestMessage.getUserProtobuf() != null ? latestMessage.getUserProtobuf().getUid() : null;

            List<MessageEntity> messages = StringUtils.hasText(threadTopic)
                    ? messageRestService.findByThreadTopicBetweenCreatedAt(threadTopic, windowStart, repliedAt)
                    : messageRestService.findByThreadUidBetweenCreatedAt(threadUid, windowStart, repliedAt);
            if (messages == null || messages.isEmpty()) {
                continue;
            }

            int updated = 0;
            for (MessageEntity message : messages) {
                if (message == null || !message.isFromVisitor() || message.getCreatedAt() == null) {
                    continue;
                }
                if (message.getCreatedAt().isBefore(windowStart) || message.getCreatedAt().isAfter(repliedAt)) {
                    continue;
                }
                if (Boolean.TRUE.equals(message.getAgentReplied())) {
                    continue;
                }

                message.setAgentReplied(true);
                message.setAgentRepliedAt(repliedAt);
                message.setAgentRepliedByUid(agentUid);
                messageRestService.save(message);
                updated++;
            }

            if (updated > 0) {
                healedCount += updated;
                log.info("queryUnreplied self-healed stale unreplied thread: threadUid={}, topic={}, count={}, agentUid={}",
                        threadUid, threadTopic, updated, agentUid);
            }
        }

        return healedCount;
    }

    private boolean isHumanReplyMessage(ThreadEntity thread, MessageEntity message) {
        if (thread == null || message == null) {
            return false;
        }
        if (!Boolean.TRUE.equals(thread.isCustomerService())) {
            return false;
        }
        if (message.isFromVisitor() || message.isFromRobot() || message.isFromSystem()) {
            return false;
        }
        return true;
    }

    private boolean isAgentOrRobotReplyMessage(ThreadEntity thread, MessageEntity message) {
        if (thread == null || message == null) {
            return false;
        }
        if (!Boolean.TRUE.equals(thread.isCustomerService())) {
            return false;
        }
        if (message.isFromVisitor() || message.isFromSystem()) {
            return false;
        }
        return message.isFromRobot() || isHumanReplyMessage(thread, message);
    }

    private boolean isVisitorMessageForUnreplied(MessageEntity message) {
        if (message == null || !message.isFromVisitor()) {
            return false;
        }

        MessageTypeEnum messageType = MessageTypeEnum.fromValue(message.getType());
        return messageType != MessageTypeEnum.SYSTEM && messageType != MessageTypeEnum.NOTICE;
    }

    @Cacheable(value = "queue", key = "#uid", unless = "#result==null")
    @Override
    public Optional<QueueEntity> findByUid(String uid) {
        return queueRepository.findByUid(uid);
    }

    public Optional<QueueEntity> findByTopicAndDay(String topic, String day) {
        return queueRepository.findFirstByTopicAndDayAndDeletedFalseOrderByCreatedAtDesc(topic, day);
    }

    public QueueEntity createQueue(QueueRequest request) {
        QueueEntity queue = modelMapper.map(request, QueueEntity.class);
        queue.setUid(uidUtils.getUid());
        try {
            return queueRepository.save(queue);
        } catch (DataIntegrityViolationException e) {
            // 如果在保存时发生唯一约束冲突，说明其他线程已创建
            // 重新查询获取已存在的记录
            return queueRepository.findByTopicAndDayAndDeletedFalse(request.getTopic(), request.getDay())
                    .orElseThrow(() -> new RuntimeException("Queue creation failed"));
        }
    }

    @Override
    public QueueResponse create(QueueRequest request) {
        QueueEntity entity = modelMapper.map(request, QueueEntity.class);
        entity.setUid(uidUtils.getUid());
        //
        QueueEntity savedQueue = save(entity);
        if (savedQueue == null) {
            throw new RuntimeException("Create queue failed");
        }
        return convertToResponse(savedQueue);
    }

    @Override
    public QueueResponse update(QueueRequest request) {
        Optional<QueueEntity> queueOptional = findByUid(request.getUid());
        if (queueOptional.isPresent()) {
            QueueEntity entity = queueOptional.get();
            // modelMapper.map(request, entity);
            entity.setUid(request.getUid());
            //
            return convertToResponse(save(entity));
        }
        return create(request);
    }

    @CachePut(value = "queue", key = "#entity.uid")
    @Override
    protected QueueEntity doSave(QueueEntity entity) {
        return queueRepository.save(entity);
    }

    @CacheEvict(value = "queue", key = "#uid")
    @Override
    public void deleteByUid(String uid) {
        Optional<QueueEntity> optional = findByUid(uid);
        if (optional.isPresent()) {
            // delete(optional.get());
            optional.get().setDeleted(true);
            save(optional.get());
        }
    }

    @Override
    public void delete(QueueRequest entity) {
        deleteByUid(entity.getUid());
    }

    @Override
    public QueueEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, QueueEntity entity) {
        // 乐观锁处理实现
        try {
            Optional<QueueEntity> latest = queueRepository.findByUid(entity.getUid());
            if (latest.isPresent()) {
                QueueEntity latestEntity = latest.get();
                // 合并需要保留的数据
                // 这里可以根据业务需求合并实体
                return queueRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            throw new RuntimeException("无法处理乐观锁冲突: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public QueueResponse convertToResponse(QueueEntity entity) {
        return ServiceConvertUtils.convertToQueueResponse(entity);
    }

    public Optional<QueueEntity> findLatestByQueueTopicAndDay(String queueTopic, String day) {
        return queueRepository.findFirstByTopicAndDayAndDeletedFalseOrderByCreatedAtDesc(queueTopic, day);
    }
    
    @Override
    public QueueExcel convertToExcel(QueueEntity entity) {
        QueueExcel queueExcel = modelMapper.map(entity, QueueExcel.class);
        queueExcel.setUid(entity.getUid());
        
        // 使用ThreadTypeEnum的静态方法将类型转换为中文名称
        String typeStr = entity.getType();
        queueExcel.setType(ThreadTypeEnum.getChineseNameByString(typeStr));
        
        // 使用QueueStatusEnum的静态方法将状态转换为中文名称
        // String statusStr = entity.getStatus();
        // queueExcel.setStatus(QueueStatusEnum.getChineseNameByString(statusStr));
        
        return queueExcel;
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
    public QueueService.AgentQueuingCount getAgentTotalQueuingCount(String agentUid) {
        return queueService.getAgentTotalQueuingCount(agentUid);
    }

    /**
     * 获取客服的完整队列统计信息
     * 包括今日服务人数、排队人数、接待人数、留言数、转人工数等
     * 
     * @param agentUid 客服UID
     * @return 完整队列统计响应
     */
    public QueueAgentStatsResponse getAgentQueueStats(String agentUid) {
        return queueService.getAgentQueueStats(agentUid);
    }
}
