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
package com.bytedesk.ai.mcp_client.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.util.SerializationUtils;

import com.bytedesk.ai.mcp_client.McpClientEntity;

/**
 * Ensures every mcp_client-related application event carries a detached snapshot so
 * async listeners never touch managed persistence contexts from other threads.
 */
public abstract class AbstractMcpClientEvent extends ApplicationEvent {

    private static final long serialVersionUID = 1L;

    private final McpClientEntity mcp_client;

    protected AbstractMcpClientEvent(Object source, McpClientEntity mcp_client) {
        super(source);
        this.mcp_client = snapshot(mcp_client);
    }

    public McpClientEntity getMcpClient() {
        return mcp_client;
    }

    private McpClientEntity snapshot(McpClientEntity source) {
        if (source == null) {
            return null;
        }
        try {
            return SerializationUtils.clone(source);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Failed to snapshot mcp_client " + source.getUid(), ex);
        }
    }
}
