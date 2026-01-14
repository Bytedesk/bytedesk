/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 13:32:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-16 07:08:31
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.thread;

import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.event.ThreadCreateEvent;
import com.bytedesk.core.thread.event.ThreadRemoveTopicEvent;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;
import com.bytedesk.core.thread.event.ThreadAddTopicEvent;
import com.bytedesk.core.topic.TopicCacheService;
import com.bytedesk.core.topic.TopicRequest;
import com.bytedesk.core.topic.TopicRestService;
import com.bytedesk.core.topic.TopicUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ThreadEventListener {

    private static final Set<String> CREATE_EVENT_IMMEDIATE_SUBSCRIBE_TYPES = Set.of(
            ThreadTypeEnum.AGENT.name(),
            ThreadTypeEnum.WORKGROUP.name(),
            ThreadTypeEnum.MEMBER.name(),
            ThreadTypeEnum.GROUP.name(),
            ThreadTypeEnum.TICKET.name());

    private static final Set<String> ADD_TOPIC_IMMEDIATE_SUBSCRIBE_TYPES = Set.of(
            ThreadTypeEnum.AGENT.name(),
            ThreadTypeEnum.WORKGROUP.name(),
            ThreadTypeEnum.MEMBER.name(),
            ThreadTypeEnum.TICKET.name()
        );

    private final TopicCacheService topicCacheService;

    private final TopicRestService topicRestService;

    private final ActiveThreadCacheService activeThreadCacheService;

    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        ThreadEntity thread = event.getThread();
        if (thread == null) {
            return;
        }
        UserEntity user = thread.getOwner();
        log.info("thread ThreadCreateEvent: {}", thread.getUid());

        // 将服务类型会话添加到活跃会话缓存
        activeThreadCacheService.addOrUpdateActiveThread(thread);

        // 机器人接待的会话存在user == null的情况，不需要订阅topic
        if (user == null) {
            return;
        }

        // 创建客服会话之后，需要订阅topic
        if (CREATE_EVENT_IMMEDIATE_SUBSCRIBE_TYPES.contains(thread.getType())) {
            // 防止首次消息延迟，立即订阅
            String topic = thread.getTopic();
            // 订阅内部会话
            String topicInternal = TopicUtils.formatTopicInternal(topic);
            TopicRequest request = TopicRequest.builder()
                    .userUid(user.getUid())
                    .build();
            request.getTopics().add(topic);
            request.getTopics().add(topicInternal);
            topicRestService.create(request);
        } else {
            // 文件助手、排队助手、系统通知会话延迟订阅topic
            TopicRequest request = TopicRequest.builder()
                    .topic(thread.getTopic())
                    .userUid(user.getUid())
                    .build();
            topicCacheService.pushRequest(request);
        }
    }

    @EventListener
    public void onThreadAddTopicEvent(ThreadAddTopicEvent event) {
        ThreadEntity thread = event.getThread();
        if (thread == null) {
            return;
        }

        UserEntity user = thread.getOwner();
        log.info("thread ThreadUpdateTopicEvent: {}", thread.getUid());

        // 机器人接待的会话存在user == null的情况，不需要订阅topic
        if (user == null) {
            return;
        }

        TopicRequest request = TopicRequest.builder()
                .topic(thread.getTopic())
                .userUid(user.getUid())
                .build();

        // AGENT/WORKGROUP/MEMBER 立即订阅；其他延迟订阅
        if (ADD_TOPIC_IMMEDIATE_SUBSCRIBE_TYPES.contains(thread.getType())) {
            topicRestService.create(request);
        } else {
            topicCacheService.pushRequest(request);
        }
    }

    @EventListener
    public void onThreadRemoveTopicEvent(ThreadRemoveTopicEvent event) {
        ThreadEntity thread = event.getThread();
        UserEntity user = thread.getOwner();
        // UserEntity user = thread.getOwner();
        log.info("thread ThreadRemoveTopicEvent: {}", thread.getUid());
        TopicRequest request = TopicRequest.builder()
                    .topic(thread.getTopic())
                    .userUid(user.getUid())
                    .build();
        topicRestService.remove(request);
    }

}
