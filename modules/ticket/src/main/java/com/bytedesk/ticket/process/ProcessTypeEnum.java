/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-23 17:02:46
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-01 14:04:14
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.process;

public enum ProcessTypeEnum {
    /**
     * 内部工单流程 - 用于创建和处理客户服务工单
     */
    TICKET_INTERNAL,
    /**
     * 外部工单流程 - 用于访客提交的工单
     */
    TICKET_EXTERNAL,
    /**
     * 会话流程 - 用于客服会话管理
     */
    THREAD,
    /**
     * 演示流程 - 用于展示工作流能力（请假、报销、IT支持等）
     */
    DEMO,
    // FLOW
}
