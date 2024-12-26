/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 07:51:39
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-26 11:01:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

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
import com.bytedesk.service.queue_member.event.QueueMemberCreateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class QueueMemberEventListener {

    private final QueueMemberRestService queueMemberRestService;

    @EventListener
    public void onQueueMemberCreateEvent(QueueMemberCreateEvent event) {
        log.info("queue member create event ");
    }    

    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        ThreadEntity thread = event.getThread();
        // UserEntity user = thread.getOwner();
        log.info("queue member onThreadCreateEvent: {}", thread.getUid());
    }

    @EventListener
    public void onThreadUpdateEvent(ThreadUpdateEvent event) {
        ThreadEntity thread = event.getThread();
        // UserEntity user = thread.getOwner();
        log.info("queue member onThreadUpdateEvent: {}", thread.getUid());
    }

    @EventListener
    public void onThreadCloseEvent(ThreadCloseEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("queue member onThreadCloseEvent: {}", thread.getAgent());
    }

    @EventListener
    public void onThreadAcceptEvent(ThreadAcceptEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("queue member onThreadAcceptEvent: {}", thread.getAgent());
        String queueTopic = TopicUtils.getQueueTopicFromThreadTopic(thread.getTopic());
        log.info("queue onThreadAcceptEvent: {}", queueTopic);
        String queueDay = thread.getCreatedAt().format(DateTimeFormatter.ISO_DATE);
        Optional<QueueMemberEntity> memberOptional = queueMemberRestService.findByQueueTopicAndQueueDayAndThreadUidAndStatus(
            queueTopic, 
            queueDay, 
            thread.getUid(), 
            QueueMemberStatusEnum.WAITING.name()
        );
        if (memberOptional.isPresent()) {
            QueueMemberEntity member = memberOptional.get();
            member.setStatus(QueueMemberStatusEnum.SERVING.name());
            queueMemberRestService.save(member);
        } else {
            log.error("queue member onThreadAcceptEvent: member not found: {}", thread.getUid());
        }
    }
}
