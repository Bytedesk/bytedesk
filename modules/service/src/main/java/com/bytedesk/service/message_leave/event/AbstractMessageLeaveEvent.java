/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-08 10:20:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-08 10:20:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.message_leave.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.SerializationUtils;

import com.bytedesk.service.message_leave.MessageLeaveEntity;

/**
 * Provides detached message leave snapshots for async consumers.
 */
public abstract class AbstractMessageLeaveEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final MessageLeaveEntity messageLeave;

    protected AbstractMessageLeaveEvent(Object source, MessageLeaveEntity messageLeave) {
        super(source);
        this.messageLeave = snapshot(messageLeave);
    }

    public MessageLeaveEntity getMessageLeave() {
        return messageLeave;
    }

    private MessageLeaveEntity snapshot(MessageLeaveEntity source) {
        if (source == null) {
            return null;
        }
        try {
            return SerializationUtils.clone(source);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Failed to snapshot message leave " + source.getUid(), ex);
        }
    }
}
