/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-03 13:34:21
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-08 09:27:16
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

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.enums.PermissionEnum;
import com.bytedesk.core.rbac.authority.AuthorityRestService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TicketInitializer implements SmartInitializingSingleton {

    private final AuthorityRestService authorityService;

    private final TicketRestService ticketRestService;

    @Override
    public void afterSingletonsInstantiated() {
        initAuthority();
        // 创建默认的工单分类
        String orgUid = BytedeskConsts.DEFAULT_ORGANIZATION_UID;
        ticketRestService.initTicketCategory(orgUid);
    }

    private void initAuthority() {
        for (PermissionEnum permission : PermissionEnum.values()) {
            String permissionValue = TicketPermissions.TICKET_PREFIX + permission.name();
            authorityService.createForPlatform(permissionValue);
        }
    }

}
