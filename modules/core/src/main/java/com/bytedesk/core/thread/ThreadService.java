/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:21:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-23 11:14:44
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

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.asistant.Asistant;
import com.bytedesk.core.channel.Channel;
import com.bytedesk.core.constant.AvatarConsts;
import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.constant.TopicConsts;
import com.bytedesk.core.constant.UserConsts;
import com.bytedesk.core.enums.ClientEnum;
import com.bytedesk.core.rbac.auth.AuthService;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.rbac.user.UserResponseSimple;
import com.bytedesk.core.rbac.user.UserService;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.ConvertUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ThreadService {

    private AuthService authService;

    private ModelMapper modelMapper;

    private UserService userService;

    private ThreadRepository threadRepository;

    private UidUtils uidUtils;

    public Page<ThreadResponse> queryByOrg(ThreadRequest request) {

        // 优先加载最近更新的会话记录，updatedAt越大越新
        Pageable pageable = PageRequest.of(request.getPageNumber(), request.getPageSize(), Sort.Direction.DESC,
                "updatedAt");

        Specification<Thread> specs = ThreadSpecification.search(request);
        Page<Thread> threadPage = threadRepository.findAll(specs, pageable);
        // Page<Thread> threadPage =
        // threadRepository.findByOrgUidAndDeleted(pageParam.getOrgUid(), false,
        // pageable);

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

    /** */
    public Thread getReverse(Thread thread) {

        String reverseTid = new StringBuffer(thread.getUid()).reverse().toString();
        Optional<Thread> reverseThreadOptional = findByUid(reverseTid);
        if (reverseThreadOptional.isPresent()) {
            reverseThreadOptional.get().setContent(thread.getContent());
            return save(reverseThreadOptional.get());
        } else {
            Optional<User> userOptional = userService.findByUid(thread.getTopic());
            if (!userOptional.isPresent()) {
                return null;
            }
            Thread reverseThread = new Thread();
            reverseThread.setUid(reverseTid);
            reverseThread.setTopic(thread.getOwner().getUid());
            //
            UserResponseSimple user = ConvertUtils.convertToUserResponseSimple(thread.getOwner());
            reverseThread.setUser(JSON.toJSONString(user));
            // reverseThread.setUser(thread.getOwner());
            //
            reverseThread.setContent(thread.getContent());
            // reverseThread.setExtra(thread.getExtra());
            reverseThread.setType(thread.getType());
            reverseThread.setOwner(userOptional.get());
            //
            return save(reverseThread);
        }

    }

    /**
     * create member thread
     * 
     * @param threadRequest
     * @return
     */
    public ThreadResponse createMemberThread(ThreadRequest threadRequest) {

        User owner = authService.getCurrentUser();
        //
        Optional<Thread> threadOptional = findByTopicAndOwner(threadRequest.getTopic(), owner);
        if (threadOptional.isPresent()) {
            return convertToResponse(threadOptional.get());
        }
        //
        Thread thread = modelMapper.map(threadRequest, Thread.class);
        thread.setUid(uidUtils.getCacheSerialUid());
        thread.setStatus(ThreadStatusEnum.OPEN);
        //
        String user = JSON.toJSONString(threadRequest.getUser());
        log.info("request {}, user {}", threadRequest.toString(), user);
        thread.setUser(user);
        //
        thread.setOwner(owner);
        thread.setOrgUid(owner.getOrgUid());
        //
        Thread result = save(thread);
        if (result == null) {
            throw new RuntimeException("thread save failed");
        }
        //
        return convertToResponse(result);
    }

    /** */
    public Thread createFileAsistantThread(User user, Asistant asistant) {

        UserResponseSimple userSimple = UserResponseSimple.builder()
                // .uid(UserConsts.DEFAULT_FILE_ASISTANT_UID)
                .nickname(I18Consts.I18N_FILE_ASISTANT_NAME)
                .avatar(AvatarConsts.DEFAULT_FILE_ASISTANT_AVATAR_URL)
                .build();
        //
        Thread thread = Thread.builder()
                // .tid(uidUtils.getCacheSerialUid())
                // .type(ThreadTypeConsts.ASISTANT)
                .type(ThreadTypeEnum.ASISTANT)
                .topic(TopicConsts.TOPIC_FILE_ASISTANT + "/" + user.getUid())
                // .status(StatusConsts.THREAD_STATUS_INIT)
                .status(ThreadStatusEnum.OPEN)
                // .client(TypeConsts.TYPE_SYSTEM)
                .client(ClientEnum.SYSTEM)
                .user(JSON.toJSONString(userSimple))
                // .user(userSimple)
                // .user(asistant)
                .owner(user)
                // .orgUid(user.getOrgUid())
                .build();
        thread.setUid(uidUtils.getCacheSerialUid());
        thread.setOrgUid(user.getOrgUid());

        return save(thread);
    }

    public Thread createSystemNotificationChannelThread(User user, Channel channel) {

        UserResponseSimple userSimple = UserResponseSimple.builder()
                .nickname(I18Consts.I18N_SYSTEM_NOTIFICATION_NAME)
                .avatar(AvatarConsts.DEFAULT_SYSTEM_NOTIFICATION_AVATAR_URL)
                .build();
        userSimple.setUid(UserConsts.DEFAULT_SYSTEM_NOTIFICATION_UID);
        //
        Thread thread = Thread.builder()
                // .tid(uidUtils.getCacheSerialUid())
                // .type(ThreadTypeConsts.CHANNEL)
                .type(ThreadTypeEnum.CHANNEL)
                .topic(TopicConsts.TOPIC_SYSTEM_NOTIFICATION + "/" + user.getUid())
                // .status(StatusConsts.THREAD_STATUS_INIT)
                .status(ThreadStatusEnum.OPEN)
                // .client(TypeConsts.TYPE_SYSTEM)
                .client(ClientEnum.SYSTEM)
                .user(JSON.toJSONString(userSimple))
                // .user(channel)
                .owner(user)
                // .orgUid(user.getOrgUid())
                .build();
        thread.setUid(uidUtils.getCacheSerialUid());
        thread.setOrgUid(user.getOrgUid());

        return save(thread);
    }

    public ThreadResponse update(ThreadRequest threadRequest) {
        String uid = threadRequest.getUid();
        Optional<Thread> threadOptional = findByUid(uid);
        //
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("thread not found");
        }
        //
        Thread thread = threadOptional.get();
        thread.setStatus(threadRequest.getStatus());
        //
        Thread updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }
        return convertToResponse(updateThread);
    }

    public ThreadResponse close(ThreadRequest threadRequest) {
        String uid = threadRequest.getUid();
        Optional<Thread> threadOptional = findByUid(uid);
        //
        if (!threadOptional.isPresent()) {
            throw new RuntimeException("thread not found");
        }
        //
        Thread thread = threadOptional.get();
        //
        if (ThreadStatusEnum.AGENT_CLOSED.equals(thread.getStatus())
                || ThreadStatusEnum.AUTO_CLOSED.equals(thread.getStatus())) {
            log.info("thread {} is already closed", uid);
            throw new RuntimeException("thread is already closed");
        }
        //
        thread.setStatus(ThreadStatusEnum.AGENT_CLOSED);
        //
        Thread updateThread = save(thread);
        if (updateThread == null) {
            throw new RuntimeException("thread save failed");
        }

        return convertToResponse(updateThread);
    }

    @Cacheable(value = "thread", key = "#uid", unless = "#result == null")
    public Optional<Thread> findByUid(String uid) {
        return threadRepository.findByUid(uid);
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
        return threadRepository.findByOwnerAndDeleted(user, false, pageable);
    }

    // TODO: 更新缓存
    @Cacheable(value = "threadOpen")
    public List<Thread> findStatusOpen() {
        // return threadRepository.findByStatus(StatusConsts.THREAD_STATUS_OPEN);
        return threadRepository.findByStatusAndDeleted(ThreadStatusEnum.OPEN, false);
    }

    public Boolean isClosed(Thread thread) {
        // return !StatusConsts.THREAD_STATUS_OPEN.equals(thread.getStatus());
        return !ThreadStatusEnum.OPEN.equals(thread.getStatus());
    }

    public Thread reopen(Thread thread) {
        // thread.setStatus(StatusConsts.THREAD_STATUS_OPEN);
        thread.setStatus(ThreadStatusEnum.OPEN);
        return save(thread);
    }

    public Thread autoClose(Thread thread) {
        // thread.setStatus(StatusConsts.THREAD_STATUS_CLOSED_AUTO);
        thread.setStatus(ThreadStatusEnum.AUTO_CLOSED);
        return save(thread);
    }

    // public Thread agentClose(Thread thread) {
    // // thread.setStatus(StatusConsts.THREAD_STATUS_CLOSED_AGENT);
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
        } catch (ObjectOptimisticLockingFailureException e) {
            // TODO: handle exception
            e.printStackTrace();
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
        UserResponseSimple user = JSON.parseObject(thread.getUser(), UserResponseSimple.class);
        threadResponse.setUser(user);

        return threadResponse;
    }

    //
    public void initData() {

        if (threadRepository.count() > 0) {
            return;
        }

    }

}
