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

public class ReportPermissions {

    public static final String REPORT_PREFIX = "REPORT_";
    // Report permissions
    public static final String REPORT_CREATE = "hasAuthority('REPORT_CREATE')";
    public static final String REPORT_READ = "hasAuthority('REPORT_READ')";
    public static final String REPORT_UPDATE = "hasAuthority('REPORT_UPDATE')";
    public static final String REPORT_DELETE = "hasAuthority('REPORT_DELETE')";
    public static final String REPORT_EXPORT = "hasAuthority('REPORT_EXPORT')";

    // 
    public static final String REPORT_ANY = "hasAnyAuthority('REPORT_CREATE', 'REPORT_READ', 'REPORT_UPDATE', 'REPORT_EXPORT', 'REPORT_DELETE')";
    
}