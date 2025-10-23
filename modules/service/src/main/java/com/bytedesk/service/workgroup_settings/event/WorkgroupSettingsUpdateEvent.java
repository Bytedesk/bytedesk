/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-10-24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.workgroup_settings.event;

import com.bytedesk.service.workgroup_settings.WorkgroupSettingsEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import org.springframework.context.ApplicationEvent;

/**
 * Event fired when a workgroup settings is updated
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class WorkgroupSettingsUpdateEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final WorkgroupSettingsEntity workgroupSettings;

    public WorkgroupSettingsUpdateEvent(Object source, WorkgroupSettingsEntity workgroupSettings) {
        super(source);
        this.workgroupSettings = workgroupSettings;
    }

    public WorkgroupSettingsEntity getWorkgroupSettings() {
        return workgroupSettings;
    }
}
