/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-07 16:27:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-21 18:14:02
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
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
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
    
    // 存储收集到的权限
    private final ConcurrentHashMap<String, Set<String>> roleAuthorityMap = new ConcurrentHashMap<>();

    @EventListener
    public void onRoleCreateEvent(RoleCreateEvent event) {
        // RoleEntity roleEntity = event.getRoleEntity();
        // log.info("onRoleCreateEvent: {}", roleEntity.toString());
    }

    @EventListener
    public void onAuthorityCreateEvent(AuthorityCreateEvent event) {
        AuthorityEntity authorityEntity = event.getAuthority();
        log.info("role AuthorityCreateEvent: {}", authorityEntity.getUid());
        
        // 将权限添加到超级管理员的权限集合
        roleAuthorityMap.computeIfAbsent(BytedeskConsts.DEFAULT_ROLE_SUPER_UID, k -> new HashSet<>())
                .add(authorityEntity.getUid());
        
        // 将权限添加到管理员的权限集合
        roleAuthorityMap.computeIfAbsent(BytedeskConsts.DEFAULT_ROLE_ADMIN_UID, k -> new HashSet<>())
                .add(authorityEntity.getUid());
        
        // 团队成员和客服、用户: 仅赋予部分权限：ticket、robot、kbase
        if (authorityEntity.getUid().startsWith("ticket")
                || authorityEntity.getUid().startsWith("robot")
                || authorityEntity.getUid().startsWith("kbase")) {
            // 将权限添加到团队成员的权限集合
            roleAuthorityMap.computeIfAbsent(BytedeskConsts.DEFAULT_ROLE_MEMBER_UID, k -> new HashSet<>())
                    .add(authorityEntity.getUid());
            
            // 将权限添加到客服的权限集合
            roleAuthorityMap.computeIfAbsent(BytedeskConsts.DEFAULT_ROLE_AGENT_UID, k -> new HashSet<>())
                    .add(authorityEntity.getUid());

            // 将权限添加到用户的权限集合
            roleAuthorityMap.computeIfAbsent(BytedeskConsts.DEFAULT_ROLE_USER_UID, k -> new HashSet<>())
                    .add(authorityEntity.getUid());
        }

        // 访客创建工单时，必须登录
        // 访客：仅赋予部分权限：ticket
        if (authorityEntity.getUid().startsWith("ticket")) {
            // 将权限添加到访客的权限集合
            // roleAuthorityMap.computeIfAbsent(BytedeskConsts.DEFAULT_ROLE_VISITOR_UID, k -> new HashSet<>())
            //         .add(authorityEntity.getUid());
        }
    }

    @EventListener
    public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
        // 处理收集的权限
        roleAuthorityMap.forEach((roleUid, authorityUids) -> {
            if (!authorityUids.isEmpty()) {
                log.info("处理角色权限: {} - 权限数量: {}", roleUid, authorityUids.size());
                RoleRequest roleRequest = RoleRequest.builder()
                        .uid(roleUid)
                        .authorityUids(authorityUids)
                        .build();
                roleService.addAuthorities(roleRequest);
                // 清除已处理的权限，避免重复添加
                authorityUids.clear();
            }
        });
    }
}
