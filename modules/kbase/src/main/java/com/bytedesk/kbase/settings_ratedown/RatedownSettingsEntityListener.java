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
package com.bytedesk.kbase.settings_ratedown;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.kbase.settings_ratedown.event.RatedownSettingsCreateEvent;
import com.bytedesk.kbase.settings_ratedown.event.RatedownSettingsUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RatedownSettingsEntityListener {

    @PostPersist
    public void onPostPersist(RatedownSettingsEntity ratedownSetting) {
        log.info("onPostPersist: {}", ratedownSetting);
        RatedownSettingsEntity cloneRatedownSettings = SerializationUtils.clone(ratedownSetting);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new RatedownSettingsCreateEvent(cloneRatedownSettings));
    }

    @PostUpdate
    public void onPostUpdate(RatedownSettingsEntity ratedownSetting) {
        log.info("onPostUpdate: {}", ratedownSetting);
        RatedownSettingsEntity cloneRatedownSettings = SerializationUtils.clone(ratedownSetting);
        // 
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new RatedownSettingsUpdateEvent(cloneRatedownSettings));
    }
    
}
