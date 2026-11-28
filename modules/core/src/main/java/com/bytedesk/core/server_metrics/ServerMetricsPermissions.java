/*
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com. All Rights Reserved. 
 */
package com.bytedesk.core.server_metrics;

import com.bytedesk.core.base.BasePermissions;

public class ServerMetricsPermissions extends BasePermissions {

    // 模块前缀
    public static final String SERVER_METRICS_PREFIX = "SERVER_METRICS_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "SERVER_METRICS";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String SERVER_METRICS_READ = "SERVER_METRICS_READ";
    public static final String SERVER_METRICS_CREATE = "SERVER_METRICS_CREATE";
    public static final String SERVER_METRICS_UPDATE = "SERVER_METRICS_UPDATE";
    public static final String SERVER_METRICS_DELETE = "SERVER_METRICS_DELETE";
    public static final String SERVER_METRICS_EXPORT = "SERVER_METRICS_EXPORT";

    // PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_SERVER_METRICS_READ = "hasAuthority('" + SERVER_METRICS_READ + "')";
    public static final String HAS_SERVER_METRICS_CREATE = "hasAuthority('" + SERVER_METRICS_CREATE + "')";
    public static final String HAS_SERVER_METRICS_UPDATE = "hasAuthority('" + SERVER_METRICS_UPDATE + "')";
    public static final String HAS_SERVER_METRICS_DELETE = "hasAuthority('" + SERVER_METRICS_DELETE + "')";
    public static final String HAS_SERVER_METRICS_EXPORT = "hasAuthority('" + SERVER_METRICS_EXPORT + "')";

}
