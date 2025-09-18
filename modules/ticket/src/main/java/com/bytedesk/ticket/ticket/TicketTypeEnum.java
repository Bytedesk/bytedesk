/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-03 08:55:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-18 16:30:57
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

public enum TicketTypeEnum {
    AGENT, // agent 处理的工单
    DEPARTMENT, // department 部门工单
    WORKGROUP, // workgroup 工作组工单
    USER, // user 提交的工单
    VISITOR // visitor 提交的工单
}
