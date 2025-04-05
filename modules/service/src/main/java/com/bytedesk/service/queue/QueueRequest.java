/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:04:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-05 10:38:35
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

    // 队列类型
    @Builder.Default
    private String type = ThreadTypeEnum.WORKGROUP.name();  

    // 区别于thread topic，此处的topic是队列的主题，用于访客监听排队人数变化
    private String topic;

    // 队列日期(YYYY-MM-DD)
    private String day;

    // 队列状态
    @Builder.Default
    private String status = QueueStatusEnum.ACTIVE.name();  // 队列状态

    @Builder.Default
    private Integer newCount = 0;  // 今日请求服务人数，当前排队号码

    @Builder.Default
    private Integer queuingCount = 0;  // 排队中人数

    @Builder.Default
    private Integer chattingCount = 0;  // 正在服务人数

    @Builder.Default
    private Integer offlineCount = 0;  // 请求时，客服离线或非接待状态的请求人次

    @Builder.Default
    private Integer closedCount = 0;  // 对话结束人数

    @Builder.Default
    private Integer avgWaitTime = 0;  // 平均等待时间(秒)

    @Builder.Default
    private Integer avgResolveTime = 0;  // 平均解决时间(秒)

    
}
