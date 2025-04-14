/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 13:32:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 15:37:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.event.ThreadCreateEvent;
import com.bytedesk.core.thread.event.ThreadUpdateEvent;
import com.bytedesk.core.topic.TopicCacheService;
import com.bytedesk.core.topic.TopicRequest;
import com.bytedesk.core.topic.TopicService;
import com.bytedesk.core.topic.TopicUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ThreadEventListener {

    private final TopicService topicService;

    private final TopicCacheService topicCacheService;

    private final ThreadRestService threadRestService;

    @Order(3)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        String orgUid = organization.getUid();
        log.info("thread - organization created: {}", organization.getName());
        threadRestService.initThreadCategory(orgUid);
        threadRestService.initThreadTag(orgUid);
    }

    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        ThreadEntity thread = event.getThread();
        UserEntity user = thread.getOwner();
        log.info("thread ThreadCreateEvent: {}", thread.getUid());

        // 机器人接待的会话存在user == null的情况，不需要订阅topic
        if (user == null) {
            return;
        }

        // 创建客服会话之后，需要订阅topic
        if (thread.getType().equals(ThreadTypeEnum.AGENT.name())
                || thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())
                || thread.getType().equals(ThreadTypeEnum.MEMBER.name())
                || thread.getType().equals(ThreadTypeEnum.TICKET.name())) {
            // 防止首次消息延迟，立即订阅
            String topic = thread.getTopic();
            // 订阅内部会话
            String topicInternal = TopicUtils.formatTopicInternal(topic);
            TopicRequest request = TopicRequest.builder()
                    .userUid(user.getUid())
                    .build();
            request.getTopics().add(topic);
            request.getTopics().add(topicInternal);
            topicService.create(request);
        } else {
            // 文件助手、系统通知会话延迟订阅topic
            TopicRequest request = TopicRequest.builder()
                    .topic(thread.getTopic())
                    .userUid(user.getUid())
                    .build();
            topicCacheService.pushRequest(request);
        }
    }

    @EventListener
    public void onThreadUpdateEvent(ThreadUpdateEvent event) {
        ThreadEntity thread = event.getThread();
        UserEntity user = thread.getOwner();
        log.info("thread onThreadUpdateEvent: {}", thread.getUid());
        // if (thread.isClosed()) {
        //     return;
        // }

        // 机器人接待的会话存在user == null的情况，不需要订阅topic
        if (thread == null || user == null) {
            return;
        }

        if (thread.getType().equals(ThreadTypeEnum.AGENT.name())) {
            // 防止首次消息延迟，立即订阅
            TopicRequest request = TopicRequest.builder()
                    .topic(thread.getTopic())
                    .userUid(user.getUid())
                    .build();
            topicService.create(request);
        } else if (thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
            // 工作组会话，需要订阅topic

            // 判断状态，如果关闭，则取消订阅
            // if (thread.isClosed()) {
                // TopicRequest request = TopicRequest.builder()
                    // .topic(thread.getTopic())
                    // .userUid(user.getUid())
                    // .build();
                // topicService.remove(request);
            // } else {
            // 重新订阅
            TopicRequest request = TopicRequest.builder()
                    .topic(thread.getTopic())
                    .userUid(user.getUid())
                    .build();
            topicService.create(request);
            // }
        } else if (thread.getType().equals(ThreadTypeEnum.MEMBER.name())) {
            // 会员会话，需要订阅topic
            TopicRequest request = TopicRequest.builder()
                    .topic(thread.getTopic())
                    .userUid(user.getUid())
                    .build();
            topicService.create(request);
        } else {
            // 文件助手、系统通知会话延迟订阅topic
            TopicRequest request = TopicRequest.builder()
                    .topic(thread.getTopic())
                    .userUid(user.getUid())
                    .build();
            topicCacheService.pushRequest(request);
        }
    }

    // @EventListener
    // public void onThreadCloseEvent(ThreadCloseEvent event) {
    // ThreadEntity thread = event.getThread();
    // log.info("thread event listener onThreadCloseEvent: {}", thread.getAgent());
    // //
    // if (thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
    // TODO：工作组会话，会话关闭后，需要取消订阅topic
    // if (thread.isClosed() && thread.getOwner() != null) {
    // // 取消订阅
    // TopicRequest request = TopicRequest.builder()
    // .topic(thread.getTopic())
    // .userUid(thread.getOwner().getUid())
    // .build();
    // topicService.remove(request);
    // }
    // }
    // }

    // @EventListener
    // public void onMessageCreateEvent(MessageCreateEvent event) {
    // MessageEntity message = event.getMessage();
    // if (message.getType().equalsIgnoreCase(MessageTypeEnum.STREAM.name())) {
    // return;
    // }
    // Optional<Thread> threadOptional =
    // threadService.findFirstByTopic(message.getThreadTopic());
    // if (threadOptional.isPresent()) {
    // Thread thread = threadOptional.get();
    // thread.setHide(false);
    // thread.setContent(message.getContent());
    // // threadService.save(thread);
    // threadPersistCache.pushForPersist(thread);
    // }
    // }

    // @EventListener
    // public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
    // List<ThreadEntity> threadList = threadPersistCache.getListForPersist();
    // if (threadList != null) {
    // threadList.forEach(thread -> {
    // threadService.save(thread);
    // });
    // }
    // }

    // @EventListener
    // public void onUserUpdateEvent(UserUpdateEvent event) {
    // // todo: on user avatar update, update thread entity user avatar
    // // update member thread avatar
    // }

}
