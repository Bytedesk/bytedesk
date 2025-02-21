/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-19 17:02:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 23:04:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic_workgroup;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.service.statistic_thread.StatisticThreadCreateEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class StatisticWorkgroupEventListener {

    @EventListener
    public void onStatisticThreadCreateEvent(StatisticThreadCreateEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("StatisticWorkgroupEventListener onStatisticThreadCreateEvent: {}", thread.getUid());
        //
    }

    // @EventListener
    // public void onThreadCreateEvent(ThreadCreateEvent event) {
    // Thread thread = event.getThread();
    // log.info("statistic_agent ThreadCreateEvent: {}, type {}", thread.getUid(),
    // thread.getType());
    // // 仅处理客服会话
    // }

    // @EventListener
    // public void onThreadUpdateEvent(ThreadUpdateEvent event) {
    // Thread thread = event.getThread();
    // log.info("statistic_agent onThreadUpdateEvent: {}", thread.getUid());
    // // 仅处理客服会话

    // }

    // @EventListener
    // public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
    // // log.info("statistic_agent quartz one min event: " + event);
    // }
}
