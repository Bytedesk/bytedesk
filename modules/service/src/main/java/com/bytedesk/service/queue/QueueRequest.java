/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:04:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2024-12-24 12:34:18
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import com.bytedesk.core.base.BaseRequest;
import com.bytedesk.core.thread.ThreadTypeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class QueueRequest extends BaseRequest {

    @Builder.Default
    private int currentNumber = 0;

    @Builder.Default
    private int waitingNumber = 0;

    @Builder.Default
    private int waitSeconds = 0;
    
    @Builder.Default
    private int servingNumber = 0;

    @Builder.Default
    private int finishedNumber = 0;

    // 队列状态
    @Builder.Default
    private String status = QueueStatusEnum.ACTIVE.name();  // 队列状态

    private String topic;

    private String day;

    @Builder.Default
    private String type = ThreadTypeEnum.WORKGROUP.name();  
}
