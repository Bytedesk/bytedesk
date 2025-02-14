/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-11-05 16:58:18
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-14 17:26:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.process;

public class TicketProcessPermissions {

    public static final String TICKET_PROCESS_PREFIX = "TICKET_PROCESS_";
    // TicketProcess permissions
    public static final String TICKET_PROCESS_CREATE = "hasAuthority('TICKET_PROCESS_CREATE')";
    public static final String TICKET_PROCESS_READ = "hasAuthority('TICKET_PROCESS_READ')";
    public static final String TICKET_PROCESS_UPDATE = "hasAuthority('TICKET_PROCESS_UPDATE')";
    public static final String TICKET_PROCESS_DELETE = "hasAuthority('TICKET_PROCESS_DELETE')";
    public static final String TICKET_PROCESS_EXPORT = "hasAuthority('TICKET_PROCESS_EXPORT')";

    // 
    
    
}