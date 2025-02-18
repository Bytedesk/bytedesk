
/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-18 17:03:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-18 17:03:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.message.event;

import java.util.Date;

import com.bytedesk.core.rbac.user.UserProtobuf;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TicketMessageEvent {
    private String ticketUid;
    private String processInstanceId;
    private TicketMessageType type;
    private UserProtobuf assignee;
    private String description;
    private Date createTime;
}