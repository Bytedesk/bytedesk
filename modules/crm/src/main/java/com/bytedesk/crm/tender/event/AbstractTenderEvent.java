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
package com.bytedesk.crm.tender.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.SerializationUtils;

import com.bytedesk.crm.tender.TenderEntity;

/**
 * Ensures every tender-related application event carries a detached snapshot so
 * async listeners never touch managed persistence contexts from other threads.
 */
public abstract class AbstractTenderEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final TenderEntity tender;

    protected AbstractTenderEvent(Object source, TenderEntity tender) {
        super(source);
        this.tender = snapshot(tender);
    }

    public TenderEntity getTender() {
        return tender;
    }

    private TenderEntity snapshot(TenderEntity source) {
        if (source == null) {
            return null;
        }
        try {
            return SerializationUtils.clone(source);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Failed to snapshot tender " + source.getUid(), ex);
        }
    }
}
