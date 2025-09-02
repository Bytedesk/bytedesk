/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:59:29
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-18 15:38:43
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.settings_template.event;

import org.springframework.context.ApplicationEvent;

import com.bytedesk.service.settings_template.SettingsTemplateEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SettingsTemplateCreateEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private SettingsTemplateEntity agentTemplate;

    public SettingsTemplateCreateEvent(SettingsTemplateEntity agentTemplate) {
        super(agentTemplate);
        this.agentTemplate = agentTemplate;
    }

}
