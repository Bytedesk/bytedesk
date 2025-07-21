/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:44:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-04 15:50:06
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.alibaba.demo.shopping;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ShoppingEventListener {

    private final ShoppingRestService shoppingRestService;

    @Order(3)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        String orgUid = organization.getUid();
        log.info("thread - organization created: {}", organization.getName());
        shoppingRestService.initShoppings(orgUid);
    }

 
}

