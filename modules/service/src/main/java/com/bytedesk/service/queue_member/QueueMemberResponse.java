/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-14 17:57:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-01 15:38:00
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  仅支持企业内部员工自用，严禁私自用于销售、二次销售或者部署SaaS方式销售 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.time.LocalDateTime;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueueMemberResponse extends BaseResponse {

    private String queueUid;  // 关联队列

    private String threadUid;  // 关联会话

    private String visitorUid;  // 访客ID

    @Builder.Default
    private int beforeNumber = 0;  // 前面排队人数

    @Builder.Default
    private int waitTime = 0;  // 等待时间(秒)

    @Builder.Default
    private int queueNumber = 1;  // 排队号码

    @Builder.Default
    private String status = QueueMemberStatusEnum.WAITING.name();  // 成员状态

    @Builder.Default
    private LocalDateTime enqueueTime = LocalDateTime.now();  // 加入时间

    @Builder.Default
    private String acceptType = QueueMemberAcceptTypeEnum.AUTO.name();  // 接单方式

    private LocalDateTime acceptTime;  // 开始服务时间

    private LocalDateTime firstResponseTime;  // 首次响应时间

    @Builder.Default
    private boolean firstResponse = false;  // 是否首次响应

    private LocalDateTime closeTime;  // 结束时间

    private String agentUid;  // 服务客服

    @Builder.Default
    private int priority = 0;  // 优先级(0-100)

    // 已解决
    @Builder.Default
    private boolean solved = false;

    // 已评价
    @Builder.Default
    private boolean rated = false;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
}
