/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:58:38
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-05 10:30:00
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
import com.bytedesk.core.rbac.user.UserResponse;
import com.bytedesk.service.agent.AgentResponse;
import com.bytedesk.service.workgroup.WorkgroupResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponse extends BaseResponse {
    // 
    private String title;
    private String description;
    // 
    private String status;
    private String priority;
    // 
    private String type;
    // 
    private String threadTopic;
    private String categoryUid;
    // 
    private WorkgroupResponse workgroup;
    // 
    private AgentResponse assignee;
    // 
    private UserResponse reporter;
    // 
    private String createdAt;
    private String updatedAt;
} 