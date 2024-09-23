/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-29 13:00:33
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-19 14:49:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor_thread;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.thread.ThreadCreateEvent;
import com.bytedesk.core.thread.ThreadUpdateEvent;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
import com.bytedesk.core.thread.Thread;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class VisitorThreadEventListener {

    private final VisitorThreadService visitorThreadService;

    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        Thread thread = event.getThread();
        log.info("visitor ThreadCreateEvent: {}, type {}", thread.getUid(), thread.getType());
        // 仅同步客服会话
        if (thread.isCustomerService()) {
            visitorThreadService.create(event.getThread());
        } else {
            log.info("visitor ThreadCreateEvent not isCustomerService: {}, type {}", thread.getUid(), thread.getType());
        }
    }

    @EventListener
    public void onThreadUpdateEvent(ThreadUpdateEvent event) {
        Thread thread = event.getThread();
        log.info("visitor onThreadUpdateEvent: {}", thread.getUid());
        // 更新visitor_thread表
        if (thread.isCustomerService()) {
            visitorThreadService.update(event.getThread());
        }
    }

    @EventListener
    public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
        // log.info("visitor_thread quartz one min event: " + event);
        // auto close thread
        visitorThreadService.autoCloseThread();
    }

}
