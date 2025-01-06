/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-04-27 12:09:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-10-14 09:31:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.assistant;

import org.springframework.stereotype.Component;

import jakarta.persistence.PostPersist;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
@Component
public class AssistantEntityListener {

    @PostPersist
    public void onPostPersist(AssistantEntity assistant) {
        // log.debug("AssistantListener: onPostPersist {}", assistant.getNickname());
    }

}
