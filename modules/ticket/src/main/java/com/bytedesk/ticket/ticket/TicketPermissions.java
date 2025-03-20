/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-03 13:35:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-20 11:34:27
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

    public static final String TICKET_PREFIX = "TICKET_";
    // 
    public static final String TICKET_CREATE = formatAuthority(TICKET_PREFIX + "CREATE");
    public static final String TICKET_READ = formatAuthority(TICKET_PREFIX + "READ");
    public static final String TICKET_UPDATE = formatAuthority(TICKET_PREFIX + "UPDATE");
    public static final String TICKET_DELETE = formatAuthority(TICKET_PREFIX + "DELETE");
    public static final String TICKET_EXPORT = formatAuthority(TICKET_PREFIX + "EXPORT");
    // 
    public static final String TICKET_ANY = formatAnyAuthority(TICKET_PREFIX + "CREATE", TICKET_PREFIX + "READ", TICKET_PREFIX + "UPDATE", TICKET_PREFIX + "EXPORT", TICKET_PREFIX + "DELETE");

}