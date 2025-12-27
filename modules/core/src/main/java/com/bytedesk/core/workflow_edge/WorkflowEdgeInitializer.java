/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-06 21:43:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-04 11:23:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow_edge;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class WorkflowEdgeInitializer implements SmartInitializingSingleton {

    // private final WorkflowEdgeRestService workflow_edgeRestService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        // create default
        // String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        // workflow_edgeRestService.initWorkflowEdges(orgUid);
    }

    private void initAuthority() {
        // for (PermissionEnum permission : PermissionEnum.values()) {
        //     String permissionValue = WorkflowEdgePermissions.WORKFLOW_EDGE_PREFIX + permission.name();
        //     authorityService.createForPlatform(permissionValue);
        // }
    }

    

}
