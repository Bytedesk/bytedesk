/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-05-29 15:09:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-10 23:21:44
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.thread_log;

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
public class ThreadLogEventListener {

    private ThreadLogService threadLogService;

    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        Thread thread = event.getThread();
        // log.info("thread log ThreadCreateEvent: {}", thread.getUid());
        // 
        threadLogService.create(thread);
    }

    @EventListener
    public void onThreadUpdateEvent(ThreadUpdateEvent event) {
        Thread thread = event.getThread();
        log.info("thread log onThreadUpdateEvent: {}", thread.getUid());
        // 
        // 访客之前会话已经关闭，重新请求会话，则重新创建一条threadlog
        // if (thread.getStatus().equals(ThreadStatusEnum.REENTER)) {
        //     threadLogService.create(thread);
        // }
        // TODO: 更新threadlog
        
    }
    
    @EventListener
    public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
        // log.info("threadlog quartz five second event: " + event);
        // auto close thread
        threadLogService.autoCloseThread();
    }
    
    
}
