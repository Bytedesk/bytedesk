/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 13:32:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-28 19:49:29
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

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.message.Message;
import com.bytedesk.core.message.MessageCreateEvent;
import com.bytedesk.core.message.MessageTypeEnum;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
import com.bytedesk.core.rbac.user.User;
import com.bytedesk.core.topic.TopicCacheService;
import com.bytedesk.core.topic.TopicRequest;
import com.bytedesk.core.topic.TopicService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ThreadEventListener {

    private final TopicService topicService;

    private final TopicCacheService topicCacheService;

    private final ThreadService threadService;

    private final ThreadPersistCache threadPersistCache;

    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        Thread thread = event.getThread();
        User user = thread.getOwner();
        log.info("thread ThreadCreateEvent: {}", thread.getUid());

        // 机器人接待的会话存在user == null的情况，不需要订阅topic
        if (user == null) {
            return;
        }
        
        // 创建客服会话之后，需要订阅topic
        if (thread.getType().equals(ThreadTypeEnum.AGENT.name())
                || thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
            // 防止首次消息延迟，立即订阅
            TopicRequest request = TopicRequest.builder()
                    .topic(thread.getTopic())
                    .userUid(user.getUid())
                    .build();
            topicService.create(request);
        } else if (thread.getType().equals(ThreadTypeEnum.MEMBER.name())
                || thread.getType().equals(ThreadTypeEnum.ASISTANT.name())
                || thread.getType().equals(ThreadTypeEnum.CHANNEL.name())) {
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
        Thread thread = event.getThread();
        User user = thread.getOwner();
        log.info("topic onThreadUpdateEvent: {}", thread.getUid());
        // TODO: 会话关闭之后，需要取消订阅
        
        // 机器人接待的会话存在user == null的情况，不需要订阅topic
        if (user == null) {
            return;
        }
        
        if (thread.getType().equals(ThreadTypeEnum.AGENT.name())
                || thread.getType().equals(ThreadTypeEnum.WORKGROUP.name())) {
            // 防止首次消息延迟，立即订阅
            TopicRequest request = TopicRequest.builder()
                    .topic(thread.getTopic())
                    .userUid(user.getUid())
                    .build();
            topicService.create(request);
        } else if (thread.getType().equals(ThreadTypeEnum.MEMBER.name())
                || thread.getType().equals(ThreadTypeEnum.ASISTANT.name())
                || thread.getType().equals(ThreadTypeEnum.CHANNEL.name())) {
            // 文件助手、系统通知会话延迟订阅topic
            TopicRequest request = TopicRequest.builder()
                    .topic(thread.getTopic())
                    .userUid(user.getUid())
                    .build();
            topicCacheService.pushRequest(request);
        } else if (user != null) {
            TopicRequest request = TopicRequest.builder()
                    .topic(thread.getTopic())
                    .userUid(user.getUid())
                    .build();
            // 防止首次消息延迟，立即订阅
            topicService.create(request);
        }
    }

    @EventListener
    public void onMessageCreateEvent(MessageCreateEvent event) {
        Message message = event.getMessage();
        if (message.getType().equals(MessageTypeEnum.STREAM.name())) {
            return;
        }
        // log.info("robot message unread create event: " + event);
        Optional<Thread> threadOptional = threadService.findByTopic(message.getThreadTopic());
        if (threadOptional.isPresent()) {
            Thread thread = threadOptional.get();
            thread.setHide(false);
            thread.setContent(message.getContent());
            // threadService.save(thread);
            threadPersistCache.pushForPersist(thread);
        }
    }

    @EventListener
    public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
        List<Thread> threadList = threadPersistCache.getListForPersist();
        if (threadList != null) {
            threadList.forEach(thread -> {
                threadService.save(thread);
            });
        }

    }
}
