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
package com.bytedesk.core.workflow_log;

import com.bytedesk.core.base.BasePermissions;

public class WorkflowLogPermissions extends BasePermissions {

    // 模块前缀
    public static final String WORKFLOW_LOG_PREFIX = "WORKFLOW_LOG_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String WORKFLOW_LOG_READ = "WORKFLOW_LOG_READ";
    public static final String WORKFLOW_LOG_CREATE = "WORKFLOW_LOG_CREATE";
    public static final String WORKFLOW_LOG_UPDATE = "WORKFLOW_LOG_UPDATE";
    public static final String WORKFLOW_LOG_DELETE = "WORKFLOW_LOG_DELETE";
    public static final String WORKFLOW_LOG_EXPORT = "WORKFLOW_LOG_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_WORKFLOW_LOG_READ = "hasAuthority('WORKFLOW_LOG_READ')";
    public static final String HAS_WORKFLOW_LOG_CREATE = "hasAuthority('WORKFLOW_LOG_CREATE')";
    public static final String HAS_WORKFLOW_LOG_UPDATE = "hasAuthority('WORKFLOW_LOG_UPDATE')";
    public static final String HAS_WORKFLOW_LOG_DELETE = "hasAuthority('WORKFLOW_LOG_DELETE')";
    public static final String HAS_WORKFLOW_LOG_EXPORT = "hasAuthority('WORKFLOW_LOG_EXPORT')";

}
