/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-10 23:11:19
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 16:06:42
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.quartz.event.QuartzHalfHourEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceStatisticEventListener {

    private final ServiceStatisticService serviceStatisticService;

    // 每半小时计算一次客服统计
    @EventListener
    public void handleQuartzHalfHourEvent(QuartzHalfHourEvent event) {
        log.info("ServiceStatisticEventListener handleQuartzHalfHourEvent: {}", event);
        serviceStatisticService.calculateTodayStatistics();
    }


}
