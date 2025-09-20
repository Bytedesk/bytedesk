/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:52:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 17:49:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.service_settings;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.service.service_settings.event.ServiceSettingsCreateEvent;
import com.bytedesk.service.service_settings.event.ServiceSettingsUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ServiceSettingsEntityListener {

    @PostPersist
    public void onPostPersist(ServiceSettingsEntity serviceSettings) {
        log.info("onPostPersist: {}", serviceSettings);
        ServiceSettingsEntity cloneServiceSettings = SerializationUtils.clone(serviceSettings);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new ServiceSettingsCreateEvent(cloneServiceSettings));
    }

    @PostUpdate
    public void onPostUpdate(ServiceSettingsEntity serviceSettings) {
        log.info("onPostUpdate: {}", serviceSettings);
        ServiceSettingsEntity cloneServiceSettings = SerializationUtils.clone(serviceSettings);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new ServiceSettingsUpdateEvent(cloneServiceSettings));
    }
    
}
