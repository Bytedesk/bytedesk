/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:04:03
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 22:20:11
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

    private static final long serialVersionUID = 1L;

    // 队列名称
    private String nickname;

    // 队列类型
    // @Builder.Default
    // private String type = ThreadTypeEnum.WORKGROUP.name();  

    // 区别于thread topic，此处的topic是队列的主题，用于访客监听排队人数变化
    private String topic;

    // 队列日期(YYYY-MM-DD)
    private String day;

    // 队列状态
    @Builder.Default
    private String status = QueueStatusEnum.ACTIVE.name();  // 队列状态

    // 客服端根据agentUid拉取自己统计数据
    private String agentUid;  // 客服uid
}
