/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-23 15:21:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-03-29 13:58:20
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
    NEW,            // 1新建 - 工单刚创建
    ASSIGNED,       // 2已分配 - 工单被分配给客服处理人
    CLAIMED,        // 3已认领 - 工单被客服主动认领
    UNCLAIMED,      // 4已退回 - 工单被处理人退回到工作组
    PROCESSING,     // 5处理中 - 处理人正在处理工单
    TRANSFERRED,    // 6已转移 - 工单被转移给其他客服处理人
    PENDING,        // 7待处理 - 等待客户响应或第三方处理
    HOLDING,        // 8挂起 - 暂停处理（如等待更多信息）
    RESUMED,        // 9恢复 - 恢复处理
    REOPENED,       // 10重新打开 - 已解决的工单被重新打开
    RESOLVED,       // 11已解决 - 工单问题已解决，等待确认
    ESCALATED,      // 12已升级 - 工单已升级到更高级别处理
    CLOSED,         // 13已关闭 - 工单处理完成并确认
    CANCELLED,      // 14已取消 - 工单被取消（如重复提交）
    VERIFIED_OK,    // 15已验证 - 客户已确认工单解决
    VERIFIED_FAIL   // 16已验证 - 客户已确认工单未解决
}
