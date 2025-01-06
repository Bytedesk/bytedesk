/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-12 22:12:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-11-22 13:11:40
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.model;

import org.springframework.stereotype.Component;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Component
public class LlmModelEntityListener {

    @PostPersist
    public void postPersist(LlmModelEntity entity) {
        // log.info("LlmModelEntityListener postPersist: {}", entity);
    }

    @PostUpdate
    public void postUpdate(LlmModelEntity entity) {
        // log.info("LlmModelEntityListener postUpdate: {}", entity);
    }
    
}
