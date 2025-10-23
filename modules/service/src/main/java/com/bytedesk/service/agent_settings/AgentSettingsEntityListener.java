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
package com.bytedesk.service.agent_settings;

import org.springframework.stereotype.Component;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

/**
 * Entity listener for AgentSettings lifecycle events
 */
@Slf4j
@Component
public class AgentSettingsEntityListener {
    
    @PostPersist
    public void onPostPersist(AgentSettingsEntity agentSettings) {
        log.debug("AgentSettingsEntityListener: onPostPersist - {}", agentSettings.getUid());
    }

    @PostUpdate
    public void onPostUpdate(AgentSettingsEntity agentSettings) {
        log.debug("AgentSettingsEntityListener: onPostUpdate - {}", agentSettings.getUid());
    }
}
