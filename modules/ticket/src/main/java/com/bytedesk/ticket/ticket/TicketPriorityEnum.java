/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 15:22:59
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-23 16:20:41
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

// 工单优先级
public enum TicketPriorityEnum {
    LOWEST,         // 最低 - 可以延后处理的问题
    LOW,            // 低 - 不急迫的问题
    MEDIUM,         // 中 - 标准优先级
    HIGH,           // 高 - 重要且紧急
    URGENT,         // 紧急 - 需要立即处理
    CRITICAL;       // 严重 - 影响核心业务/系统运行
}
