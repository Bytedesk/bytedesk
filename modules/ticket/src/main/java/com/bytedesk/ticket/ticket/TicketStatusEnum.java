/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 15:21:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-23 16:19:45
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

// 工单状态
public enum TicketStatusEnum {
    NEW,            // 新建
    ASSIGNED,       // 已分配
    IN_PROGRESS,    // 处理中
    PENDING,        // 待处理（等待客户响应/等待第三方）
    ON_HOLD,        // 挂起（暂停处理）
    REOPENED,       // 重新打开
    RESOLVED,       // 已解决
    CLOSED,         // 已关闭
    CANCELLED;      // 已取消
}
