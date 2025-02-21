/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-19 09:23:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 23:04:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic_thread;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.rbac.user.UserProtobuf;
import com.bytedesk.core.thread.ThreadEntity;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class StatisticThreadEventListener {

    private final StatisticThreadService statisticThreadService;

    @EventListener
    public void onStatisticThreadCreateEvent(StatisticThreadCreateEvent event) {
        ThreadEntity thread = event.getThread();
        log.info("StatisticThreadEventListener onStatisticThreadCreateEvent: {}", thread.getUid());
        // 
        UserProtobuf agent = JSON.parseObject(thread.getAgent(), UserProtobuf.class);
        // 
        StatisticThreadRequest request = StatisticThreadRequest.builder().build();
        request.setThreadUid(thread.getUid());
        request.setThreadTopic(thread.getTopic());
        request.setStatus(thread.getState());
        request.setType(thread.getType());
        request.setAgentUid(agent.getUid());
        request.setOrgUid(thread.getOrgUid());
        statisticThreadService.create(request);
    }

}
