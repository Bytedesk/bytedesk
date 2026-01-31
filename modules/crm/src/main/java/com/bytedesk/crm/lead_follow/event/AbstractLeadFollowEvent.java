/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-11-28 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-28 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.crm.lead_follow.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.SerializationUtils;

import com.bytedesk.crm.lead_follow.LeadFollowEntity;

/**
 * Ensures every lead_follow-related application event carries a detached snapshot so
 * async listeners never touch managed persistence contexts from other threads.
 */
public abstract class AbstractLeadFollowEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final LeadFollowEntity lead_follow;

    protected AbstractLeadFollowEvent(Object source, LeadFollowEntity lead_follow) {
        super(source);
        this.lead_follow = snapshot(lead_follow);
    }

    public LeadFollowEntity getLeadFollow() {
        return lead_follow;
    }

    private LeadFollowEntity snapshot(LeadFollowEntity source) {
        if (source == null) {
            return null;
        }
        try {
            return SerializationUtils.clone(source);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Failed to snapshot lead_follow " + source.getUid(), ex);
        }
    }
}
