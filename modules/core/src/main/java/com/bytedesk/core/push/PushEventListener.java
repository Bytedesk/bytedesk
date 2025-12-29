/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-27 16:24:13
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-10 10:22:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.push;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.quartz.event.QuartzOneMinEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class PushEventListener {

    private final PushRestService pushRestService;

    @EventListener
    public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
        // auto outdate code
        pushRestService.autoOutdateCode();
    }
    
}
