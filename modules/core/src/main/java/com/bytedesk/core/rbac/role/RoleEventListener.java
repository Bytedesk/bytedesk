/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-07 16:27:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 17:37:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.role;

import java.util.HashSet;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.LevelEnum;
import com.bytedesk.core.rbac.authority.AuthorityEntity;
import com.bytedesk.core.rbac.authority.event.AuthorityCreateEvent;
import com.bytedesk.core.rbac.role.event.RoleCreateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleEventListener {
    
    private final RoleRestService roleService;

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

    @EventListener
    public void onAuthorityCreateEvent(AuthorityCreateEvent event) {
        AuthorityEntity authorityEntity = event.getAuthority();
        log.info("role AuthorityCreateEvent: {}", authorityEntity.getName());
        // 给超级管理员和管理员赋予所有权限
        Set<String> authorityUids = new HashSet<>();
        authorityUids.add(authorityEntity.getUid());
        // 超级管理员
        RoleRequest superRoleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_SUPER_UID)
                .name(RoleConsts.ROLE_SUPER)
                .description("Super")
                .authorityUids(authorityUids)
                .level(LevelEnum.PLATFORM.name())
                .system(true)
                .build();
        roleService.createOrUpdate(superRoleRequest);
        // 管理员
        RoleRequest adminRoleRequest = RoleRequest.builder()
                .uid(BytedeskConsts.DEFAULT_ROLE_ADMIN_UID)
                .name(RoleConsts.ROLE_ADMIN)
                .description("Admin")
                .authorityUids(authorityUids)
                .level(LevelEnum.PLATFORM.name())
                .system(true)
                .build();
        roleService.createOrUpdate(adminRoleRequest);
    }

}
