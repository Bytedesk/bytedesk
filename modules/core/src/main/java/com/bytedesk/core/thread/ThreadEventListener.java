/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 13:32:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-09 16:45:38
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
import org.springframework.stereotype.Component;

import com.bytedesk.core.quartz.event.QuartzDay0Event;
import com.bytedesk.core.rbac.user.UserEntity;
import com.bytedesk.core.thread.event.ThreadCreateEvent;
import com.bytedesk.core.thread.event.ThreadRemoveTopicEvent;
import com.bytedesk.core.thread.event.ThreadAddTopicEvent;
import com.bytedesk.core.thread.event.ThreadCloseEvent;
import com.bytedesk.core.topic.TopicCacheService;
import com.bytedesk.core.topic.TopicRequest;
import com.bytedesk.core.topic.TopicRestService;
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

    private final TopicRestService topicRestService;

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
                || thread.getType().equals(ThreadTypeEnum.GROUP.name())
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
    public void onThreadAddTopicEvent(ThreadAddTopicEvent event) {
        ThreadEntity thread = event.getThread();
        UserEntity user = thread.getOwner();
        log.info("thread ThreadUpdateTopicEvent: {}", thread.getUid());
        // if (thread.isClosed()) {
        // return;
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
            // 重新订阅
            TopicRequest request = TopicRequest.builder()
                    .topic(thread.getTopic())
                    .userUid(user.getUid())
                    .build();
            topicService.create(request);
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

    @EventListener
    public void onThreadCloseEvent(ThreadCloseEvent event) {
        ThreadEntity thread = event.getThread();
        // UserEntity user = thread.getOwner();
        log.info("ThreadEventListener onThreadCloseEvent: {}", thread.getUid());
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

    @EventListener
    public void onQuartzDay0Event(QuartzDay0Event event) {
        log.info("onQuartzDay0Event: {}", event);
    }

}
