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
package com.bytedesk.ai.mcp_client;

import com.bytedesk.core.base.BasePermissions;

public class McpClientPermissions extends BasePermissions {

    // 模块前缀
    public static final String MCPCLIENT_PREFIX = "MCPCLIENT_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String MCPCLIENT_READ = "MCPCLIENT_READ";
    public static final String MCPCLIENT_CREATE = "MCPCLIENT_CREATE";
    public static final String MCPCLIENT_UPDATE = "MCPCLIENT_UPDATE";
    public static final String MCPCLIENT_DELETE = "MCPCLIENT_DELETE";
    public static final String MCPCLIENT_EXPORT = "MCPCLIENT_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_MCPCLIENT_READ = "hasAuthority('MCPCLIENT_READ')";
    public static final String HAS_MCPCLIENT_CREATE = "hasAuthority('MCPCLIENT_CREATE')";
    public static final String HAS_MCPCLIENT_UPDATE = "hasAuthority('MCPCLIENT_UPDATE')";
    public static final String HAS_MCPCLIENT_DELETE = "hasAuthority('MCPCLIENT_DELETE')";
    public static final String HAS_MCPCLIENT_EXPORT = "hasAuthority('MCPCLIENT_EXPORT')";

}
