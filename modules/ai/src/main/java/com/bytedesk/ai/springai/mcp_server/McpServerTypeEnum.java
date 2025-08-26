/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-23 17:02:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-08-26 11:16:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.springai.mcp_server;

public enum McpServerTypeEnum {
    /**
     * Knowledge base MCP server
     */
    KNOWLEDGE,
    
    /**
     * File system MCP server
     */
    FILESYSTEM,
    
    /**
     * Database MCP server
     */
    DATABASE,
    
    /**
     * Web search MCP server
     */
    SEARCH,
    
    /**
     * Custom MCP server
     */
    CUSTOM
}
