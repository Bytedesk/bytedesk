/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-06-23 10:16:15
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-08-05 19:09:51
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.thread_transfer;

import java.time.LocalDateTime;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.message.MessageStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ThreadTransferResponse extends BaseResponse {
    
    private String sender;

    private String receiver;

    private String note;

    private ThreadTransferTypeEnum type;

    private MessageStatusEnum status;

    private String threadTopic;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
