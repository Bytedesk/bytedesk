/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-15 12:39:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-01 13:51:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.process;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessEventListener {

    private final ProcessInitializationService processInitializationService;

    @Order(8)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        String orgUid = organization.getUid();
        log.info("ticket process - organization created: {}", orgUid);
        // 事务化初始化（REQUIRES_NEW）：失败记录后结束本监听器，不吞异常返回成功，
        // 也不向事件多播器抛出（避免波及同链后续 Holiday/Unified/Form 初始化）。
        // 前序监听器异常导致本监听器未执行时，通过 admin 手动 /init 端点补齐。
        try {
            processInitializationService.initDefaultProcesses(orgUid);
        } catch (Exception e) {
            log.error("ticket process - init default processes failed, orgUid={}, "
                    + "use admin manual init to recover", orgUid, e);
        }
        // processRestService.initThreadProcess(orgUid);
    }

}