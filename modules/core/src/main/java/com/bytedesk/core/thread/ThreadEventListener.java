/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-28 13:32:23
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-06-28 15:00:08
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

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ThreadEventListener {

    private ThreadService threadService;

    @EventListener
    public void onThreadCreateEvent(ThreadCreateEvent event) {
        Thread thread = event.getThread();
        log.info("thread ThreadCreateEvent: {}", thread.getUid());
        // 
        if (thread.getType().equals(ThreadTypeEnum.MEMBER)) {
            threadService.createMemberReverseThread(thread);
        }

    }
    

}
