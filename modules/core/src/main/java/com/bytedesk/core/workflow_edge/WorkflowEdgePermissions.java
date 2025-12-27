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
package com.bytedesk.core.workflow_edge;

import com.bytedesk.core.base.BasePermissions;

public class WorkflowEdgePermissions extends BasePermissions {

    // 模块前缀
    public static final String WORKFLOW_EDGE_PREFIX = "WORKFLOW_EDGE_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String WORKFLOW_EDGE_READ = "WORKFLOW_EDGE_READ";
    public static final String WORKFLOW_EDGE_CREATE = "WORKFLOW_EDGE_CREATE";
    public static final String WORKFLOW_EDGE_UPDATE = "WORKFLOW_EDGE_UPDATE";
    public static final String WORKFLOW_EDGE_DELETE = "WORKFLOW_EDGE_DELETE";
    public static final String WORKFLOW_EDGE_EXPORT = "WORKFLOW_EDGE_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_WORKFLOW_EDGE_READ = "hasAuthority('WORKFLOW_EDGE_READ')";
    public static final String HAS_WORKFLOW_EDGE_CREATE = "hasAuthority('WORKFLOW_EDGE_CREATE')";
    public static final String HAS_WORKFLOW_EDGE_UPDATE = "hasAuthority('WORKFLOW_EDGE_UPDATE')";
    public static final String HAS_WORKFLOW_EDGE_DELETE = "hasAuthority('WORKFLOW_EDGE_DELETE')";
    public static final String HAS_WORKFLOW_EDGE_EXPORT = "hasAuthority('WORKFLOW_EDGE_EXPORT')";

}
