/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 22:19:11
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-25 16:28:55
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.thread.ThreadUpdateEvent;
import com.bytedesk.core.thread.event.ThreadCreateEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class QueueEventListener {

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
    
}
