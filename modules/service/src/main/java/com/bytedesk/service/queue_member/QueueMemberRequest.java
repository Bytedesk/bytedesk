/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-14 17:57:08
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-01-18 23:45:12
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.time.LocalDateTime;

import com.bytedesk.core.base.BaseRequest;

import jakarta.persistence.Column;
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
public class QueueMemberRequest extends BaseRequest {

    private String queueUid;  // 关联队列

    private String queueNickname;  // 队列名称

    private String queueTopic;  // 队列主题，用于查询

    private String queueDay;  // 队列日期，用于查询

    private String threadUid;  // 关联会话

    private String visitorUid;  // 访客ID

    private String visitorNickname;  // 访客昵称

    private String visitorAvatar;  // 访客头像

    private String agentUid;  // 服务客服ID

    private String agentNickname;  // 客服昵称

    private String agentAvatar;  // 客服头像

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

    @Builder.Default
    private int avgResponseTime = 0;  // 平均响应时间(秒)
    
    @Builder.Default
    private int maxResponseTime = 0;  // 最长响应时间(秒)

    @Builder.Default
    private int agentMessageCount = 0;  // 客服消息数量

    @Builder.Default
    private int visitorMessageCount = 0;  // 访客消息数量

    @Builder.Default
    @Column(name = "is_timeout")
    private boolean timeout = false; // 是否超时

    private LocalDateTime lastResponseTime;  // 最后响应时间

    private LocalDateTime leaveTime;  // 离开时间

    private LocalDateTime closeTime;  // 结束时间

    @Builder.Default
    private int priority = 0;  // 优先级(0-100)

    // 已解决
    @Builder.Default
    private boolean solved = false;

    // 已评价
    @Builder.Default
    private boolean rated = false;

    private String client;  // 客户端类型
}
