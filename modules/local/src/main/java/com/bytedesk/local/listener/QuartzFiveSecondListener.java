/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-28 14:40:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-04-18 12:43:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.local.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.push.PushService;
import com.bytedesk.core.quartz.QuartzFiveSecondEvent;
// import com.bytedesk.socket.service.MessageSocketService;
import com.bytedesk.local.caffeine.CaffeineCacheService;
import com.bytedesk.service.thread_log.ThreadLogService;
import com.bytedesk.socket.service.MessageJsonService;

import lombok.AllArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Component
@AllArgsConstructor
public class QuartzFiveSecondListener implements ApplicationListener<QuartzFiveSecondEvent> {

    private final CaffeineCacheService cacheService;

    private final MessageJsonService messageJsonService;

    private final ThreadLogService threadLogService;

    private final PushService pushService;

    @Override
    public void onApplicationEvent(QuartzFiveSecondEvent event) {
        // log.info("FiveSecondJob listener");
        // auto close thread
        threadLogService.autoCloseThread();
        // auto outdate code
        pushService.autoOutdateCode();
        // 
        String json = cacheService.getFirst();
        if (json == null) {
            return;
        }
        messageJsonService.saveToDb(json);
    }

}
