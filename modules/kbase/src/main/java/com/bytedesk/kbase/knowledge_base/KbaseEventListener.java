/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-27 13:53:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-24 13:34:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.knowledge_base;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.kbase.quick_reply.QuickReplyRestService;
import com.bytedesk.core.rbac.organization.OrganizationCreateEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class KbaseEventListener {

        private final KbaseRestService kbaseService;

        private final QuickReplyRestService quickReplyRestService;

        @EventListener
        public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
                OrganizationEntity organization = (OrganizationEntity) event.getSource();
                String orgUid = organization.getUid();
                log.info("onOrganizationCreateEvent: orgUid {}", orgUid);
                // 初始化知识库
                kbaseService.initKbase(orgUid);
                // 初始化快捷回复分类
                quickReplyRestService.initQuickReplyCategory(orgUid);
                // 初始化快捷回复
                quickReplyRestService.initQuickReply(orgUid);
        }

}
