/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:04:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-02 18:19:46
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class QueueResponse extends BaseResponse {

    private String nickname;

    private String type;

    private String topic;

    private String day;

    private String status;

    private Integer currentNumber;  // 当前排队号码

    private Integer waitingNumber;  // 等待人数

    private Integer servingNumber;  // 正在服务人数

    private Integer servedNumber;  // 已完成人数

    private Integer avgWaitTime;  // 平均等待时间(秒)

    private Integer avgSolveTime;  // 平均解决时间(秒)

    // private LocalDateTime createdAt;

    // private LocalDateTime updatedAt;
}
