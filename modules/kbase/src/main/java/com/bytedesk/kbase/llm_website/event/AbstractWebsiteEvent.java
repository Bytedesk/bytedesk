/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-12-16 10:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-12-16 10:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM –
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE
 *  contact: 270580156@qq.com
 *  联系：270580156@qq.com
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved.
 */
package com.bytedesk.kbase.llm_website.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.SerializationUtils;

import com.bytedesk.kbase.llm_website.WebsiteEntity;

/**
 * Publishes detached website snapshots for safe async handling.
 */
public abstract class AbstractWebsiteEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final WebsiteEntity website;

    protected AbstractWebsiteEvent(Object source, WebsiteEntity website) {
        super(source);
        this.website = snapshot(website);
    }

    public WebsiteEntity getWebsite() {
        return website;
    }

    private WebsiteEntity snapshot(WebsiteEntity source) {
        if (source == null) {
            return null;
        }
        try {
            return SerializationUtils.clone(source);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Failed to snapshot website " + source.getUid(), ex);
        }
    }
}
