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

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        // 初始化默认组织机构的流程
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        // log.info("ticket process - organization created: {}", orgUid);
        processRestService.initProcess(orgUid);
        processRestService.initThreadProcess(orgUid);
        // 初始化演示流程模板
        processRestService.initProcessDemos(orgUid);
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = ProcessPermissions.PROCESS_PREFIX + permission.name();
            authorityService.createForPlatform(permissionValue);
        }
    }

}
