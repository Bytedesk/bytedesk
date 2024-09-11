/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-07 13:16:52
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-09-07 14:54:34
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.visitor;

import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.quartz.event.QuartzFiveMinEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class VisitorEventListener {

    private final VisitorService visitorService;

    // 更新访客在线状态：检测updatedAt时间戳，如果超过五分钟则更新为离线状态
    @EventListener
    public void onQuartzFiveMinEvent(QuartzFiveMinEvent event) {
        // log.info("visitor quartz five min event");
        // 
        List<Visitor> visitorList = visitorService.findByStatus(VisitorStatusEnum.ONLINE.name());
        visitorList.forEach(visitor -> {
            log.info("visitor: {}", visitor.getUid());
            if (System.currentTimeMillis() - visitor.getUpdatedAt().getTime() > 5 * 60 * 1000) {
                log.info("visitor: {} offline", visitor.getUid());
                visitorService.updateStatus(visitor.getUid(), VisitorStatusEnum.OFFLINE.name());
            }
        });

    }
    
}
