/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-04 16:18:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 17:00:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.rbac.authority;

import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.rbac.authority.event.AuthorityCreateEvent;
import com.bytedesk.core.rbac.authority.event.AuthorityUpdateEvent;
import com.bytedesk.core.utils.ApplicationContextHolder;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthorityEntityListener {

    @PostPersist
    public void onPostPersist(AuthorityEntity authority) {
        log.info("authority onPostPersist: " + authority);
        AuthorityEntity cloneAuthority = SerializationUtils.clone(authority);
        //
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new AuthorityCreateEvent(cloneAuthority));
    }

    @PostUpdate
    public void onPostUpdate(AuthorityEntity authority) {
        log.info("authority onPostUpdate: " + authority);
        AuthorityEntity cloneAuthority = SerializationUtils.clone(authority);
        //
        BytedeskEventPublisher bytedeskEventPublisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        bytedeskEventPublisher.publishEvent(new AuthorityUpdateEvent(cloneAuthority));
    }
    
}
