/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-01-17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-01-17
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.SerializationUtils;

import com.bytedesk.service.queue_member.QueueMemberEntity;

/**
 * Ensures every queue-member related application event carries a detached snapshot so
 * async listeners never touch managed persistence contexts from other threads.
 */
public abstract class AbstractQueueMemberEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final QueueMemberEntity member;

    protected AbstractQueueMemberEvent(Object source, QueueMemberEntity member) {
        super(source);
        this.member = snapshot(member);
    }

    public QueueMemberEntity getMember() {
        return member;
    }

    private QueueMemberEntity snapshot(QueueMemberEntity source) {
        if (source == null) {
            return null;
        }
        try {
            return SerializationUtils.clone(source);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Failed to snapshot queue member " + source.getUid(), ex);
        }
    }
}
