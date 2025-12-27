/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-06 21:43:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 13:28:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class WorkflowInitializer implements SmartInitializingSingleton {

    private final WorkflowRestService workflowRestService;

    private final AuthorityRestService authorityRestService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        initDefaultWorkflow();
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = WorkflowPermissions.WORKFLOW_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }

    private void initDefaultWorkflow() {
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        workflowRestService.initDefaultWorkflow(orgUid);
    }

}
