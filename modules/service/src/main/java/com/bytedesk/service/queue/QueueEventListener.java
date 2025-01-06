/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 22:19:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-26 12:53:52
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.event.ThreadAcceptEvent;
import com.bytedesk.core.thread.event.ThreadCloseEvent;
import com.bytedesk.core.thread.event.ThreadCreateEvent;
import com.bytedesk.core.thread.event.ThreadUpdateEvent;
import com.bytedesk.core.topic.TopicUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class QueueEventListener {

    private final QueueRestService queueRestService;
    // private final IMessageSendService messageSendService;

    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        ThreadEntity thread = event.getThread();
        // UserEntity user = thread.getOwner();
        log.info("queue onThreadCreateEvent: {}", thread.getUid());
    }

    @EventListener
    public void onThreadUpdateEvent(ThreadUpdateEvent event) {
        ThreadEntity thread = event.getThread();
        // UserEntity user = thread.getOwner();
        log.info("queue onThreadUpdateEvent: {}", thread.getUid());
    }

    @EventListener
    public void onThreadCloseEvent(ThreadCloseEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("queue onThreadCloseEvent: {}", thread.getAgent());
        String queueTopic = TopicUtils.getQueueTopicFromThreadTopic(thread.getTopic());
        log.info("queue onThreadAcceptEvent: {}", queueTopic);
        String queueDay = thread.getCreatedAt().format(DateTimeFormatter.ISO_DATE);
        Optional<QueueEntity> queueOptional = queueRestService.findByTopicAndDay(queueTopic, queueDay);
        if (queueOptional.isPresent()) {
            QueueEntity queue = queueOptional.get();
            // 增加已完成人数
            queue.increaseServedNumber();
            queueRestService.save(queue);
        } else {
            log.error("queue onThreadAcceptEvent: queue not found: {}", queueTopic);
        }
    }
    

    @EventListener
    public void onThreadAcceptEvent(ThreadAcceptEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("queue onThreadAcceptEvent: {}", thread.getTopic());
        String queueTopic = TopicUtils.getQueueTopicFromThreadTopic(thread.getTopic());
        log.info("queue onThreadAcceptEvent: {}", queueTopic);
        String queueDay = thread.getCreatedAt().format(DateTimeFormatter.ISO_DATE);
        Optional<QueueEntity> queueOptional = queueRestService.findByTopicAndDay(queueTopic, queueDay);
        if (queueOptional.isPresent()) {
            QueueEntity queue = queueOptional.get();
            // 减少排队人数，增加接入人数
            queue.acceptThread();
            queueRestService.save(queue);
        } else {
            log.error("queue onThreadAcceptEvent: queue not found: {}", queueTopic);
        }

    }
}
