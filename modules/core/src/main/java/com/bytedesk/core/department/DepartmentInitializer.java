/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-12 11:30:08
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.department;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DepartmentInitializer implements SmartInitializingSingleton {

    private final DepartmentRestService departmentService;

    private final AuthorityRestService authorityRestService;

    @Override
    public void afterSingletonsInstantiated() {
        init();
        initAuthority();
    }

    // @PostConstruct
    public void init() {
        
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        DepartmentRequest adminDept = DepartmentRequest.builder()
            .uid(DepartmentConsts.DEFAULT_DEPT_ADMIN_UID)
            .name(DepartmentConsts.DEPT_ADMIN)
            .description("Description for " + DepartmentConsts.DEPT_ADMIN)
            .orgUid(orgUid)
            .build();
        departmentService.create(adminDept);
        //
        DepartmentRequest csDept = DepartmentRequest.builder()
            .uid(DepartmentConsts.DEFAULT_DEPT_CUSTOMER_SERVICE_UID)
            .name(DepartmentConsts.DEPT_CUSTOMER_SERVICE)
            .description("Description for " + DepartmentConsts.DEPT_CUSTOMER_SERVICE)
            .orgUid(orgUid)
            .build();
        departmentService.create(csDept);
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = DepartmentPermissions.DEPARTMENT_PREFIX + permission.name();
            authorityRestService.createForPlatform(permissionValue);
        }
    }
    
}
