/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:58:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-23 17:01:11
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.service.agent.AgentResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse extends BaseResponse {

    private String title;
    private String description;
    // 
    private String status;
    private String priority;
    // 
    private String categoryUid;
    // 
    private AgentResponse assignee;
    // 
    private AgentResponse reporter;
    // 
    private String createdAt;
    // 
    private String updatedAt;
    
} 