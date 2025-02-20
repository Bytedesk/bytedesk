/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:58:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-20 12:51:27
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.statistic;

import com.bytedesk.core.base.BaseResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TicketStatisticResponse extends BaseResponse {
    private String content;
    private String author;
} 