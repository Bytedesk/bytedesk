/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-08-19 12:03:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-26 11:19:59
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.action;

import org.springframework.stereotype.Component;

import com.bytedesk.core.action.event.ActionCreateEvent;
import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ActionEntityListener {

    @PostPersist
    private void onPostPersist(ActionEntity action) {
        log.info("actionLog after: model {}, action {}", action.getTitle(), action.getAction());
        //
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new ActionCreateEvent(this, action));
    }

}
