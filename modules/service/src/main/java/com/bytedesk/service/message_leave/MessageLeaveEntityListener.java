/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-07 13:17:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-26 11:51:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.service.message_leave.event.MessageLeaveCreateEvent;
import com.bytedesk.service.message_leave.event.MessageLeaveUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MessageLeaveEntityListener {

    @PostPersist
    public void onPostPersist(MessageLeaveEntity MessageLeave) {
        log.info("onPostPersist: {}", MessageLeave);
        MessageLeaveEntity cloneMessageLeave = SerializationUtils.clone(MessageLeave);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new MessageLeaveCreateEvent(cloneMessageLeave));
    }

    @PostUpdate
    public void onPostUpdate(MessageLeaveEntity MessageLeave) {
        log.info("onPostUpdate: {}", MessageLeave);
        MessageLeaveEntity cloneMessageLeave = SerializationUtils.clone(MessageLeave);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new MessageLeaveUpdateEvent(cloneMessageLeave));
    }
    
}
