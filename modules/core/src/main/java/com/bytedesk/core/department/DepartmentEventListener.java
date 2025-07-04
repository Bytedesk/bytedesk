/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-05 10:31:31
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-07 07:24:47
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.department;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;
import com.bytedesk.core.uid.UidUtils;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class DepartmentEventListener {

    private final DepartmentRestService departmentService;

    private final UidUtils uidUtils;

    @Transactional
    @Order(1)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        // UserEntity user = organization.getUser();
        String orgUid = organization.getUid();
        log.info("organization created: {}", organization.getName());
        // 
        DepartmentRequest csDept = DepartmentRequest.builder()
            .name(DepartmentConsts.DEPT_CUSTOMER_SERVICE)
            .description("Description for " + DepartmentConsts.DEPT_CUSTOMER_SERVICE)
            .build();
        csDept.setUid(uidUtils.getUid());
        csDept.setOrgUid(orgUid);
        departmentService.create(csDept);
    }

}
