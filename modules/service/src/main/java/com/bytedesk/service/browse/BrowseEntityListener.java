/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-09 16:13:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-09 16:14:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.browse;

import org.springframework.stereotype.Component;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class BrowseEntityListener {
    
    @PostPersist
    public void onPostPersist(BrowseEntity browse) {
        log.info("onPostPersist: {}", browse);
    }

    @PostUpdate
    public void onPostUpdate(BrowseEntity browse) {
        log.info("onPostUpdate: {}", browse);
    }
}
