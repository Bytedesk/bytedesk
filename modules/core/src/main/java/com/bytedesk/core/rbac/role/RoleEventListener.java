/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-07 16:27:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-06 10:13:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.rbac.role.event.RoleCreateEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RoleEventListener {
    

    @EventListener
    public void onRoleCreateEvent(RoleCreateEvent event) {
        // RoleEntity roleEntity = event.getRoleEntity();
        // log.info("onRoleCreateEvent: {}", roleEntity.toString());
    }

    // @EventListener
    // public void onRoleUpdateEvent(RoleUpdateEvent> event) {
    //     RoleEntity roleEntity = event.getRoleEntity();
    //     log.info("onRoleUpdateEvent: {}", roleEntity.toString());
    // }

}
