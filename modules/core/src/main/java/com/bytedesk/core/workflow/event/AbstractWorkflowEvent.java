/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-03 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-03 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.workflow.WorkflowEntity;

/**
 * Provides a detached workflow snapshot so async listeners avoid touching
 * managed JPA entities from other threads.
 */
public abstract class AbstractWorkflowEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final WorkflowEntity workflow;

    protected AbstractWorkflowEvent(Object source, WorkflowEntity workflow) {
        super(source);
        this.workflow = snapshot(workflow);
    }

    public WorkflowEntity getWorkflow() {
        return workflow;
    }

    private WorkflowEntity snapshot(WorkflowEntity source) {
        if (source == null) {
            return null;
        }
        try {
            return SerializationUtils.clone(source);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Failed to snapshot workflow " + source.getUid(), ex);
        }
    }
}
