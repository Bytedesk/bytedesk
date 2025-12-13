/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-08 09:30:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-08 09:30:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.agent.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.SerializationUtils;

import com.bytedesk.service.agent.AgentEntity;

/**
 * Provides a detached snapshot of the agent so async listeners avoid touching
 * managed persistence contexts owned by other threads.
 */
public abstract class AbstractAgentEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final AgentEntity agent;

    protected AbstractAgentEvent(Object source, AgentEntity agent) {
        super(source);
        this.agent = snapshot(agent);
    }

    public AgentEntity getAgent() {
        return agent;
    }

    private AgentEntity snapshot(AgentEntity source) {
        if (source == null) {
            return null;
        }
        try {
            return SerializationUtils.clone(source);
        } catch (RuntimeException ex) {
            String uid = source.getUid() != null ? source.getUid() : "<unknown>";
            throw new IllegalStateException("Failed to snapshot agent " + uid, ex);
        }
    }
}
