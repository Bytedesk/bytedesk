/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-06 21:43:58
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-26 10:52:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup_routing;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class WorkgroupRoutingInitializer implements SmartInitializingSingleton {

    private final WorkgroupRoutingRestService workgroup_routingRestService;

    private final AuthorityRestService authorityRestService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        // create default
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        workgroup_routingRestService.initWorkgroupRoutings(orgUid);
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = WorkgroupRoutingPermissions.WORKGROUP_ROUTING_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }

    

}
