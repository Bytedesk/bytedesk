/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 21:39:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.cdr;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.call.cdr.event.CallCdrCreateEvent;
import com.bytedesk.call.cdr.event.CallCdrUpdateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * Call事件监听器
 */
@Slf4j
@Component
public class CallCdrEventListener {

    @EventListener
    public void onCallCdrCreateEvent(CallCdrCreateEvent event) {
        CallCdrEntity cdr = event.getCdr();
        log.info("收到Call CDR创建事件: uuid={}, caller={}, destination={}", 
                cdr.getUid(), cdr.getCallerIdNumber(), cdr.getDestinationNumber());
        
        // 这里可以添加额外的处理逻辑，比如：
        // 1. 通知系统管理员有新的通话记录
        // 2. 更新相关统计信息
        // 3. 触发其他业务流程
    }
    
    @EventListener
    public void onCallCdrUpdateEvent(CallCdrUpdateEvent event) {
        CallCdrEntity cdr = event.getCdr();
        log.info("收到Call CDR更新事件: uuid={}, duration={}", 
                cdr.getUid(), cdr.getDuration());
        
        // 这里可以添加额外的处理逻辑
    }
    
    
}
