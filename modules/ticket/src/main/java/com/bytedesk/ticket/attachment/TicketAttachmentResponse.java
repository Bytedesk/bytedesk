/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-02-06 12:20:07
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-06 15:43:56
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.attachment;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.upload.UploadResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class TicketAttachmentResponse extends BaseResponse {
    
    private static final long serialVersionUID = 1L;


    // 防止循环引用
    // private TicketResponse ticket;

    // private TicketCommentResponse comment;
    
    private UploadResponse upload;
}
