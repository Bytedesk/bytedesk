/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-20 14:31:54
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-24 13:33:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.quick_reply;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// 快捷用语事件监听器
@Slf4j
@Component
@AllArgsConstructor
public class QuickReplyEventListener {

    // private final QuickReplyRestService quickReplyRestService;

    @Order(7)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        // User user = organization.getUser();
        log.info("quick_reply - organization created: {}", organization.getName());
        // 为保证执行顺序，迁移到KnowledgebaseEventListener中
        // String orgUid = organization.getUid();
        // 创建快捷用语
        // quickReplyRestService.initQuickReply(orgUid);
        // 创建快捷用语分类
        // quickReplyRestService.initQuickReplyCategory(orgUid);
    }
    
}
