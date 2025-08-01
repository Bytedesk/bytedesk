/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-07 13:16:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-01 22:35:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import java.time.Duration;
import java.util.List;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.quartz.event.QuartzFiveMinEvent;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class VisitorEventListener {

    private final VisitorRestService visitorRestService;

    // 更新访客在线状态：检测updatedAt时间戳，如果超过五分钟则更新为离线状态
    @EventListener
    public void onQuartzFiveMinEvent(QuartzFiveMinEvent event) {
        // log.info("visitor quartz five min event");
        List<VisitorEntity> visitorList = visitorRestService.findByStatus(VisitorStatusEnum.ONLINE.name());
        visitorList.forEach(visitor -> {
            // log.info("visitor: {}", visitor.getUid());
            // 使用Duration计算时间差
            if (Duration.between(visitor.getUpdatedAt(), BdDateUtils.now()).toMillis() > 5 * 60 * 1000) {
                // if (System.currentTimeMillis() - visitor.getUpdatedAt().getTime() > 5 * 60 *
                // 1000) {
                // log.info("visitor: {} offline", visitor.getUid());
                visitorRestService.updateStatus(visitor.getUid(), VisitorStatusEnum.OFFLINE.name());
            }
        });
    }


}
