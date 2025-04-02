/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:04:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 18:19:19
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
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class QueueRequest extends BaseRequest {

    // 队列名称
    private String nickname;

    @Builder.Default
    private String type = ThreadTypeEnum.WORKGROUP.name();  

    // agentUid or workgroupUid
    private String topic;

    // 队列日期(YYYY-MM-DD)
    private String day;  

    // 队列状态
    @Builder.Default
    private String status = QueueStatusEnum.ACTIVE.name();  // 队列状态


    @Builder.Default
    private Integer currentNumber = 0;  // 当前排队号码

    @Builder.Default
    private Integer waitingNumber = 0;  // 等待人数

    @Builder.Default
    private Integer servingNumber = 0;  // 正在服务人数

    @Builder.Default
    private Integer servedNumber = 0;  // 已完成人数

    @Builder.Default
    private Integer avgWaitTime = 0;  // 平均等待时间(秒)

    @Builder.Default
    private Integer avgSolveTime = 0;  // 平均解决时间(秒)

    
}
