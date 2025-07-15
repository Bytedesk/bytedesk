/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-14 18:45:49
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 18:52:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.robot_message;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSON;
import com.bytedesk.core.quartz.event.QuartzFiveSecondEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RobotMessageEventListener {

    private final RobotMessageCache robotMessageCache;

    private final RobotMessageRestService robotMessageRestService;

    @EventListener
    public void onQuartzFiveSecondEvent(QuartzFiveSecondEvent event) {
        // log.info("message quartz five second event: " + event);
        List<String> messageJsonList = robotMessageCache.getListForPersist();
        if (messageJsonList == null || messageJsonList.isEmpty()) {
            return;
        }
        
        log.info("Processing {} robot messages for persistence", messageJsonList.size());
        
        messageJsonList.forEach(item -> {
            try {
                RobotMessageRequest request = JSON.parseObject(item, RobotMessageRequest.class);
                robotMessageRestService.create(request);
            } catch (Exception e) {
                log.error("Failed to persist robot message: {}, error: {}", item, e.getMessage(), e);
                // 继续处理其他消息，不因为单个消息失败而中断整个批次
            }
        });
    }
    
}
