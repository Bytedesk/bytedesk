/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-14 13:39:02
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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Arrays;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.bytedesk.core.base.BaseRestServiceWithExcel;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.enums.ChannelEnum;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.exception.NotFoundException;
import com.bytedesk.core.exception.NotLoginException;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.rbac.user.UserUtils;
import com.bytedesk.core.thread.event.ThreadCloseEvent;
import com.bytedesk.core.thread.event.ThreadRemoveTopicEvent;
import com.bytedesk.core.topic.TopicEntity;
import com.bytedesk.core.topic.TopicRequest;
import com.bytedesk.core.topic.TopicRestService;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ThreadRestService
        extends BaseRestServiceWithExcel<ThreadEntity, ThreadRequest, ThreadResponse, ThreadExcel> {

    private final AuthService authService;

    private final ModelMapper modelMapper;

    private final ThreadRepository threadRepository;

    private final UidUtils uidUtils;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    private final TopicRestService topicRestService;

    @Override
    public Page<ThreadEntity> queryByOrgEntity(ThreadRequest request) {
        Pageable pageable = request.getPageable();
        Specification<ThreadEntity> specs = ThreadSpecification.search(request, authService);
        return threadRepository.findAll(specs, pageable);
    }

    public Page<ThreadResponse> queryByOrg(ThreadRequest request) {
        Page<ThreadEntity> threadPage = queryByOrgEntity(request);
        return threadPage.map(this::convertToResponse);
    }

    public Page<ThreadResponse> query(ThreadRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new RuntimeException("login first");
        }
        request.setUserUid(user.getUid());
        request.setOwnerUid(user.getUid());
        //
        return queryByOrg(request);
    }

    @Override
    public Page<ThreadResponse> queryByUser(ThreadRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException("login first");
        }
        request.setUserUid(user.getUid());
        // 
        return queryByOrg(request);
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
            throw new NotLoginException("owner not found");
        }
        Optional<ThreadEntity> threadOptional = findFirstByTopicAndOwner(request.getTopic(), owner);
        if (threadOptional.isPresent()) {
            return convertToResponse(threadOptional.get());
        } else {
            throw new NotFoundException("thread not found");
        }
    }

    @Override
    public ThreadResponse queryByUid(ThreadRequest request) {
        Optional<ThreadEntity> threadOptional = findByUid(request.getUid());
        if (threadOptional.isPresent()) {
            return convertToResponse(threadOptional.get());
        } else {
            throw new NotFoundException("thread " + request.getUid() + " not found");
        }
    }

    public Page<ThreadResponse> queryThreadsByUserTopics(ThreadRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException("login required");
        }
        Optional<TopicEntity> topicOptional = topicRestService.findByUserUid(user.getUid());
        if (!topicOptional.isPresent()) {
            return Page.empty();
        }

        // 过滤出客服相关主题
        Set<String> customerServiceTopics = topicOptional.get().getTopics().stream()
                .filter(TopicUtils::isCustomerServiceTopic)
                .collect(java.util.stream.Collectors.toSet());

        if (customerServiceTopics.isEmpty()) {
            return Page.empty();
        }

        Pageable pageable = request.getPageable();
        Page<ThreadEntity> threadPage = threadRepository.findByTopicsInAndDeletedFalse(customerServiceTopics, pageable);

        // 对结果按topic进行过滤，每个topic只保留一条记录
        Map<String, ThreadEntity> uniqueThreadsByTopic = new HashMap<>();
        threadPage.getContent().forEach(thread -> {
            uniqueThreadsByTopic.putIfAbsent(thread.getTopic(), thread);
        });

        // 将过滤后的结果转换为Page对象
        List<ThreadEntity> uniqueThreads = new ArrayList<>(uniqueThreadsByTopic.values());
        Page<ThreadEntity> filteredPage = new PageImpl<>(
                uniqueThreads,
                pageable,
                Math.min(uniqueThreads.size(), threadPage.getTotalElements()) // 调整总数
        );

        return filteredPage.map(this::convertToResponse);
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
                .unreadCount(0)
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
                .unreadCount(0)
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
    public ThreadResponse createSystemNoticeAccountThread(UserEntity user) {
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
                .unreadCount(0)
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
        thread.setUnreadCount(threadRequest.getUnreadCount());
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

    public ThreadResponse updateUnreadCount(ThreadRequest threadRequest) {
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
        thread.setUnreadCount(threadRequest.getUnreadCount());
        //
        ThreadEntity updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(updateThread);
    }

    public ThreadResponse increaseUnreadCount(ThreadRequest threadRequest) {
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
        thread.setUnreadCount(threadRequest.getUnreadCount() + 1);
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

    // update state
    public ThreadResponse updateState(ThreadRequest threadRequest) {
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

    public ThreadResponse autoClose(ThreadEntity thread) {
        // log.info(thread.getUid() + "自动关闭");
        ThreadRequest threadRequest = ThreadRequest.builder()
                .uid(thread.getUid())
                .topic(thread.getTopic())
                .autoClose(true)
                .status(ThreadProcessStatusEnum.CLOSED.name())
                .build();
        return close(threadRequest);
    }

    public ThreadResponse close(ThreadRequest request) {
        Optional<ThreadEntity> threadOptional = findByUid(request.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("close thread " + request.getUid() + " not found");
        }
        //
        ThreadEntity thread = threadOptional.get();
        if (ThreadProcessStatusEnum.CLOSED.name().equals(thread.getStatus())) {
            throw new RuntimeException("thread " + thread.getUid() + " is already closed");
        }
        thread.setAutoClose(request.getAutoClose());
        thread.setStatus(request.getStatus());
        // 发布关闭消息, 通知用户
        String content = request.getAutoClose()
                ? I18Consts.I18N_AUTO_CLOSED
                : I18Consts.I18N_AGENT_CLOSED;
        thread.setContent(content);
        //
        ThreadEntity updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        if (Boolean.TRUE.equals(request.getUnsubscribe()) 
            || Boolean.TRUE.equals(request.getAutoClose())) {
            TopicRequest topicRequest = TopicRequest.builder()
                    .topic(request.getTopic())
                    .userUid(request.getUserUid())
                    .build();
            topicRestService.remove(topicRequest);
        }
        // 发布关闭事件
        bytedeskEventPublisher.publishEvent(new ThreadCloseEvent(this, updateThread));
        //
        return convertToResponse(updateThread);
    }

    // 找到第一个未关闭的，执行关闭并返回，用于处理前端传递thread.uid不准确的情况
    public ThreadResponse closeByTopic(ThreadRequest request) {
        UserEntity user = authService.getUser();
        if (user == null) {
            throw new NotLoginException("login required");
        }
        request.setUserUid(user.getUid());

        List<ThreadEntity> threads = findListByTopic(request.getTopic());
        for (ThreadEntity thread : threads) {
            // 找到第一个未关闭的，执行关闭并返回
            if (!thread.isClosed()) {
                thread.setAutoClose(request.getAutoClose());
                thread.setStatus(ThreadProcessStatusEnum.CLOSED.name());
                // 发布关闭消息, 通知用户
                String content = request.getAutoClose()
                        ? I18Consts.I18N_AUTO_CLOSED
                        : I18Consts.I18N_AGENT_CLOSED;
                thread.setContent(content);
                //
                ThreadEntity updateThread = save(thread);
                if (updateThread == null) {
                    throw new RuntimeException("thread save failed");
                }
                if (Boolean.TRUE.equals(request.getUnsubscribe())) {
                    TopicRequest topicRequest = TopicRequest.builder()
                            .topic(request.getTopic())
                            .userUid(request.getUserUid())
                            .build();
                    topicRestService.remove(topicRequest);
                }
                // 发布关闭事件
                bytedeskEventPublisher.publishEvent(new ThreadCloseEvent(this, updateThread));
                //
                return convertToResponse(updateThread);
            } else {
                return convertToResponse(thread);
            }
        }
        return null;
    }

    // 获取当前接待会话数量
    public int countByThreadTopicAndState(String topic, String state) {
        return threadRepository.countByTopicAndStatusAndDeletedFalse(topic, state);
    }

    // count by topic and status not
    public int countByThreadTopicAndStateNot(String topic, String state) {
        return threadRepository.countByTopicAndStatusNotAndDeletedFalse(topic, state);
    }

    // @Cacheable(value = "thread", key = "#uid", unless = "#result == null")
    public Optional<ThreadEntity> findByUid(@NonNull String uid) {
        return threadRepository.findByUid(uid);
    }

    public Boolean existsByUid(@NonNull String uid) {
        return threadRepository.existsByUid(uid);
    }

    @Cacheable(value = "thread", key = "#topic + '-' + #user.uid", unless = "#result == null")
    public Optional<ThreadEntity> findFirstByTopicAndOwner(@NonNull String topic, UserEntity user) {
        return threadRepository.findFirstByTopicAndOwnerAndDeletedOrderByUpdatedAtDesc(topic, user, false);
    }

    // 群聊同一个topic多条会话：IncorrectResultSizeDataAccessException: Query did not return a
    // unique result: 4 results were returned
    @Cacheable(value = "threads", key = "#topic", unless = "#result == null")
    public List<ThreadEntity> findListByTopic(@NonNull String topic) {
        return threadRepository.findByTopicAndDeletedOrderByCreatedAtDesc(topic, false);
    }

    @Cacheable(value = "thread", key = "#topic", unless = "#result == null")
    public Optional<ThreadEntity> findFirstByTopic(@NonNull String topic) {
        return threadRepository.findFirstByTopicAndDeletedOrderByCreatedAtDesc(topic, false);
    }

    // 找到某个访客当前对应某技能组未关闭会话
    @Cacheable(value = "thread", key = "#topic", unless = "#result == null")
    public Optional<ThreadEntity> findFirstByTopicNotClosed(String topic) {
        List<String> states = Arrays.asList(new String[] { ThreadProcessStatusEnum.CLOSED.name() });
        return threadRepository.findTopicAndStatusesNotInAndDeleted(topic, states, false);
    }

    // 根据topic前缀和状态查询会话列表
    public List<ThreadEntity> findByTopicStartsWithAndStatus(String topicPrefix, String status) {
        return threadRepository.findByTopicStartsWithAndStatusAndDeletedFalse(topicPrefix, status);
    }

    @Cacheable(value = "thread", key = "#user.uid + '-' + #pageable.getPageNumber()", unless = "#result == null")
    public Page<ThreadEntity> findByOwner(UserEntity user, Pageable pageable) {
        return threadRepository.findByOwnerAndHideAndDeleted(user, false, false, pageable);
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
        log.info("doSave thread agent: {}, owner: {}", entity.getAgent(), entity.getOwner());
        return threadRepository.save(entity);
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
                latestEntity.setUnreadCount(Math.max(latestEntity.getUnreadCount(), entity.getUnreadCount()));
                latestEntity.setStar(entity.getStar() != null ? entity.getStar() : latestEntity.getStar());
                latestEntity.setTop(entity.getTop() != null ? entity.getTop() : latestEntity.getTop());
                latestEntity.setUnread(entity.getUnread() != null ? entity.getUnread() : latestEntity.getUnread());
                latestEntity.setMute(entity.getMute() != null ? entity.getMute() : latestEntity.getMute());
                latestEntity.setHide(entity.getHide() != null ? entity.getHide() : latestEntity.getHide());
                latestEntity.setFold(entity.getFold() != null ? entity.getFold() : latestEntity.getFold());
                latestEntity.setAutoClose(
                        entity.getAutoClose() != null ? entity.getAutoClose() : latestEntity.getAutoClose());

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

    @CacheEvict(value = "thread", key = "#uid")
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
            @CacheEvict(value = "thread", key = "#thread.uid"),
            @CacheEvict(value = "thread", key = "#thread.topic")
    })
    public void delete(@NonNull ThreadRequest entity) {
        deleteByUid(entity.getUid());
    }

    public ThreadResponse convertToResponse(ThreadEntity thread) {
        return ConvertUtils.convertToThreadResponse(thread);
    }

    @Override
    public ThreadExcel convertToExcel(ThreadEntity entity) {
        ThreadExcel excel = modelMapper.map(entity, ThreadExcel.class);
        excel.setUid(entity.getUid());
        //
        if (entity.getUser() != null) {
            UserProtobuf user = UserProtobuf.fromJson(entity.getUser());
            excel.setVisitorNickname(user.getNickname());
        }
        // agent
        if (entity.getAgent() != null) {
            UserProtobuf agent = UserProtobuf.fromJson(entity.getAgent());
            excel.setAgentNickname(agent.getNickname());
        }
        // robot
        if (entity.getRobot() != null) {
            UserProtobuf robot = UserProtobuf.fromJson(entity.getRobot());
            excel.setRobotNickname(robot.getNickname());
        }
        if (entity.getWorkgroup() != null) {
            UserProtobuf workgroup = UserProtobuf.fromJson(entity.getWorkgroup());
            excel.setWorkgroupNickname(workgroup.getNickname());
        }

        // 将client转换为中文
        if (StringUtils.hasText(entity.getChannel())) {
            excel.setChannel(ChannelEnum.toChineseDisplay(entity.getChannel()));
        }

        // 将status转换为中文
        if (StringUtils.hasText(entity.getStatus())) {
            excel.setStatus(ThreadProcessStatusEnum.toChineseDisplay(entity.getStatus()));
        }

        return excel;
    }

    // 剪贴板会话：clipboard/{user_uid}
    // public ThreadResponse createClipboardAssistantThread(UserEntity user) {
    // //
    // String topic = TopicUtils.getClipboardTopic(user.getUid());
    // //
    // Optional<ThreadEntity> threadOptional = findFirstByTopicAndOwner(topic,
    // user);
    // if (threadOptional.isPresent()) {
    // return convertToResponse(threadOptional.get());
    // }
    // // 剪贴助手用户信息，头像、昵称等
    // UserProtobuf userSimple = UserUtils.getClipboardAssistantUser();
    // ThreadEntity assistantThread = ThreadEntity.builder()
    // .type(ThreadTypeEnum.ASSISTANT.name())
    // .topic(topic)
    // .unreadCount(0)
    // .state(ThreadStateEnum.STARTED.name())
    // .channel(ChannelEnum.SYSTEM.name())
    // .user(JSON.toJSONString(userSimple))
    // .owner(user)
    // .build();
    // assistantThread.setUid(uidUtils.getUid());
    // if (StringUtils.hasText(user.getOrgUid())) {
    // assistantThread.setOrgUid(user.getOrgUid());
    // } else {
    // assistantThread.setOrgUid(BytedeskConsts.DEFAULT_ORGANIZATION_UID);
    // }
    // //
    // ThreadEntity updateThread = save(assistantThread);
    // if (updateThread == null) {
    // throw new RuntimeException("thread save failed");
    // }
    // return convertToResponse(updateThread);
    // }

}
