/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-03 08:55:36
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-09-19 16:45:20
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
    INTERNAL, // 内部工单：面向企业内部成员
    EXTERNAL; // 外部工单：面向访客/客户

    public static TicketTypeEnum fromValue(String value) {
        if (value == null || value.isBlank()) {
            return TicketTypeEnum.EXTERNAL;
        }
        for (TicketTypeEnum item : TicketTypeEnum.values()) {
            if (item.name().equalsIgnoreCase(value)) {
                return item;
            }
        }
        return TicketTypeEnum.EXTERNAL;
    }
}
