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
package com.bytedesk.kanban.project_invite;

import com.bytedesk.core.base.BasePermissions;

public class ProjectInvitePermissions extends BasePermissions {

    public static final String PROJECT_INVITE_PREFIX = "PROJECT_INVITE_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String PROJECT_INVITE_READ = "PROJECT_INVITE_READ";
    public static final String PROJECT_INVITE_CREATE = "PROJECT_INVITE_CREATE";
    public static final String PROJECT_INVITE_UPDATE = "PROJECT_INVITE_UPDATE";
    public static final String PROJECT_INVITE_DELETE = "PROJECT_INVITE_DELETE";
    public static final String PROJECT_INVITE_EXPORT = "PROJECT_INVITE_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_PROJECT_INVITE_READ = "hasAuthority('PROJECT_INVITE_READ')";
    public static final String HAS_PROJECT_INVITE_CREATE = "hasAuthority('PROJECT_INVITE_CREATE')";
    public static final String HAS_PROJECT_INVITE_UPDATE = "hasAuthority('PROJECT_INVITE_UPDATE')";
    public static final String HAS_PROJECT_INVITE_DELETE = "hasAuthority('PROJECT_INVITE_DELETE')";
    public static final String HAS_PROJECT_INVITE_EXPORT = "hasAuthority('PROJECT_INVITE_EXPORT')";

}