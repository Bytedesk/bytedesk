/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:52:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 17:00:07
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.settings_template;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.service.settings_template.event.SettingsTemplateCreateEvent;
import com.bytedesk.service.settings_template.event.SettingsTemplateUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SettingsTemplateEntityListener {

    @PostPersist
    public void onPostPersist(SettingsTemplateEntity agentTemplate) {
        log.info("onPostPersist: {}", agentTemplate);
        SettingsTemplateEntity cloneSettingsTemplate = SerializationUtils.clone(agentTemplate);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new SettingsTemplateCreateEvent(cloneSettingsTemplate));
    }

    @PostUpdate
    public void onPostUpdate(SettingsTemplateEntity agentTemplate) {
        log.info("onPostUpdate: {}", agentTemplate);
        SettingsTemplateEntity cloneSettingsTemplate = SerializationUtils.clone(agentTemplate);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new SettingsTemplateUpdateEvent(cloneSettingsTemplate));
    }
    
}
