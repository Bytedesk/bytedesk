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
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.open_platform.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.SerializationUtils;

import com.bytedesk.core.open_platform.OpenPlatformEntity;

public abstract class AbstractOpenPlatformEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final OpenPlatformEntity openPlatform;

    protected AbstractOpenPlatformEvent(Object source, OpenPlatformEntity openPlatform) {
        super(source);
        this.openPlatform = snapshot(openPlatform);
    }

    public OpenPlatformEntity getOpenPlatform() {
        return openPlatform;
    }

    private OpenPlatformEntity snapshot(OpenPlatformEntity source) {
        if (source == null) {
            return null;
        }
        try {
            return SerializationUtils.clone(source);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Failed to snapshot openPlatform " + source.getUid(), ex);
        }
    }
}
