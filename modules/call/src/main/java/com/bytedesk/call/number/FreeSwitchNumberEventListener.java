/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-06-09 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-08 21:38:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.call.number;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.call.number.event.FreeSwitchNumberCreateEvent;
import com.bytedesk.call.number.event.FreeSwitchNumberUpdateEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * FreeSwitch事件监听器
 */
@Slf4j
@Component
public class FreeSwitchNumberEventListener {
    
    @Order(1)
    @EventListener
    public void onFreeSwitchNumberCreateEvent(FreeSwitchNumberCreateEvent event) {
        FreeSwitchNumberEntity user = event.getUser();
        log.info("收到FreeSwitch用户创建事件: username={}, domain={}", 
                user.getUsername(), user.getDomain());
        
        // 这里可以添加额外的处理逻辑，比如：
        // 1. 发送欢迎消息给新用户
        // 2. 添加用户到默认组或会议室
    }
    
    @Order(1)
    @EventListener
    public void onFreeSwitchNumberUpdateEvent(FreeSwitchNumberUpdateEvent event) {
        FreeSwitchNumberEntity user = event.getUser();
        log.info("收到FreeSwitch用户更新事件: username={}, enabled={}", 
                user.getUsername(), user.getEnabled());
        
        // 这里可以添加额外的处理逻辑，比如：
        // 1. 如果用户被禁用，断开其当前连接
        // 2. 更新用户权限
    }
}
