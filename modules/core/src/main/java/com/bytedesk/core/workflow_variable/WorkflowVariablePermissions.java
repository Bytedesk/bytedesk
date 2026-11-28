/*
 * @Author: jackning 270580156@qq.com
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * Copyright (c) 2024 by bytedesk.com. All Rights Reserved. 
 */
package com.bytedesk.core.workflow_variable;

import com.bytedesk.core.base.BasePermissions;

public class WorkflowVariablePermissions extends BasePermissions {

    // 模块前缀
    public static final String WORKFLOW_VARIABLE_PREFIX = "WORKFLOW_VARIABLE_";

    // 模块名称，用于权限检查
    public static final String MODULE_NAME = "WORKFLOW_VARIABLE";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String WORKFLOW_VARIABLE_READ = "WORKFLOW_VARIABLE_READ";
    public static final String WORKFLOW_VARIABLE_CREATE = "WORKFLOW_VARIABLE_CREATE";
    public static final String WORKFLOW_VARIABLE_UPDATE = "WORKFLOW_VARIABLE_UPDATE";
    public static final String WORKFLOW_VARIABLE_DELETE = "WORKFLOW_VARIABLE_DELETE";
    public static final String WORKFLOW_VARIABLE_EXPORT = "WORKFLOW_VARIABLE_EXPORT";

    // PreAuthorize 表达式（兼容：ConvertUtils 会为新旧权限互相补齐别名）
    public static final String HAS_WORKFLOW_VARIABLE_READ = "hasAuthority('" + WORKFLOW_VARIABLE_READ + "')";
    public static final String HAS_WORKFLOW_VARIABLE_CREATE = "hasAuthority('" + WORKFLOW_VARIABLE_CREATE + "')";
    public static final String HAS_WORKFLOW_VARIABLE_UPDATE = "hasAuthority('" + WORKFLOW_VARIABLE_UPDATE + "')";
    public static final String HAS_WORKFLOW_VARIABLE_DELETE = "hasAuthority('" + WORKFLOW_VARIABLE_DELETE + "')";
    public static final String HAS_WORKFLOW_VARIABLE_EXPORT = "hasAuthority('" + WORKFLOW_VARIABLE_EXPORT + "')";

}
