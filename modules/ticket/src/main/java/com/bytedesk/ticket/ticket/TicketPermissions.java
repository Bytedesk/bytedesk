/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-03 13:35:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-11-29 12:00:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import com.bytedesk.core.base.BasePermissions;

public class TicketPermissions extends BasePermissions {

    // 模块前缀
    public static final String TICKET_PREFIX = "TICKET_";

    // 统一权限（不区分层级）
    public static final String TICKET_READ = "TICKET_READ";
    public static final String TICKET_CREATE = "TICKET_CREATE";
    public static final String TICKET_UPDATE = "TICKET_UPDATE";
    public static final String TICKET_DELETE = "TICKET_DELETE";
    public static final String TICKET_EXPORT = "TICKET_EXPORT";

    // PreAuthorize 表达式 - 统一权限（不区分层级）
    public static final String HAS_TICKET_READ = "hasAuthority('TICKET_READ')";
    public static final String HAS_TICKET_CREATE = "hasAuthority('TICKET_CREATE')";
    public static final String HAS_TICKET_UPDATE = "hasAuthority('TICKET_UPDATE')";
    public static final String HAS_TICKET_DELETE = "hasAuthority('TICKET_DELETE')";
    public static final String HAS_TICKET_EXPORT = "hasAuthority('TICKET_EXPORT')";

}