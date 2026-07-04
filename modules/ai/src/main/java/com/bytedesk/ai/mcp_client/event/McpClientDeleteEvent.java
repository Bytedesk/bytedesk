/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-25 12:31:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-25 12:31:19
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.mcp_client.event;

import com.bytedesk.ai.mcp_client.McpClientEntity;

/**
 * Event published when a mcp_client is deleted.
 */
public class McpClientDeleteEvent extends AbstractMcpClientEvent {

    private static final long serialVersionUID = 1L;

    public McpClientDeleteEvent(McpClientEntity mcp_client) {
        super(mcp_client, mcp_client);
    }
}
