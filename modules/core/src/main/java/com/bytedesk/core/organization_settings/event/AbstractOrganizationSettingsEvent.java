/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-11-28 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-28 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.organization_settings.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.organization_settings.OrganizationSettingsEntity;

/**
 * Ensures every organization_settings-related application event carries a detached snapshot so
 * async listeners never touch managed persistence contexts from other threads.
 */
public abstract class AbstractOrganizationSettingsEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final OrganizationSettingsEntity organization_settings;

    protected AbstractOrganizationSettingsEvent(Object source, OrganizationSettingsEntity organization_settings) {
        super(source);
        this.organization_settings = snapshot(organization_settings);
    }

    public OrganizationSettingsEntity getOrganizationSettings() {
        return organization_settings;
    }

    private OrganizationSettingsEntity snapshot(OrganizationSettingsEntity source) {
        if (source == null) {
            return null;
        }
        try {
            return SerializationUtils.clone(source);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Failed to snapshot organization_settings " + source.getUid(), ex);
        }
    }
}
