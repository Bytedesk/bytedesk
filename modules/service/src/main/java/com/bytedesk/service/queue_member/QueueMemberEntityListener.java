/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-18 07:52:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 15:27:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.service.queue_member.event.QueueMemberCreateEvent;

import jakarta.persistence.PostPersist;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QueueMemberEntityListener {

    @PostPersist
    public void onPostPersist(QueueMemberEntity queueMember) {
        log.info("QueueMemberEntityListener onPostPersist: {}", queueMember.getUid());
        ApplicationEventPublisher eventPublisher = ApplicationContextHolder.getBean(ApplicationEventPublisher.class);
        eventPublisher.publishEvent(new QueueMemberCreateEvent(queueMember));
    }

    // @PostUpdate
    // public void onPostUpdate(QueueMemberEntity queueMember) {
    //     log.info("QueueMemberEntityListener onPostUpdate: {}", queueMember.getUid());
    // }
    
}
