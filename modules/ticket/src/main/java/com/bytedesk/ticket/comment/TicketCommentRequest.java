/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:58:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-21 10:00:37
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.comment;

import com.bytedesk.core.base.BaseRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TicketCommentRequest extends BaseRequest {
    private static final long serialVersionUID = 1L;

    private String content;
    private String author;
} 