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
package com.bytedesk.service.config;

import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.bytedesk.ai.robot_settings.RobotSettingsRestService;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.event.OrganizationCreateEvent;
import com.bytedesk.service.agent_settings.AgentSettingsRestService;
import com.bytedesk.service.workgroup_settings.WorkgroupSettingsRestService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class ServiceSettingsEventListener {

    private final AgentSettingsRestService agentSettingsRestService;

    private final WorkgroupSettingsRestService workgroupSettingsRestService;

    private final RobotSettingsRestService robotSettingsRestService;

    /**
     * Ensure default settings exist for the organization. Run early so other
     * organization-level initializers can rely on these settings.
     */
    @Order(5)
    @EventListener
    public void onOrganizationCreateEvent(OrganizationCreateEvent event) {
        OrganizationEntity organization = (OrganizationEntity) event.getSource();
        String orgUid = organization.getUid();
        log.info("settings - organization created {}", orgUid);

        try {
            agentSettingsRestService.getOrCreateDefault(orgUid);
        } catch (Exception e) {
            log.error("failed to create default agent settings for org {}: {}", orgUid, e.getMessage());
        }

        try {
            workgroupSettingsRestService.getOrCreateDefault(orgUid);
        } catch (Exception e) {
            log.error("failed to create default workgroup settings for org {}: {}", orgUid, e.getMessage());
        }

        try {
            robotSettingsRestService.getOrCreateDefault(orgUid);
        } catch (Exception e) {
            log.error("failed to create default robot settings for org {}: {}", orgUid, e.getMessage());
        }
    }

}
