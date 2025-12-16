/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 09:52:34
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-06 18:14:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kbase.kbase_invite;

import org.springframework.stereotype.Component;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.utils.ApplicationContextHolder;
import com.bytedesk.kbase.kbase_invite.event.KbaseInviteCreateEvent;
import com.bytedesk.kbase.kbase_invite.event.KbaseInviteDeleteEvent;
import com.bytedesk.kbase.kbase_invite.event.KbaseInviteUpdateEvent;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class KbaseInviteEntityListener {

    @PostPersist
    public void onPostPersist(KbaseInviteEntity kbaseInvite) {
        log.info("onPostPersist: {}", kbaseInvite);
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        publisher.publishEvent(new KbaseInviteCreateEvent(this, kbaseInvite));
    }

    @PostUpdate
    public void onPostUpdate(KbaseInviteEntity kbaseInvite) {
        log.info("onPostUpdate: {}", kbaseInvite);
        BytedeskEventPublisher publisher = ApplicationContextHolder.getBean(BytedeskEventPublisher.class);
        if (kbaseInvite.isDeleted()) {
            publisher.publishEvent(new KbaseInviteDeleteEvent(this, kbaseInvite));
        } else {
            publisher.publishEvent(new KbaseInviteUpdateEvent(this, kbaseInvite));
        }
    }
    
}
