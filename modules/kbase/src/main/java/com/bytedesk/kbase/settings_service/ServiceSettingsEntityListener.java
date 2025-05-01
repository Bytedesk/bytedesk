/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:52:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-29 15:24:29
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.settings_service;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.kbase.settings_service.event.ServiceSettingsCreateEvent;
import com.bytedesk.kbase.settings_service.event.ServiceSettingsUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ServiceSettingsEntityListener {

    @PostPersist
    public void onPostPersist(ServiceSettingsEntity serviceSetting) {
        log.info("onPostPersist: {}", serviceSetting);
        ServiceSettingsEntity cloneServiceSettings = SerializationUtils.clone(serviceSetting);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new ServiceSettingsCreateEvent(cloneServiceSettings));
    }

    @PostUpdate
    public void onPostUpdate(ServiceSettingsEntity serviceSetting) {
        log.info("onPostUpdate: {}", serviceSetting);
        ServiceSettingsEntity cloneServiceSettings = SerializationUtils.clone(serviceSetting);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new ServiceSettingsUpdateEvent(cloneServiceSettings));
    }
    
}
