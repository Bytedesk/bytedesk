/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2026-03-14 00:00:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2026-03-14 00:00:00
 * @Description: bytedesk.com https://github.com/bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2026 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.ticket;

import com.bytedesk.core.base.BaseExtra;
import com.bytedesk.core.message.MessageExtra;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
public class TicketNoticeExtra extends MessageExtra {

    private String uid;
    private String title;
    private String ticketNumber;
    private String status;
    private String priority;
    private String type;
    private String reporterUid;
    private String assigneeUid;

    public static TicketNoticeExtra fromJson(String json) {
        TicketNoticeExtra result = BaseExtra.fromJson(json, TicketNoticeExtra.class);
        return result != null ? result : TicketNoticeExtra.builder().build();
    }
}