/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-03 13:35:22
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-03 13:35:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

public class TicketPermissions {

    public static final String TICKET_PREFIX = "TICKET_";

    public static final String TICKET_CREATE = "hasAuthority('TICKET_CREATE')";
    public static final String TICKET_READ = "hasAuthority('TICKET_READ')";
    public static final String TICKET_UPDATE = "hasAuthority('TICKET_UPDATE')";
    public static final String TICKET_DELETE = "hasAuthority('TICKET_DELETE')";
    public static final String TICKET_EXPORT = "hasAuthority('TICKET_EXPORT')";

}
