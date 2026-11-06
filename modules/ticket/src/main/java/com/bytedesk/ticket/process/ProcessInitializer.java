/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-15 13:03:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-01 13:51:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.process;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("processInitializer")
@RequiredArgsConstructor
public class ProcessInitializer implements SmartInitializingSingleton {

    private final AuthorityRestService authorityService;
    private final ProcessRestService processRestService;
    private final ProcessInitializationService processInitializationService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        // 初始化默认组织机构的流程
        // 复用事务化初始化入口（REQUIRES_NEW + 行锁 + 状态矩阵），失败抛出由日志记录；
        // 默认组织此刻已由 core 各 Initializer 前置持久化（@DependsOn 链）。
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        try {
            processInitializationService.initDefaultProcesses(orgUid);
        } catch (Exception e) {
            log.error("初始化默认组织工单流程失败: orgUid={}", orgUid, e);
        }
        // processRestService.initThreadProcess(orgUid);
        // 初始化演示流程模板
        // processRestService.initProcessDemos(orgUid);
        // 迁移：为缺少 flowgramSchema 的旧流程生成默认 JSON
        processRestService.migrateFlowgramSchemas();
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = ProcessPermissions.PROCESS_PREFIX + permission.name();
            authorityService.createForPlatform(permissionValue);
        }
    }

}
