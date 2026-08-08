/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-05-06 11:55:32
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ai.mcp_server;

import com.bytedesk.core.base.BasePermissions;

public class McpServerPermissions extends BasePermissions {

    // 模块前缀
    public static final String MCPSERVER_PREFIX = "MCPSERVER_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String MCPSERVER_READ = "MCPSERVER_READ";
    public static final String MCPSERVER_CREATE = "MCPSERVER_CREATE";
    public static final String MCPSERVER_UPDATE = "MCPSERVER_UPDATE";
    public static final String MCPSERVER_DELETE = "MCPSERVER_DELETE";
    public static final String MCPSERVER_EXPORT = "MCPSERVER_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_MCPSERVER_READ = "hasAuthority('MCPSERVER_READ')";
    public static final String HAS_MCPSERVER_CREATE = "hasAuthority('MCPSERVER_CREATE')";
    public static final String HAS_MCPSERVER_UPDATE = "hasAuthority('MCPSERVER_UPDATE')";
    public static final String HAS_MCPSERVER_DELETE = "hasAuthority('MCPSERVER_DELETE')";
    public static final String HAS_MCPSERVER_EXPORT = "hasAuthority('MCPSERVER_EXPORT')";

}
