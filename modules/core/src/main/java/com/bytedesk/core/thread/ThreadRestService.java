/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-16 15:46:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
// import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestService;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.exception.CommonI18nExceptions;
import com.bytedesk.core.exception.ResourceI18nExceptions;
import com.bytedesk.core.message.enums.MessageTypeEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.message.MessageEntity;
import com.bytedesk.core.message.MessageRestService;
import com.bytedesk.core.message.enums.MessageStatusEnum;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserUtils;
import com.bytedesk.core.thread.enums.ThreadCloseTypeEnum;
import com.bytedesk.core.thread.enums.ThreadProcessStatusEnum;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.thread.event.ThreadCloseEvent;
import com.bytedesk.core.thread.event.ThreadRemoveTopicEvent;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.topic_subscription.TopicSubscriptionRequest;
import com.bytedesk.core.topic_subscription.TopicSubscriptionRestService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ThreadRestService
        extends BaseRestService<ThreadEntity, ThreadRequest, ThreadResponse> {

    private final AuthService authService;

    private final ModelMapper modelMapper;

    private final ThreadRepository threadRepository;

    private final UidUtils uidUtils;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    private final TopicSubscriptionRestService topicSubscriptionRestService;

    private final ActiveThreadCacheService activeThreadCacheService;

    private final MessageRestService messageRestService;

    // @Cacheable(value = "thread", key = "#uid", unless = "#result == null")
    public Optional<ThreadEntity> findByUid(String uid) {
        return threadRepository.findByUid(uid);
    }

    /**
     * 在独立只读事务中加载会话，返回游离（detached）实体。
     *
     * <p>供工单创建等“只读绑定”场景使用：普通 findByUid 会把会话实体挂进
     * 调用方事务的持久化上下文，一旦该会话行被并发更新（version 变化），
     * 调用方事务提交/flush 时就会抛乐观锁异常并整体回滚
     * （如会话关闭自动建单：Unexpected row count, expected 1 but was 0）。
     * 独立只读事务读出的游离实体不参与调用方事务的脏检查，天然隔离该风险。
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Optional<ThreadEntity> findByUidReadOnly(String uid) {
        return threadRepository.findByUid(uid);
    }

    /**
     * 事务提交后再发布 ThreadCloseEvent。
     *
     * <p>BytedeskEventPublisher 是 @Async 的：若在事务内直接发布，监听器
     * （自动建单、队列清理、关闭消息下发、会话小结等）会立即在其他线程开跑，
     * 与发布者事务并发读写同一会话行，极易触发乐观锁冲突；监听器在提交前
     * 也可能读到未落库的中间状态。提交后发布保证监听器看到的是终态数据；
     * 无事务上下文时（如直接在非事务方法中调用）保持原行为直接发布。
     * 与 TicketEntityListener#publishAfterCommit 同一模式。
     */
    private void publishThreadCloseEventAfterCommit(ThreadEntity thread) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    bytedeskEventPublisher.publishEvent(new ThreadCloseEvent(ThreadRestService.this, thread));
                }
            });
        } else {
            bytedeskEventPublisher.publishEvent(new ThreadCloseEvent(this, thread));
        }
    }

    public Boolean existsByUid(String uid) {
        return threadRepository.existsByUid(uid);
    }

    // @Cacheable(value = "thread", key = "#topic + '-' + #user.uid", unless =
    // "#result == null")
    public Optional<ThreadEntity> findFirstByTopicAndOwner(String topic, UserEntity user) {
        return threadRepository.findFirstByTopicAndOwnerAndDeletedOrderByUpdatedAtDesc(topic, user, false);
    }

    // 群聊同一个topic多条会话：IncorrectResultSizeDataAccessException: Query did not return a
    // unique result: 4 results were returned
    // @Cacheable(value = "threads", key = "#topic", unless = "#result == null")
    public List<ThreadEntity> findListByTopic(String topic) {
        return threadRepository.findByTopicAndDeletedOrderByCreatedAtDesc(topic, false);
    }

    // @Cacheable(value = "thread", key = "#topic", unless = "#result == null")
    public Optional<ThreadEntity> findFirstByTopic(String topic) {
        return threadRepository.findFirstByTopicAndDeletedOrderByCreatedAtDesc(topic, false);
    }

    // 找到某个访客当前对应某工作组未关闭会话
    // @Cacheable(value = "thread", key = "#topic", unless = "#result == null")
    public Optional<ThreadEntity> findFirstByTopicNotClosed(String topic) {
        List<String> states = Arrays.asList(new String[] { ThreadProcessStatusEnum.CLOSED.name(),
                ThreadProcessStatusEnum.TIMEOUT.name() });
        return threadRepository.findTopicAndStatusesNotInAndDeleted(topic, states, false);
    }

    // 根据topic前缀和状态查询会话列表
    public List<ThreadEntity> findByTopicStartsWithAndStatus(String topicPrefix, String status) {
        return threadRepository.findByTopicStartsWithAndStatusAndDeletedFalse(topicPrefix, status);
    }

    // @Cacheable(value = "thread", key = "#user.uid + '-' +
    // #pageable.getPageNumber()", unless = "#result == null")
    public Page<ThreadEntity> findByOwner(UserEntity user, Pageable pageable) {
        return threadRepository.findByOwnerAndHideAndDeleted(user, false, false, pageable);
    }

    /**
     * 根据访客ID查找最近的客服会话记录
     * 用于最近一次会话路由策略
     */
    public List<ThreadEntity> findRecentAgentThreadsByVisitorUid(String visitorUid) {
        return threadRepository.findRecentAgentThreadsByVisitorUid(visitorUid);
    }

    public Map<String, Long> queryClosedByCloseType(java.time.ZonedDateTime start, java.time.ZonedDateTime end) {
        List<Object[]> rows = threadRepository.countClosedGroupedByCloseType(start, end);
        Map<String, Long> result = new HashMap<>();
        for (Object[] row : rows) {
            String key = (String) row[0];
            Long count = (Long) row[1];
            result.put(key != null ? key : "NONE", count != null ? count : 0L);
        }
        return result;
    }

    public Page<ThreadEntity> queryByOrgEntity(ThreadRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ThreadEntity> specs = ThreadSpecification.search(request, authService);
        return threadRepository.findAll(specs, pageable);
    }

    @Override
    public Page<ThreadResponse> queryByOrg(ThreadRequest request) {
        Page<ThreadEntity> threadPage = queryByOrgEntity(request);
        return threadPage.map(this::convertToResponse);
    }

    // 客服端-加载自己的会话列表
    @Override
    public Page<ThreadResponse> queryByUser(ThreadRequest request) {
        UserEntity user = authService.getUser();

        if (user == null) {
            throw CommonI18nExceptions.loginRequired();
        }

        request.setUserUid(user.getUid());
        request.setOwnerUid(user.getUid());

        Pageable pageable = request.getPageable();
        Specification<ThreadEntity> specs = ThreadSpecification.searchForUser(request, user.getUid(), user.getOrgUid());

        if (!Boolean.FALSE.equals(request.getMergeByTopic())) {
            List<ThreadEntity> mergedThreads = mergeThreadsByTopic(threadRepository.findAll(specs));
            return pageMergedThreads(mergedThreads, pageable);
        }

        Page<ThreadEntity> threadPage = threadRepository.findAll(specs, pageable);
        return threadPage.map(this::convertToResponse);
    }

    /**
     * 访客端-根据访客UID分页查询其相关的会话列表（支持基础筛选）
     */
    public Page<ThreadResponse> queryByVisitor(ThreadRequest request) {
        if (request == null) {
            return Page.empty();
        }

        String uid = request.getUid();
        String visitorUid = request.getVisitorUid();
        if (!StringUtils.hasText(uid) && !StringUtils.hasText(visitorUid)) {
            return Page.empty();
        }

        Pageable pageable = request.getPageable();

        // visitor(匿名)侧：统一走 Specification 查询，避免 repository native query 的列名/排序兼容问题
        Pageable effectivePageable = (pageable == null)
                ? Pageable.unpaged()
                : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

        Specification<ThreadEntity> specs = ThreadSpecification.searchForVisitor(request, uid, visitorUid);

        if (Boolean.TRUE.equals(request.getMergeByTopic())) {
            List<ThreadEntity> mergedThreads = mergeThreadsByTopic(threadRepository.findAll(specs));

            if (pageable == null || pageable.isUnpaged()) {
                return new PageImpl<>(
                        mergedThreads.stream().map(this::convertToResponse).toList(),
                        Pageable.unpaged(),
                        mergedThreads.size());
            }

            int start = Math.toIntExact(pageable.getOffset());
            if (start >= mergedThreads.size()) {
                return new PageImpl<>(List.of(), pageable, mergedThreads.size());
            }

            int end = Math.min(start + pageable.getPageSize(), mergedThreads.size());
            List<ThreadResponse> content = mergedThreads.subList(start, end).stream()
                    .map(this::convertToResponse)
                    .toList();
            return new PageImpl<>(content, pageable, mergedThreads.size());
        }

        Page<ThreadEntity> threadPage = threadRepository.findAll(specs, effectivePageable);
        return threadPage.map(this::convertToResponse);
    }

    /**
     * 访客端-查询全部匹配会话，用于上层做跨实体过滤后再分页。
     */
    public List<ThreadResponse> queryAllByVisitor(ThreadRequest request) {
        if (request == null) {
            return List.of();
        }

        String uid = request.getUid();
        String visitorUid = request.getVisitorUid();
        if (!StringUtils.hasText(uid) && !StringUtils.hasText(visitorUid)) {
            return List.of();
        }

        Specification<ThreadEntity> specs = ThreadSpecification.searchForVisitor(request, uid, visitorUid);
        List<ThreadEntity> threads = threadRepository.findAll(specs);
        List<ThreadEntity> effectiveThreads = Boolean.TRUE.equals(request.getMergeByTopic())
                ? mergeThreadsByTopic(threads)
                : threads;
        return effectiveThreads.stream().map(this::convertToResponse).toList();
    }

    private List<ThreadEntity> mergeThreadsByTopic(List<ThreadEntity> threads) {
        return threads.stream()
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                thread -> StringUtils.hasText(thread.getTopic()) ? thread.getTopic() : thread.getUid(),
                                thread -> thread,
                                (existing, ignored) -> existing,
                                LinkedHashMap::new),
                        map -> new ArrayList<>(map.values())));
    }

    private Page<ThreadResponse> pageMergedThreads(List<ThreadEntity> mergedThreads, Pageable pageable) {
        if (pageable == null || pageable.isUnpaged()) {
            return new PageImpl<>(
                    mergedThreads.stream().map(this::convertToResponse).toList(),
                    Pageable.unpaged(),
                    mergedThreads.size());
        }

        int start = Math.toIntExact(pageable.getOffset());
        if (start >= mergedThreads.size()) {
            return new PageImpl<>(List.of(), pageable, mergedThreads.size());
        }

        int end = Math.min(start + pageable.getPageSize(), mergedThreads.size());
        List<ThreadResponse> content = mergedThreads.subList(start, end).stream()
                .map(this::convertToResponse)
                .toList();
        return new PageImpl<>(content, pageable, mergedThreads.size());
    }

    public Page<ThreadResponse> queryThreadsByUserTopics(ThreadRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw CommonI18nExceptions.loginRequired();
        }

        Set<String> customerServiceTopics = topicSubscriptionRestService.findSubscribedTopicsByUserUid(user.getUid())
                .stream()
                .filter(TopicUtils::isCustomerServiceTopic)
                .collect(Collectors.toSet());

        if (customerServiceTopics.isEmpty()) {
            return Page.empty();
        }

        Pageable pageable = request.getPageable();
        Page<ThreadEntity> threadPage = threadRepository.findByTopicsInAndDeletedFalse(customerServiceTopics, pageable);

        Map<String, ThreadEntity> uniqueThreadsByTopic = new HashMap<>();
        threadPage.getContent().forEach(thread -> uniqueThreadsByTopic.putIfAbsent(thread.getTopic(), thread));

        List<ThreadEntity> uniqueThreads = new ArrayList<>(uniqueThreadsByTopic.values());
        Page<ThreadEntity> filteredPage = new PageImpl<>(
                uniqueThreads,
                pageable,
                Math.min(uniqueThreads.size(), threadPage.getTotalElements()));

        return filteredPage.map(this::convertToResponse);
    }

    public Page<ThreadResponse> queryByTopic(ThreadRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ThreadEntity> specs = ThreadSpecification.search(request, authService);
        Page<ThreadEntity> threadPage = threadRepository.findAll(specs, pageable);
        return threadPage.map(this::convertToResponse);
    }

    public ThreadResponse queryByTopicAndOwner(ThreadRequest request) {
        UserEntity owner = authService.getUser();
        if (owner == null) {
            throw CommonI18nExceptions.loginRequired();
        }
        Optional<ThreadEntity> threadOptional = findFirstByTopicAndOwner(request.getTopic(), owner);
        if (threadOptional.isPresent()) {
            return convertToResponse(threadOptional.get());
        } else {
            throw ResourceI18nExceptions.threadNotFound();
        }
    }

    @Override
    public ThreadResponse queryByUid(ThreadRequest request) {
        Optional<ThreadEntity> threadOptional = findByUid(request.getUid());
        if (threadOptional.isPresent()) {
            return convertToResponse(threadOptional.get());
        } else {
            throw ResourceI18nExceptions.threadNotFound(request.getUid());
        }
    }

    @Transactional
    @Override
    public ThreadResponse create(ThreadRequest request) {
        UserEntity owner = authService.getUser();
        //
        Optional<ThreadEntity> threadOptional = findFirstByTopicAndOwner(request.getTopic(), owner);
        if (threadOptional.isPresent()) {
            return convertToResponse(threadOptional.get());
        }
        //
        ThreadEntity thread = modelMapper.map(request, ThreadEntity.class);
        thread.setUid(uidUtils.getUid());
        thread.setStatus(ThreadProcessStatusEnum.CHATTING.name());
        //
        String user = request.getUser().toJson();
        log.info("request {}, user {}", request.toString(), user);
        thread.setUser(user);
        //
        thread.setChannel(request.getChannel());
        thread.setOwner(owner);
        thread.setOrgUid(owner.getOrgUid());
        //
        ThreadEntity savedThread = save(thread);
        if (savedThread == null) {
            throw new RuntimeException("thread save failed");
        }
        //
        return convertToResponse(savedThread);
    }

    @Transactional
    public ThreadResponse createGroupMemberThread(String user, String topic, String orgUid, UserEntity owner) {
        //
        Optional<ThreadEntity> threadOptional = findFirstByTopicAndOwner(topic, owner);
        if (threadOptional.isPresent()) {
            return convertToResponse(threadOptional.get());
        }

        ThreadEntity groupThread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .type(ThreadTypeEnum.GROUP.name())
                .topic(topic)
                // .unreadCount(0)
                .status(ThreadProcessStatusEnum.CHATTING.name())
                .channel(ChannelEnum.SYSTEM.name())
                .user(user)
                .owner(owner)
                .orgUid(orgUid)
                .build();

        ThreadEntity updateThread = save(groupThread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }

        return convertToResponse(updateThread);
    }

    public void removeGroupMemberThread(String topic, UserEntity owner) {
        Optional<ThreadEntity> threadOptional = findFirstByTopicAndOwner(topic, owner);
        if (threadOptional.isPresent()) {
            deleteByUid(threadOptional.get().getUid());
            //
            bytedeskEventPublisher.publishEvent(new ThreadRemoveTopicEvent(this, threadOptional.get()));
        }
    }

    // 当前群组更新昵称的时候，更新群组会话的昵称
    public void updateGroupMemberThread(String user, String topic, UserEntity owner) {
        Optional<ThreadEntity> threadOptional = findFirstByTopicAndOwner(topic, owner);
        if (threadOptional.isPresent()) {
            ThreadEntity thread = threadOptional.get();
            thread.setUser(user);
            save(thread);
        }
    }

    /** 文件助手会话：file/{user_uid} */
    public ThreadResponse createFileAssistantThread(UserEntity user) {
        //
        String topic = TopicUtils.getFileTopic(user.getUid());
        //
        Optional<ThreadEntity> threadOptional = findFirstByTopicAndOwner(topic, user);
        if (threadOptional.isPresent()) {
            return convertToResponse(threadOptional.get());
        }
        // 文件助手用户信息，头像、昵称等
        UserProtobuf userSimple = UserUtils.getFileAssistantUser();
        //
        ThreadEntity assistantThread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .type(ThreadTypeEnum.ASSISTANT.name())
                .topic(topic)
                // .unreadCount(0)
                .status(ThreadProcessStatusEnum.NEW.name())
                .channel(ChannelEnum.SYSTEM.name())
                .level(LevelEnum.USER.name())
                .user(userSimple.toJson())
                .userUid(user.getUid())
                .owner(user)
                .build();

        ThreadEntity updateThread = save(assistantThread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }

        return convertToResponse(updateThread);
    }

    // 系统通知会话：system/{user_uid}
    public ThreadResponse createSystemPublicAccountThread(UserEntity user) {
        //
        String topic = TopicUtils.getSystemTopic(user.getUid());
        //
        Optional<ThreadEntity> threadOptional = findFirstByTopicAndOwner(topic, user);
        if (threadOptional.isPresent()) {
            return convertToResponse(threadOptional.get());
        }

        UserProtobuf userSimple = UserUtils.getSystemUser();
        //
        ThreadEntity noticeThread = ThreadEntity.builder()
                .uid(uidUtils.getUid())
                .type(ThreadTypeEnum.CHANNEL.name())
                .topic(topic)
                // .unreadCount(0)
                .status(ThreadProcessStatusEnum.NEW.name())
                .channel(ChannelEnum.SYSTEM.name())
                .level(LevelEnum.USER.name())
                .user(userSimple.toJson())
                .userUid(user.getUid())
                .owner(user)
                .build();
        //
        ThreadEntity updateThread = save(noticeThread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }

        return convertToResponse(updateThread);
    }

    public ThreadResponse update(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        //
        Optional<ThreadEntity> threadOptional = findByUid(threadRequest.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        //
        ThreadEntity thread = threadOptional.get();
        thread.setTop(threadRequest.getTop());
        thread.setUnread(threadRequest.getUnread());
        thread.setMute(threadRequest.getMute());
        thread.setHide(threadRequest.getHide());
        thread.setStar(threadRequest.getStar());
        thread.setFold(threadRequest.getFold());
        thread.setContent(threadRequest.getContent());
        // thread.setTagList(threadRequest.getTagList()); // 标签列表不在这里更新，使用 updateTagList
        // 方法更新
        //
        ThreadEntity updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(updateThread);
    }

    // update top
    public ThreadResponse updateTop(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        //
        Optional<ThreadEntity> threadOptional = findByUid(threadRequest.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        //
        ThreadEntity thread = threadOptional.get();
        thread.setTop(threadRequest.getTop());
        //
        ThreadEntity updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(updateThread);
    }

    // update star
    public ThreadResponse updateStar(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        //
        Optional<ThreadEntity> threadOptional = findByUid(threadRequest.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        //
        ThreadEntity thread = threadOptional.get();
        thread.setStar(threadRequest.getStar());
        //
        ThreadEntity updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(updateThread);
    }

    // update mute
    public ThreadResponse updateMute(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        //
        Optional<ThreadEntity> threadOptional = findByUid(threadRequest.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        //
        ThreadEntity thread = threadOptional.get();
        thread.setMute(threadRequest.getMute());
        //
        ThreadEntity updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(updateThread);
    }

    // update hide
    public ThreadResponse updateHide(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        //
        Optional<ThreadEntity> threadOptional = findByUid(threadRequest.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        //
        ThreadEntity thread = threadOptional.get();
        thread.setHide(threadRequest.getHide());
        //
        ThreadEntity updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(updateThread);
    }

    // update fold
    public ThreadResponse updateFold(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        //
        Optional<ThreadEntity> threadOptional = findByUid(threadRequest.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        //
        ThreadEntity thread = threadOptional.get();
        thread.setFold(threadRequest.getFold());
        //
        ThreadEntity updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(updateThread);
    }

    // update user info
    public ThreadResponse updateUser(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        //
        Optional<ThreadEntity> threadOptional = findByUid(threadRequest.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        //
        ThreadEntity thread = threadOptional.get();
        thread.setUser(threadRequest.getUser().toJson());
        //
        ThreadEntity updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(updateThread);
    }

    // update tagList
    public ThreadResponse updateTagList(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        //
        Optional<ThreadEntity> threadOptional = findByUid(threadRequest.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        //
        ThreadEntity thread = threadOptional.get();
        thread.setTagList(threadRequest.getTagList());
        //
        ThreadEntity updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(updateThread);
    }

    // update unread
    public ThreadResponse updateUnread(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        //
        Optional<ThreadEntity> threadOptional = findByUid(threadRequest.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        //
        ThreadEntity thread = threadOptional.get();
        thread.setUnread(threadRequest.getUnread());
        //
        ThreadEntity updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(updateThread);
    }

    /** observedMessageUids 服务端上限：防止客户端一次携带海量 uid 造成慢查询 */
    private static final int FILE_ASSISTANT_READ_MAX_OBSERVED = 200;

    /**
     * 文件助手会话“打开即已读”。
     *
     * 前置条件（服务端强校验，不信任前端判定）：
     * - 会话存在且未删除；
     * - type = ASSISTANT 且 topic 精确等于 file/{当前用户 uid}（本人专属文件助手）；
     * - owner 为当前用户。
     *
     * 行为：
     * 1. 将会话内所有未读消息（SENDING/SUCCESS/DELIVERED，未删除）条件更新为 READ（幂等，
     *    不触碰 READ/失败/撤回等终态）；
     * 2. 清除该会话的手动未读标记（unread = false），不修改 top/mute/star/fold 等其他属性；
     * 3. observedMessageUids：客户端已观察到的消息 uid 集合（上限 200），用于确认其是否
     *    已落库——服务端消息经 Redis 队列 + 5 秒定时任务异步持久化，回显到达 ≠ 已落库。
     *    已落库且属于本会话 → 已随步骤 1 置 READ；未落库或归属异常 → 返回 pendingMessageUids，
     *    客户端须做有限重试，不得据 updatedCount=0 判定全部成功；
     * 4. unreadCount 为操作后服务端权威未读数。
     */
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "thread", key = "#threadUid"),
    })
    public ThreadFileAssistantReadResponse markFileAssistantRead(String threadUid, List<String> observedMessageUids) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw CommonI18nExceptions.loginRequired();
        }
        if (!StringUtils.hasText(threadUid)) {
            throw CommonI18nExceptions.forbidden("thread.uid.required");
        }
        //
        ThreadEntity thread = threadRepository.findByUid(threadUid)
                .orElseThrow(ResourceI18nExceptions::threadNotFound);
        if (thread.isDeleted()) {
            throw ResourceI18nExceptions.threadNotFound(threadUid);
        }
        // 仅允许本人专属文件助手会话：类型 + 精确 topic 联合判定，防止越权清其他 ASSISTANT/通知会话
        String expectedTopic = TopicUtils.getFileTopic(user.getUid());
        if (!ThreadTypeEnum.ASSISTANT.name().equals(thread.getType())
                || !expectedTopic.equals(thread.getTopic())
                || thread.getOwner() == null
                || !user.getUid().equals(thread.getOwner().getUid())) {
            log.warn("markFileAssistantRead rejected: threadUid={}, type={}, topic={}, currentUser={}",
                    threadUid, thread.getType(), thread.getTopic(), user.getUid());
            throw CommonI18nExceptions.forbidden("thread.fileAssistant.read.notAllowed");
        }
        //
        // 1. 批量条件更新未读 → READ（幂等）
        int updatedCount = messageRestService.markThreadUnreadAsRead(threadUid);
        // 2. 清除手动未读标记（不动其他属性；已为 false 时跳过，避免无谓版本号自增）
        if (Boolean.TRUE.equals(thread.getUnread())) {
            thread.setUnread(false);
            save(thread);
        }
        //
        // 3. observedMessageUids 落库确认
        List<String> pendingMessageUids = new ArrayList<>();
        if (observedMessageUids != null && !observedMessageUids.isEmpty()) {
            List<String> limited = observedMessageUids.stream()
                    .filter(StringUtils::hasText)
                    .distinct()
                    .limit(FILE_ASSISTANT_READ_MAX_OBSERVED + 1L)
                    .collect(Collectors.toList());
            if (limited.size() > FILE_ASSISTANT_READ_MAX_OBSERVED) {
                throw CommonI18nExceptions.forbidden("thread.fileAssistant.read.observedTooMany");
            }
            List<MessageEntity> found = messageRestService.findByUidIn(limited);
            // 未查到（尚未落库）或归属不符（不属于本会话/已删除）→ pending，交客户端有限重试
            java.util.Set<String> foundInThread = found.stream()
                    .filter(m -> !m.isDeleted() && threadUid.equals(m.getThread() != null ? m.getThread().getUid() : null))
                    .map(m -> m.getUid())
                    .collect(Collectors.toSet());
            for (String uid : limited) {
                if (!foundInThread.contains(uid)) {
                    pendingMessageUids.add(uid);
                }
            }
        }
        //
        // 4. 权威未读数：批量更新后 thread.messages 集合可能仍是旧快照，按消息重查
        int unreadCount = countFileAssistantUnread(threadUid);
        //
        log.info("markFileAssistantRead: threadUid={}, updated={}, pending={}, unread={}",
                threadUid, updatedCount, pendingMessageUids.size(), unreadCount);
        return ThreadFileAssistantReadResponse.builder()
                .threadUid(threadUid)
                .updatedCount(updatedCount)
                .unreadCount(unreadCount)
                .pendingMessageUids(pendingMessageUids)
                .build();
    }

    /** 文件助手会话权威未读数：未删除 + 状态 SENDING/SUCCESS/DELIVERED 的消息条数 */
    private int countFileAssistantUnread(String threadUid) {
        List<MessageEntity> messages = messageRestService.findByThreadUid(threadUid);
        int count = 0;
        for (MessageEntity message : messages) {
            if (message == null || message.isDeleted()) {
                continue;
            }
            if (message.isUnread()) {
                count++;
            }
        }
        return count;
    }

    // update state
    public ThreadResponse updateStatus(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        //
        Optional<ThreadEntity> threadOptional = findByUid(threadRequest.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        //
        ThreadEntity thread = threadOptional.get();
        thread.setStatus(threadRequest.getStatus());
        //
        ThreadEntity updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(updateThread);
    }

    // update/note
    public ThreadResponse updateNote(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        //
        Optional<ThreadEntity> threadOptional = findByUid(threadRequest.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }
        //
        ThreadEntity thread = threadOptional.get();
        thread.setNote(threadRequest.getNote());
        //
        ThreadEntity updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(updateThread);
    }

    /**
     * 管理后台更新会话（聚合更新），用于 ThreadEditDrawer 一次性提交
     */
    @Transactional
    public ThreadResponse adminUpdate(ThreadRequest threadRequest) {
        if (!StringUtils.hasText(threadRequest.getUid())) {
            throw new RuntimeException("thread uid is required");
        }
        Optional<ThreadEntity> threadOptional = findByUid(threadRequest.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("update thread " + threadRequest.getUid() + " not found");
        }

        ThreadEntity thread = threadOptional.get();

        if (StringUtils.hasText(threadRequest.getStatus())) {
            thread.setStatus(threadRequest.getStatus());
        }
        if (threadRequest.getTagList() != null) {
            thread.setTagList(threadRequest.getTagList());
        }

        ThreadEntity updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(updateThread);
    }

    public ThreadResponse autoClose(ThreadEntity thread) {
        UserEntity user = thread.getOwner();
        // log.info(thread.getUid() + "自动关闭");
        ThreadRequest threadRequest = ThreadRequest.builder()
                .uid(thread.getUid())
                .topic(thread.getTopic())
                .userUid(user != null ? user.getUid() : null)
                .closeType(ThreadCloseTypeEnum.AUTO.name())
                .status(ThreadProcessStatusEnum.CLOSED.name())
                .build();
        return closeByUid(threadRequest);
    }

    public ThreadResponse closeByUid(ThreadRequest request) {
        UserEntity user = authService.getUser();
        if (user != null) {
            request.setUserUid(user.getUid());
        }
        //
        Optional<ThreadEntity> threadOptional = findByUid(request.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("close thread " + request.getUid() + " not found");
        }
        //
        ThreadEntity thread = threadOptional.get();
        if (thread.isClosed()) {
            if (!StringUtils.hasText(thread.getCloseType()) && StringUtils.hasText(request.getCloseType())) {
                thread.setCloseType(request.getCloseType());
                ThreadEntity updatedThread = save(thread);
                return convertToResponse(updatedThread != null ? updatedThread : thread);
            }
            log.info("thread {} is already closed, returning current state", thread.getUid());
            return convertToResponse(thread);
        }
        // 设置关闭来源
        if (StringUtils.hasText(request.getCloseType())) {
            thread.setCloseType(request.getCloseType());
        }
        thread.setStatus(ThreadProcessStatusEnum.CLOSED.name());
        markClosedAtIfAbsent(thread);
        // 发布关闭消息, 通知用户
        String content;
        String closeType = thread.getCloseType();
        if (com.bytedesk.core.thread.enums.ThreadCloseTypeEnum.AUTO.name().equalsIgnoreCase(closeType)) {
            content = I18Consts.I18N_AUTO_CLOSED;
        } else if (com.bytedesk.core.thread.enums.ThreadCloseTypeEnum.AGENT.name().equalsIgnoreCase(closeType)) {
            content = I18Consts.I18N_AGENT_CLOSED;
        } else if (com.bytedesk.core.thread.enums.ThreadCloseTypeEnum.VISITOR.name().equalsIgnoreCase(closeType)) {
            content = I18Consts.I18N_VISITOR_CLOSED;
        } else {
            content = I18Consts.I18N_AGENT_CLOSED;
        }
        MessageTypeEnum mt;
        if (com.bytedesk.core.thread.enums.ThreadCloseTypeEnum.AUTO.name().equalsIgnoreCase(closeType)) {
            mt = MessageTypeEnum.AUTO_CLOSED;
        } else if (com.bytedesk.core.thread.enums.ThreadCloseTypeEnum.VISITOR.name().equalsIgnoreCase(closeType)) {
            mt = MessageTypeEnum.VISITOR_CLOSED;
        } else {
            mt = MessageTypeEnum.AGENT_CLOSED;
        }
        thread.setContent(ThreadContent.of(mt, content, content).toJson());
        //
        ThreadEntity updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        if (Boolean.TRUE.equals(request.getUnsubscribe())
                || com.bytedesk.core.thread.enums.ThreadCloseTypeEnum.AUTO.name().equalsIgnoreCase(closeType)) {
            TopicSubscriptionRequest topicRequest = TopicSubscriptionRequest.builder()
                    .topic(request.getTopic())
                    .userUid(request.getUserUid())
                    .build();
            topicSubscriptionRestService.remove(topicRequest);
        }
        // 发布关闭事件（提交后发布，避免监听器与发布者事务并发读写同一会话行）
        publishThreadCloseEventAfterCommit(updateThread);
        //
        return convertToResponse(updateThread);
    }

    // 找到第一个未关闭的，执行关闭并返回，用于处理前端传递thread.uid不准确的情况
    public ThreadResponse closeByTopic(ThreadRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw CommonI18nExceptions.loginRequired();
        }
        request.setUserUid(user.getUid());

        List<ThreadEntity> threads = findListByTopic(request.getTopic());
        if (threads == null || threads.isEmpty()) {
            return null;
        }

        ThreadEntity lastUpdatedThread = null;
        ThreadEntity targetUpdatedThread = null;
        for (ThreadEntity thread : threads) {
            if (thread == null) {
                continue;
            }

            if (thread.isClosed()) {
                continue;
            }

            if (StringUtils.hasText(request.getCloseType())) {
                thread.setCloseType(request.getCloseType());
            }
            thread.setStatus(ThreadProcessStatusEnum.CLOSED.name());
            markClosedAtIfAbsent(thread);

            // 发布关闭消息, 通知用户
            String closeType = thread.getCloseType();
            String content;
            if (com.bytedesk.core.thread.enums.ThreadCloseTypeEnum.AUTO.name().equalsIgnoreCase(closeType)) {
                content = I18Consts.I18N_AUTO_CLOSED;
            } else if (com.bytedesk.core.thread.enums.ThreadCloseTypeEnum.AGENT.name().equalsIgnoreCase(closeType)) {
                content = I18Consts.I18N_AGENT_CLOSED;
            } else if (com.bytedesk.core.thread.enums.ThreadCloseTypeEnum.VISITOR.name().equalsIgnoreCase(closeType)) {
                content = I18Consts.I18N_VISITOR_CLOSED;
            } else {
                content = I18Consts.I18N_AGENT_CLOSED;
            }

            MessageTypeEnum mt;
            if (com.bytedesk.core.thread.enums.ThreadCloseTypeEnum.AUTO.name().equalsIgnoreCase(closeType)) {
                mt = MessageTypeEnum.AUTO_CLOSED;
            } else if (com.bytedesk.core.thread.enums.ThreadCloseTypeEnum.VISITOR.name().equalsIgnoreCase(closeType)) {
                mt = MessageTypeEnum.VISITOR_CLOSED;
            } else {
                mt = MessageTypeEnum.AGENT_CLOSED;
            }
            thread.setContent(ThreadContent.of(mt, content, content).toJson());

            ThreadEntity updateThread = save(thread);
            if (updateThread == null) {
                throw new RuntimeException("thread save failed");
            }

            // 发布关闭事件（每条 thread 都要发；提交后发布避免与发布者事务并发）
            publishThreadCloseEventAfterCommit(updateThread);
            lastUpdatedThread = updateThread;

            if (StringUtils.hasText(request.getUid()) && request.getUid().equals(updateThread.getUid())) {
                targetUpdatedThread = updateThread;
            }
        }

        if (Boolean.TRUE.equals(request.getUnsubscribe())) {
            TopicSubscriptionRequest topicRequest = TopicSubscriptionRequest.builder()
                    .topic(request.getTopic())
                    .userUid(request.getUserUid())
                    .build();
            topicSubscriptionRestService.remove(topicRequest);
        }

        // 返回值：优先返回 thread.uid == request.uid
        if (targetUpdatedThread != null) {
            return convertToResponse(targetUpdatedThread);
        }
        if (StringUtils.hasText(request.getUid())) {
            for (ThreadEntity thread : threads) {
                if (thread != null && request.getUid().equals(thread.getUid())) {
                    return convertToResponse(thread);
                }
            }
        }

        // 如果 request.uid 未命中：有更新则返回最后更新的，否则返回第一条
        return convertToResponse(lastUpdatedThread != null ? lastUpdatedThread : threads.get(0));
    }

    // 获取当前接待会话数量
    public int countByThreadTopicAndState(String topic, String state) {
        return threadRepository.countByTopicAndStatusAndDeletedFalse(topic, state);
    }

    // count by topic and status not
    public int countByThreadTopicAndStateNot(String topic, String state) {
        return threadRepository.countByTopicAndStatusNotAndDeletedFalse(topic, state);
    }

    @Transactional
    public ThreadSequenceResponse allocateMessageMetadata(String threadUid) {
        if (!StringUtils.hasText(threadUid)) {
            throw new IllegalArgumentException("thread uid is required");
        }

        return ThreadSequenceResponse.builder()
                .threadUid(threadUid)
                .messageUid(uidUtils.getUid())
                .timestamp(System.currentTimeMillis()) //
                .build();
    }

    public List<ThreadEntity> findServiceThreadStateStarted() {
        List<String> types = Arrays.asList(new String[] { ThreadTypeEnum.AGENT.name(), ThreadTypeEnum.WORKGROUP.name(),
                ThreadTypeEnum.ROBOT.name() });
        return threadRepository.findByTypesInAndStatusNotAndDeletedFalse(types, ThreadProcessStatusEnum.CLOSED.name());
    }

    @Override
    @Caching(put = {
            @CachePut(value = "thread", key = "#thread.uid", unless = "#result == null"),
            @CachePut(value = "thread", key = "#thread.topic", unless = "#result == null")
    })
    protected ThreadEntity doSave(ThreadEntity entity) {
        // log.info("doSave thread agent: {}, owner: {}", entity.getAgent(),
        // entity.getOwner());
        ThreadEntity savedEntity = threadRepository.save(entity);
        // 更新活跃会话缓存
        if (savedEntity != null) {
            activeThreadCacheService.addOrUpdateActiveThread(savedEntity);
        }
        return savedEntity;
    }

    @Recover
    public ThreadEntity recover(ObjectOptimisticLockingFailureException e, ThreadEntity entity) {
        return handleOptimisticLockingFailureException(e, entity);
    }

    @Override
    @Caching(put = {
            @CachePut(value = "thread", key = "#entity.uid", unless = "#result == null"),
            @CachePut(value = "thread", key = "#entity.topic", unless = "#result == null")
    })
    public ThreadEntity handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e,
            ThreadEntity entity) {
        try {
            Optional<ThreadEntity> latestOptional = threadRepository.findByUid(entity.getUid());
            if (latestOptional.isPresent()) {
                ThreadEntity latestEntity = latestOptional.get();
                log.warn("Optimistic locking conflict detected for thread topic {}, attempting to merge data",
                        entity.getTopic());

                // Preserve basic thread information
                if (entity.getTopic() != null) {
                    latestEntity.setTopic(entity.getTopic());
                }
                if (entity.getContent() != null) {
                    latestEntity.setContent(entity.getContent());
                }
                if (entity.getType() != null) {
                    latestEntity.setType(entity.getType());
                }
                if (entity.getStatus() != null) {
                    latestEntity.setStatus(entity.getStatus());
                }
                log.info("latestEntity status: {}", latestEntity.getStatus());

                // Preserve thread state flags
                // latestEntity.setUnreadCount(Math.max(latestEntity.getUnreadCount(),
                // entity.getUnreadCount()));
                latestEntity.setStar(entity.getStar() != null ? entity.getStar() : latestEntity.getStar());
                latestEntity.setTop(entity.getTop() != null ? entity.getTop() : latestEntity.getTop());
                latestEntity.setUnread(entity.getUnread() != null ? entity.getUnread() : latestEntity.getUnread());
                latestEntity.setMute(entity.getMute() != null ? entity.getMute() : latestEntity.getMute());
                latestEntity.setHide(entity.getHide() != null ? entity.getHide() : latestEntity.getHide());
                latestEntity.setFold(entity.getFold() != null ? entity.getFold() : latestEntity.getFold());
                if (entity.getCloseType() != null) {
                    latestEntity.setCloseType(entity.getCloseType());
                }
                if (entity.getClosedAt() != null && latestEntity.getClosedAt() == null) {
                    latestEntity.setClosedAt(entity.getClosedAt());
                }

                // Preserve metadata
                if (entity.getNote() != null) {
                    latestEntity.setNote(entity.getNote());
                }
                if (entity.getTagList() != null && !entity.getTagList().isEmpty()) {
                    latestEntity.setTagList(entity.getTagList());
                }
                // if (entity.getChannel() != null) {
                // latestEntity.setChannel(entity.getChannel());
                // }
                if (entity.getExtra() != null) {
                    latestEntity.setExtra(entity.getExtra());
                }

                // Preserve user and agent information
                if (entity.getUser() != null) {
                    latestEntity.setUser(entity.getUser());
                }
                if (entity.getAgent() != null) {
                    latestEntity.setAgent(entity.getAgent());
                }
                if (entity.getRobot() != null) {
                    latestEntity.setRobot(entity.getRobot());
                }
                if (entity.getWorkgroup() != null) {
                    latestEntity.setWorkgroup(entity.getWorkgroup());
                }

                // Preserve lists
                if (entity.getInvites() != null && !entity.getInvites().isEmpty()) {
                    latestEntity.setInvites(entity.getInvites());
                }
                if (entity.getMonitors() != null && !entity.getMonitors().isEmpty()) {
                    latestEntity.setMonitors(entity.getMonitors());
                }
                if (entity.getAssistants() != null && !entity.getAssistants().isEmpty()) {
                    latestEntity.setAssistants(entity.getAssistants());
                }
                if (entity.getTicketors() != null && !entity.getTicketors().isEmpty()) {
                    latestEntity.setTicketors(entity.getTicketors());
                }

                // Preserve process information
                if (entity.getProcessInstanceId() != null) {
                    latestEntity.setProcessInstanceId(entity.getProcessInstanceId());
                }
                if (entity.getProcessEntityUid() != null) {
                    latestEntity.setProcessEntityUid(entity.getProcessEntityUid());
                }

                // Preserve owner if changed
                if (entity.getOwner() != null) {
                    latestEntity.setOwner(entity.getOwner());
                }

                log.info("Successfully merged thread data for {}", entity.getUid());
                return threadRepository.save(latestEntity);
            }
        } catch (Exception ex) {
            log.error("Failed to handle optimistic locking conflict for thread {}: {}", entity.getUid(),
                    ex.getMessage(), ex);
            throw new RuntimeException("Failed to handle optimistic locking conflict: " + ex.getMessage(), ex);
        }
        return null;
    }

    private void markClosedAtIfAbsent(ThreadEntity thread) {
        if (thread == null || thread.getClosedAt() != null) {
            return;
        }
        if (ThreadProcessStatusEnum.CLOSED.name().equals(thread.getStatus())
                || ThreadProcessStatusEnum.TIMEOUT.name().equals(thread.getStatus())) {
            thread.setClosedAt(BdDateUtils.now());
        }
    }

    @CacheEvict(value = "thread", key = "#topic")
    public void deleteByTopic(String topic) {
        List<ThreadEntity> threads = findListByTopic(topic);
        threads.forEach(thread -> {
            thread.setDeleted(true);
            save(thread);
        });
    }

    @Override
    @CacheEvict(value = "thread", key = "#uid")
    public void deleteByUid(String uid) {
        Optional<ThreadEntity> threadOptional = findByUid(uid);
        threadOptional.ifPresent(thread -> {
            thread.setDeleted(true);
            save(thread);
        });
    }

    @Caching(evict = {
            @CacheEvict(value = "thread", key = "#p0.uid", condition = "#p0 != null && #p0.uid != null"),
            @CacheEvict(value = "thread", key = "#p0.topic", condition = "#p0 != null && #p0.topic != null")
    })
    public void delete(ThreadRequest entity) {
        deleteByUid(entity.getUid());
    }

    public ThreadResponse restore(ThreadRequest request) {
        ThreadEntity thread = threadRepository.findByUid(request.getUid())
                .orElseThrow(ResourceI18nExceptions::threadNotFound);
        thread.setDeleted(false);
        return convertToResponse(save(thread));
    }

    public ThreadResponse convertToResponse(ThreadEntity thread) {
        ThreadResponse threadResponse = ThreadConvertUtils.convertToThreadResponse(thread);
        enrichMemberThreadUnreadCount(thread, threadResponse);
        return threadResponse;
    }

    /**
     * MEMBER 同事会话未读数修正。
     *
     * 背景：同事一对一会话在库中是成对的两个 thread（topic 互为反转：org/member/A/B 与 org/member/B/A），
     * 消息只挂载在发送方自己的 thread 上（反转复制仅发生在 MQTT 投递层，不落库），
     * 因此 {@link ThreadEntity#getUnreadCount()} 遍历本 thread 的 messages 集合
     * （里面只有自己发送的消息）恒为 0，导致 ThreadList 刷新/重新登录后不显示未读数。
     *
     * 这里按“正反 topic 对 + 排除自己发送 + 未读状态”跨 thread 统计真实未读数，
     * 消息类型口径与前端 desktop shouldIncreaseUnreadCountByMessageType 对齐。
     */
    private void enrichMemberThreadUnreadCount(ThreadEntity thread, ThreadResponse threadResponse) {
        if (!thread.isMember() || !StringUtils.hasText(thread.getTopic())) {
            return;
        }
        // 防御：MEMBER 会话 topic 固定为 org/member/{self}/{other} 四段，异常格式直接跳过
        if (thread.getTopic().split("/").length != 4) {
            return;
        }
        String ownerUserUid = thread.getOwner() != null ? thread.getOwner().getUid() : null;
        if (!StringUtils.hasText(ownerUserUid)) {
            return;
        }
        String reverseTopic = TopicUtils.getOrgMemberTopicReverse(thread.getTopic());
        int unreadCount = (int) messageRestService.countMemberUnreadByTopics(
                thread.getTopic(), reverseTopic, ownerUserUid);
        threadResponse.setUnreadCount(unreadCount);
    }

    @Override
    protected Specification<ThreadEntity> createSpecification(ThreadRequest request) {
        return ThreadSpecification.search(request, authService);
    }

    @Override
    protected Page<ThreadEntity> executePageQuery(Specification<ThreadEntity> spec, Pageable pageable) {
        return threadRepository.findAll(spec, pageable);
    }

    /**
     * 标记访客在当前会话中的未读客服消息为已读
     * 将 thread 中所有 agent 发送的未读消息状态更新为 READ，
     * 这样 getVisitorUnreadCount() 会返回 0。
     */
    @Transactional
    public void markVisitorMessagesRead(String threadUid) {
        if (!StringUtils.hasText(threadUid)) {
            return;
        }

        List<MessageEntity> messages = messageRestService.findByThreadUid(threadUid);
        int updated = 0;
        for (MessageEntity message : messages) {
            if (message == null || message.isDeleted()) {
                continue;
            }
            // 只处理 agent 发送的未读消息
            if (message.isFromAgent() && message.isUnread()) {
                message.setStatus(MessageStatusEnum.READ.name());
                messageRestService.save(message);
                updated++;
            }
        }
        log.info("markVisitorMessagesRead: threadUid={}, updated={}", threadUid, updated);
    }

}
