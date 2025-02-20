/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-20 13:26:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-20 13:34:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.statistic;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.quartz.event.QuartzHourlyEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketStatisticEventListener {

    private final TicketStatisticService ticketStatisticService;

    // 每小时计算一次工单统计
    @EventListener
    public void handleQuartzHourlyEvent(QuartzHourlyEvent event) {
        log.info("TicketStatisticEventListener handleQuartzHourlyEvent: {}", event);
        ticketStatisticService.calculateAllStatistics();
    }
    
}
