/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-04-02 14:13:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-22 15:55:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.process;

/**
 * 工单流程状态枚举
 */
public enum TicketProcessStatusEnum {
    /**
     * 草稿状态
     */
    DRAFT,
    
    /**
     * 已部署状态
     */
    DEPLOYED,

    /**
     * 已禁用状态
     */
    DISABLED
}
