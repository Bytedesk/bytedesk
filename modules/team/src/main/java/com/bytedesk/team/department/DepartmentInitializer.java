/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 13:43:02
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-07 07:15:54
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.team.department;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.BytedeskConsts;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class DepartmentInitializer implements SmartInitializingSingleton {

    private final DepartmentService departmentService;

    private final DepartmentRepository departmentRepository;

    @Override
    public void afterSingletonsInstantiated() {
        init();
    }

    // @PostConstruct
    public void init() {

        // create接口中自动去重，可以省略掉此步骤
        if (departmentRepository.count() > 0) {
            return;
        }
        //
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        DepartmentRequest adminDept = DepartmentRequest.builder()
            .name(DepartmentConsts.DEPT_ADMIN)
            .description("Description for " + DepartmentConsts.DEPT_ADMIN)
            .build();
        adminDept.setUid(DepartmentConsts.DEFAULT_DEPT_ADMIN_UID);
        adminDept.setOrgUid(orgUid);
        departmentService.create(adminDept);
        //
        DepartmentRequest csDept = DepartmentRequest.builder()
            .name(DepartmentConsts.DEPT_CUSTOMER_SERVICE)
            .description("Description for " + DepartmentConsts.DEPT_CUSTOMER_SERVICE)
            .build();
        csDept.setUid(DepartmentConsts.DEFAULT_DEPT_CUSTOMER_SERVICE_UID);
        csDept.setOrgUid(orgUid);
        departmentService.create(csDept);
    }
    
}
