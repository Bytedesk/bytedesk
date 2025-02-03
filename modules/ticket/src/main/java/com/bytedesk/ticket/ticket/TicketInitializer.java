/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-03 13:34:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-03 13:37:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import com.bytedesk.core.constant.I18Consts;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRequest;
import com.bytedesk.core.rbac.authority.AuthorityService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TicketInitializer implements SmartInitializingSingleton {

    private final AuthorityService authorityService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        initTicketCategory();
        initTicket();
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = TicketPermissions.TICKET_PREFIX + permission.name();
            if (authorityService.existsByValue(permissionValue)) {
                log.info("Ticket authority {} already exists", permissionValue);
                continue;
            }
            AuthorityRequest authRequest = AuthorityRequest.builder()
                    .name(I18Consts.I18N_PREFIX + permissionValue)
                    .value(permissionValue)
                    .description("Permission for " + permissionValue)
                    .build();
            authRequest.setUid(permissionValue.toLowerCase());
            authorityService.create(authRequest);
        }
    }

    private void initTicketCategory() {
        log.info("initTicketCategory");
    }

    private void initTicket() {
        log.info("initTicket");
    }

}
