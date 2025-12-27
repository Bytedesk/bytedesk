/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2025-03-08 10:32:22
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.kanban.project;

import com.bytedesk.core.base.BasePermissions;

public class ProjectPermissions extends BasePermissions {

    public static final String PROJECT_PREFIX = "PROJECT_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String PROJECT_READ = "PROJECT_READ";
    public static final String PROJECT_CREATE = "PROJECT_CREATE";
    public static final String PROJECT_UPDATE = "PROJECT_UPDATE";
    public static final String PROJECT_DELETE = "PROJECT_DELETE";
    public static final String PROJECT_EXPORT = "PROJECT_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_PROJECT_READ = "hasAuthority('PROJECT_READ')";
    public static final String HAS_PROJECT_CREATE = "hasAuthority('PROJECT_CREATE')";
    public static final String HAS_PROJECT_UPDATE = "hasAuthority('PROJECT_UPDATE')";
    public static final String HAS_PROJECT_DELETE = "hasAuthority('PROJECT_DELETE')";
    public static final String HAS_PROJECT_EXPORT = "hasAuthority('PROJECT_EXPORT')";

}