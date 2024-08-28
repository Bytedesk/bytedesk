/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-26 06:57:09
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.base.BaseService;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.constant.BdConstants;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;

import io.jsonwebtoken.lang.Arrays;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ThreadService extends BaseService<Thread, ThreadRequest, ThreadResponse> {

    private AuthService authService;

    private ModelMapper modelMapper;

    private ThreadRepository threadRepository;

    private UidUtils uidUtils;

    public Page<ThreadResponse> queryByOrg(ThreadRequest request) {

        // 优先加载最近更新的会话记录，updatedAt越大越新
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");

        Specification<Thread> specs = ThreadSpecification.search(request);

        Page<Thread> threadPage = threadRepository.findAll(specs, pageable);

        return threadPage.map(this::convertToResponse);
    }

    /**  */
    public Page<ThreadResponse> query(ThreadRequest pageParam) {

        User user = authService.getCurrentUser();

        // 优先加载最近更新的会话记录，updatedAt越大越新
        Pageable pageable = PageRequest.of(pageParam.getPageNumber(), pageParam.getPageSize(), Sort.Direction.DESC,
                "updatedAt");

        Page<Thread> threadPage = findByOwner(user, pageable);

        return threadPage.map(this::convertToResponse);
    }

    /**
     * create thread
     * 同事会话、机器人会话、群聊会话
     * 
     * @{TopicUtils}
     * 
     * @param request
     * @return
     */
    public ThreadResponse create(ThreadRequest request) {

        User owner = authService.getCurrentUser();
        //
        Optional<Thread> threadOptional = findByTopicAndOwner(request.getTopic(), owner);
        if (threadOptional.isPresent()) {
            return convertToResponse(threadOptional.get());
        }
        //
        Thread thread = modelMapper.map(request, Thread.class);
        thread.setUid(uidUtils.getCacheSerialUid());
        thread.setStatus(ThreadStatusEnum.NORMAL.name());
        //
        String user = JSON.toJSONString(request.getUser());
        log.info("request {}, user {}", request.toString(), user);
        thread.setUser(user);
        //
        thread.setClient(ClientEnum.fromValue(request.getClient()).name());
        thread.setOwner(owner);
        thread.setOrgUid(owner.getOrgUid());
        
        //
        Thread savedThread = save(thread);
        if (savedThread == null) {
            throw new RuntimeException("thread save failed");
        }
        //
        return convertToResponse(savedThread);
    }
    
    // 在group会话创建之后，自动为group成员members创建会话
    // 同事群组会话：org/group/{group_uid}
    public ThreadResponse createGroupMemberThread(Thread thread, User owner) {
        //
        Optional<Thread> threadOptional = findByTopicAndOwner(thread.getTopic(), owner);
        if (threadOptional.isPresent()) {
            return convertToResponse(threadOptional.get());
        }

        Thread groupThread = Thread.builder()
                .type(thread.getType())
                .topic(thread.getTopic())
                .unreadCount(0)
                .status(thread.getStatus())
                .client(ClientEnum.SYSTEM.name())
                .user(thread.getUser())
                .owner(owner)
                .build();
        groupThread.setUid(uidUtils.getCacheSerialUid());
        groupThread.setOrgUid(thread.getOrgUid());

        Thread updateThread = save(groupThread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }

        return convertToResponse(updateThread);
    }

    /** 文件助手会话：file/{user_uid} */
    public ThreadResponse createFileAsistantThread(User user) {
        // 
        String topic = TopicUtils.TOPIC_FILE_PREFIX + user.getUid();
        //
        Optional<Thread> threadOptional = findByTopicAndOwner(topic, user);
        if (threadOptional.isPresent()) {
            return convertToResponse(threadOptional.get());
        }

        UserProtobuf userSimple = UserProtobuf.builder()
                .nickname(I18Consts.I18N_FILE_ASISTANT_NAME)
                .avatar(AvatarConsts.DEFAULT_FILE_ASISTANT_AVATAR_URL)
                .build();
        userSimple.setUid(BdConstants.DEFAULT_FILE_ASISTANT_UID);
        //
        Thread asistantThread = Thread.builder()
                .type(ThreadTypeEnum.ASISTANT.name())
                .topic(topic)
                .unreadCount(0)
                .status(ThreadStatusEnum.NORMAL.name())
                .client(ClientEnum.SYSTEM.name())
                .user(JSON.toJSONString(userSimple))
                .owner(user)
                .build();
        asistantThread.setUid(uidUtils.getCacheSerialUid());
        if (StringUtils.hasText(user.getOrgUid())) {
            asistantThread.setOrgUid(user.getOrgUid());
        } else {
            asistantThread.setOrgUid(BdConstants.DEFAULT_ORGANIZATION_UID);
        }

        Thread updateThread = save(asistantThread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }

        return convertToResponse(updateThread);
    }

    // 系统通知会话：system/{user_uid}
    public ThreadResponse createSystemChannelThread(User user) {
        // 
        String topic = TopicUtils.TOPIC_SYSTEM_PREFIX + user.getUid();
        //
        Optional<Thread> threadOptional = findByTopicAndOwner(topic, user);
        if (threadOptional.isPresent()) {
            return convertToResponse(threadOptional.get());
        }

        UserProtobuf userSimple = UserProtobuf.builder()
                .nickname(I18Consts.I18N_SYSTEM_NOTIFICATION_NAME)
                .avatar(AvatarConsts.DEFAULT_SYSTEM_NOTIFICATION_AVATAR_URL)
                .build();
        userSimple.setUid(BdConstants.DEFAULT_SYSTEM_UID);
        //
        Thread noticeThread = Thread.builder()
                .type(ThreadTypeEnum.CHANNEL.name())
                .topic(topic)
                .unreadCount(0)
                .status(ThreadStatusEnum.NORMAL.name())
                .client(ClientEnum.SYSTEM.name())
                .user(JSON.toJSONString(userSimple))
                .owner(user)
                .build();
        noticeThread.setUid(uidUtils.getCacheSerialUid());
        if (StringUtils.hasText(user.getOrgUid())) {
            noticeThread.setOrgUid(user.getOrgUid());
        } else {
            noticeThread.setOrgUid(BdConstants.DEFAULT_ORGANIZATION_UID);
        }
        // 
        Thread updateThread = save(noticeThread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }

        return convertToResponse(updateThread);
    }

    public ThreadResponse update(ThreadRequest threadRequest) {
        Optional<Thread> threadOptional = findByUid(threadRequest.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("thread not found");
        }
        //
        Thread thread = threadOptional.get();
        thread.setTop(threadRequest.getTop());
        thread.setUnread(threadRequest.getUnread());
        thread.setUnreadCount(threadRequest.getUnreadCount());
        thread.setMute(threadRequest.getMute());
        thread.setHide(threadRequest.getHide());
        thread.setStar(threadRequest.getStar());
        thread.setFolded(threadRequest.getFolded());
        thread.setContent(threadRequest.getContent());
        //
        Thread updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(updateThread);
    }

    public ThreadResponse close(ThreadRequest threadRequest) {
        Optional<Thread> threadOptional = findByUid(threadRequest.getUid());
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("thread not found");
        }
        //
        Thread thread = threadOptional.get();
        //
        if (ThreadStatusEnum.AGENT_CLOSED.equals(thread.getStatus())
                || ThreadStatusEnum.AUTO_CLOSED.equals(thread.getStatus())) {
            // log.info("thread {} is already closed", uid);
            throw new RuntimeException("thread is already closed");
        }
        thread.setStatus(ThreadStatusEnum.AGENT_CLOSED.name());
        //
        Thread updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        //
        return convertToResponse(updateThread);
    }

    // 群组解散之后，将所有会话设置为已解散状态
    public void dismissByTopic(String topic) {
        List<Thread> threads = threadRepository.findByTopic(topic);
        if (threads == null || threads.isEmpty()) {
            return;
        }
        Iterator<Thread> iterator = threads.iterator();
        while (iterator.hasNext()) {
            Thread thread = iterator.next();
            thread.setStatus(ThreadStatusEnum.DISMISSED.name());
            //
            save(thread);
        }
    }

    @Cacheable(value = "thread", key = "#uid", unless = "#result == null")
    public Optional<Thread> findByUid(String uid) {
        return threadRepository.findByUid(uid);
    }

    public Boolean existsByUid(String uid) {
        return threadRepository.existsByUid(uid);
    }

    // TODO: how to cacheput or cacheevict?
    @Cacheable(value = "thread", key = "#topic-#user.uid", unless = "#result == null")
    public Optional<Thread> findByTopicAndOwner(String topic, User user) {
        return threadRepository.findFirstByTopicAndOwnerAndDeleted(topic, user, false);
    }

    @Cacheable(value = "thread", key = "#topic", unless = "#result == null")
    public Optional<Thread> findByTopic(String topic) {
        return threadRepository.findFirstByTopicAndDeleted(topic, false);
    }

    // TODO: how to cacheput or cacheevict?
    @Cacheable(value = "thread", key = "#user.uid-#pageable.getPageNumber()", unless = "#result == null")
    public Page<Thread> findByOwner(User user, Pageable pageable) {
        return threadRepository.findByOwnerAndHideAndDeleted(user, false, false, pageable);
    }

    // TODO: 更新缓存
    // @Cacheable(value = "threadOpen")
    public List<Thread> findStatusOpen() {
        List<String> statuses = Arrays
                .asList(new String[] { ThreadStatusEnum.NORMAL.name(), ThreadStatusEnum.CONTINUE.name() });
        return threadRepository.findByStatusesAndDeleted(statuses, false);
    }

    public Boolean isClosed(Thread thread) {
        return ThreadStatusEnum.AGENT_CLOSED.equals(thread.getStatus())
                || ThreadStatusEnum.AUTO_CLOSED.equals(thread.getStatus());
    }

    // public Thread reenter(Thread thread) {
    // if (thread.getType().equals(ThreadTypeEnum.AGENT)
    // || thread.getType().equals(ThreadTypeEnum.WORKGROUP)) {
    // thread.setUnreadCount(1);
    // }
    // thread.setStatus(ThreadStatusEnum.REENTER);
    // return save(thread);
    // }

    public Thread autoClose(Thread thread) {
        thread.setStatus(ThreadStatusEnum.AUTO_CLOSED.name());
        return save(thread);
    }

    // public Thread agentClose(Thread thread) {
    // thread.setStatus(ThreadStatusEnum.AGENT_CLOSED);
    // return save(thread);
    // }

    @Caching(put = {
            @CachePut(value = "thread", key = "#thread.uid"),
            @CachePut(value = "thread", key = "#thread.topic")
    })
    public Thread save(@NonNull Thread thread) {
        try {
            return threadRepository.save(thread);
        } catch (Exception e) {
            e.printStackTrace();
            // handleOptimisticLockingFailureException(e, thread);
        }
        return null;
    }

    @Caching(evict = {
            @CacheEvict(value = "thread", key = "#thread.uid"),
            @CacheEvict(value = "thread", key = "#thread.topic")
    })
    public void delete(@NonNull Thread entity) {
        // threadRepository.delete(thread);
        Optional<Thread> threadOptional = findByUid(entity.getUid());
        threadOptional.ifPresent(thread -> {
            thread.setDeleted(true);
            save(thread);
        });
    }

    public ThreadResponse convertToResponse(Thread thread) {
        ThreadResponse threadResponse = modelMapper.map(thread, ThreadResponse.class);
        //
        UserProtobuf user = JSON.parseObject(thread.getUser(), UserProtobuf.class);
        threadResponse.setUser(user);

        return threadResponse;
    }

    //
    public void initData() {

        if (threadRepository.count() > 0) {
            return;
        }

    }

    @Override
    public Page<ThreadResponse> queryByUser(ThreadRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'queryByUser'");
    }

    @Override
    public void deleteByUid(String uid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteByUid'");
    }

    @Override
    public void handleOptimisticLockingFailureException(ObjectOptimisticLockingFailureException e, Thread entity) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleOptimisticLockingFailureException'");
    }

}
