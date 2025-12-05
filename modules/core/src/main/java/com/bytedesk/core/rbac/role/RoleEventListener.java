/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-07 16:27:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-05 14:54:58
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
import org.springframework.util.StringUtils;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.quartz.event.QuartzOneMinEvent;
import com.bytedesk.core.rbac.authority.AuthorityEntity;
import com.bytedesk.core.rbac.authority.event.AuthorityCreateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleEventListener {

    private final RoleRestService roleRestService;
    
    // 存储收集到的权限
    private final ConcurrentHashMap<String, Set<String>> roleAuthorityMap = new ConcurrentHashMap<>();

    // @EventListener
    // public void onRoleCreateEvent(RoleCreateEvent event) {
    //     // RoleEntity roleEntity = event.getRoleEntity();
    //     // log.info("onRoleCreateEvent: {}", roleEntity.toString());
    // }

    @EventListener
    public void onAuthorityCreateEvent(AuthorityCreateEvent event) {
        AuthorityEntity authorityEntity = event.getAuthority();
        // log.info("role AuthorityCreateEvent: {}", authorityEntity.getUid());
        
        // 权限层级设计原则：高层级权限包含低层级权限的访问能力
        // PLATFORM > ORGANIZATION > DEPARTMENT > WORKGROUP > AGENT > USER
        // 因此只需要分配最高需要的层级权限即可，无需分配所有层级权限
        
        String authorityUid = authorityEntity.getUid();
        if (!StringUtils.hasText(authorityUid)) {
            return;
        }
        String authorityValue = StringUtils.hasText(authorityEntity.getValue())
            ? authorityEntity.getValue()
            : authorityUid;
        String levelMarker = authorityValue.toUpperCase();
        
        // 权限层级设计：
        // SUPER（超级管理员）: 平台级 PLATFORM 权限 - 管理整个平台
        // ADMIN（组织管理员）: 组织级 ORGANIZATION 权限 - 管理某个组织
        // DEPT_ADMIN（部门管理员）: 部门级 DEPARTMENT 权限 - 管理某个部门
        // WORKGROUP_ADMIN（工作组管理员）: 工作组级 WORKGROUP 权限 - 管理某个工作组
        // AGENT: 一线客服在限定模块内的操作权限
        // USER: 终端用户在限定模块内的操作权限
        
        // 超级管理员: 只分配 PLATFORM 级别权限
        if (levelMarker.contains("_PLATFORM_")) {
            roleAuthorityMap.computeIfAbsent(BytedeskConsts.DEFAULT_ROLE_SUPER_UID, k -> new HashSet<>())
                    .add(authorityUid);
        }
        
        // 组织管理员: 只分配 ORGANIZATION 级别权限：组织属于平台下属，一个平台可以有多个组织
        if (levelMarker.contains("_ORGANIZATION_")) {
            roleAuthorityMap.computeIfAbsent(BytedeskConsts.DEFAULT_ROLE_ADMIN_UID, k -> new HashSet<>())
                    .add(authorityUid);
        }
        
        // 部门管理员: 只分配 DEPARTMENT 级别权限：部门属于组织下属，一个组织可以有多个部门
        if (levelMarker.contains("_DEPARTMENT_")) {
            roleAuthorityMap.computeIfAbsent(BytedeskConsts.DEFAULT_ROLE_DEPT_ADMIN_UID, k -> new HashSet<>())
                    .add(authorityUid);
        }
        
        // 工作组管理员: 只分配 WORKGROUP 级别权限：工作组属于客服部门下属，一个部门可以有多个工作组
        if (levelMarker.contains("_WORKGROUP_")) {
            roleAuthorityMap.computeIfAbsent(BytedeskConsts.DEFAULT_ROLE_WORKGROUP_ADMIN_UID, k -> new HashSet<>())
                    .add(authorityUid);
        }
        
        // 客服：仅接收允许模块的 AGENT 级别权限 && matchesPrefix(authorityUid, AGENT_MODULE_PREFIXES)
        if (levelMarker.contains("_AGENT_")) {
            roleAuthorityMap.computeIfAbsent(BytedeskConsts.DEFAULT_ROLE_AGENT_UID, k -> new HashSet<>())
                .add(authorityUid);
        }

        // 终端用户：接收 USER 级别权限
        if (levelMarker.contains("_USER_")) {
            roleAuthorityMap.computeIfAbsent(BytedeskConsts.DEFAULT_ROLE_USER_UID, k -> new HashSet<>())
                .add(authorityUid);
        }
    }

    @EventListener
    public void onQuartzOneMinEvent(QuartzOneMinEvent event) {
        // 处理收集的权限
        roleAuthorityMap.forEach((roleUid, authorityUids) -> {
            if (!authorityUids.isEmpty()) {
                // log.info("处理角色权限: {} - 权限数量: {}", roleUid, authorityUids.size());
                RoleRequest roleRequest = RoleRequest.builder()
                        .uid(roleUid)
                        .authorityUids(authorityUids)
                        .build();
                roleRestService.addAuthorities(roleRequest);
                // 清除已处理的权限，避免重复添加
                authorityUids.clear();
            }
        });
    }

    // private boolean matchesPrefix(String authorityUid, Set<String> prefixes) {
    //     return prefixes.stream().anyMatch(authorityUid::startsWith);
    // }
}
