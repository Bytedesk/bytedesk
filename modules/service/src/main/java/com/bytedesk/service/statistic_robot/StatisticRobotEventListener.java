/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-21 09:52:26
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 23:04:28
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic_robot;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.service.statistic_thread.StatisticThreadCreateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class StatisticRobotEventListener {

    @EventListener
    public void onStatisticThreadCreateEvent(StatisticThreadCreateEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("StatisticRobotEventListener onStatisticThreadCreateEvent: {}", thread.getUid());
    }
}
