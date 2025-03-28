/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-15 13:03:35
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-28 16:28:29
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
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketProcessInitializer implements SmartInitializingSingleton {

    // private final AuthorityRestService authorityService;

    // private final OrganizationRestService organizationService;

    private final TicketProcessRestService ticketProcessRestService;

    // private final RepositoryService repositoryService;

    // private final ResourceLoader resourceLoader;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        // initProcess();
        // 
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        log.info("ticket process - organization created: {}", orgUid);
        ticketProcessRestService.initProcess(orgUid);
    }

    private void initAuthority() {
        // for (PermissionEnum permission : PermissionEnum.values()) {
        //     String permissionValue = TicketProcessPermissions.PROCESS_PREFIX + permission.name();
        //     if (authorityService.existsByValue(permissionValue)) {
        //         continue;
        //     }
        //     AuthorityRequest authRequest = AuthorityRequest.builder()
        //             .name(I18Consts.I18N_PREFIX + permissionValue)
        //             .value(permissionValue)
        //             .description("Permission for " + permissionValue)
        //             .build();
        //     authRequest.setUid(permissionValue.toLowerCase());
        //     authorityService.create(authRequest);
        // }
    }
}
