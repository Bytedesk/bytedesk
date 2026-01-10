/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 17:07:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.core.department;

import com.bytedesk.core.base.BasePermissions;

public class DepartmentPermissions extends BasePermissions {

    public static final String MODULE_NAME = "DEPARTMENT";

    // 模块前缀
    public static final String DEPARTMENT_PREFIX = "DEPARTMENT_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String DEPARTMENT_READ = "DEPARTMENT_READ";
    public static final String DEPARTMENT_CREATE = "DEPARTMENT_CREATE";
    public static final String DEPARTMENT_UPDATE = "DEPARTMENT_UPDATE";
    public static final String DEPARTMENT_DELETE = "DEPARTMENT_DELETE";
    public static final String DEPARTMENT_EXPORT = "DEPARTMENT_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_DEPARTMENT_READ = "hasAuthority('DEPARTMENT_READ')";
    // 用于工单详情等“只读展示”场景：允许拥有工单读取权限的用户获取部门详情
    public static final String HAS_DEPARTMENT_READ_OR_TICKET_READ = "hasAuthority('DEPARTMENT_READ') or hasAuthority('TICKET_READ')";
    public static final String HAS_DEPARTMENT_CREATE = "hasAuthority('DEPARTMENT_CREATE')";
    public static final String HAS_DEPARTMENT_UPDATE = "hasAuthority('DEPARTMENT_UPDATE')";
    public static final String HAS_DEPARTMENT_DELETE = "hasAuthority('DEPARTMENT_DELETE')";
    public static final String HAS_DEPARTMENT_EXPORT = "hasAuthority('DEPARTMENT_EXPORT')";

}