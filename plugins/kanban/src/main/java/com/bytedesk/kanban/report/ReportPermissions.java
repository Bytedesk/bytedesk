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
package com.bytedesk.kanban.report;

import com.bytedesk.core.base.BasePermissions;

public class ReportPermissions extends BasePermissions {

    public static final String REPORT_PREFIX = "REPORT_";

    // 统一权限（不再在权限字符串中编码层级）
    public static final String REPORT_READ = "REPORT_READ";
    public static final String REPORT_CREATE = "REPORT_CREATE";
    public static final String REPORT_UPDATE = "REPORT_UPDATE";
    public static final String REPORT_DELETE = "REPORT_DELETE";
    public static final String REPORT_EXPORT = "REPORT_EXPORT";

    // PreAuthorize 表达式
    public static final String HAS_REPORT_READ = "hasAuthority('REPORT_READ')";
    public static final String HAS_REPORT_CREATE = "hasAuthority('REPORT_CREATE')";
    public static final String HAS_REPORT_UPDATE = "hasAuthority('REPORT_UPDATE')";
    public static final String HAS_REPORT_DELETE = "hasAuthority('REPORT_DELETE')";
    public static final String HAS_REPORT_EXPORT = "hasAuthority('REPORT_EXPORT')";

}