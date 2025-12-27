/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-14 17:37:24
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.workflow;

import com.bytedesk.core.base.BasePermissions;

public class WorkflowPermissions extends BasePermissions {

    // 模块前缀
    public static final String WORKFLOW_PREFIX = "WORKFLOW_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String WORKFLOW_READ = "WORKFLOW_READ";
    public static final String WORKFLOW_CREATE = "WORKFLOW_CREATE";
    public static final String WORKFLOW_UPDATE = "WORKFLOW_UPDATE";
    public static final String WORKFLOW_DELETE = "WORKFLOW_DELETE";
    public static final String WORKFLOW_EXPORT = "WORKFLOW_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_WORKFLOW_READ = "hasAuthority('WORKFLOW_READ')";
    public static final String HAS_WORKFLOW_CREATE = "hasAuthority('WORKFLOW_CREATE')";
    public static final String HAS_WORKFLOW_UPDATE = "hasAuthority('WORKFLOW_UPDATE')";
    public static final String HAS_WORKFLOW_DELETE = "hasAuthority('WORKFLOW_DELETE')";
    public static final String HAS_WORKFLOW_EXPORT = "hasAuthority('WORKFLOW_EXPORT')";

}