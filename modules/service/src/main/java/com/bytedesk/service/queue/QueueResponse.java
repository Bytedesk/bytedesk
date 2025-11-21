/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-03-22 23:04:12
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-14 22:19:39
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import java.util.List;
import java.util.ArrayList;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.thread.enums.ThreadTypeEnum;

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
public class QueueResponse extends BaseResponse {
    
    private static final long serialVersionUID = 1L;

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
    private Integer totalCount = 0;  // 今日请求服务人数，当前排队号码

    @Builder.Default
    private Integer robotingCount = 0;  // 机器人服务中人次

    @Builder.Default
    private Integer offlineCount = 0;  // 客服离线人次

    @Builder.Default
    private Integer leaveMsgCount = 0;  // 留言数

    @Builder.Default
    private Integer robotToAgentCount = 0;  // 转人工数

    @Builder.Default
    private Integer queuingCount = 0;  // 排队中人数

    @Builder.Default
    private Integer chattingCount = 0;  // 正在服务人数

    @Builder.Default
    private Integer closedCount = 0;  // 对话结束人数

    @Builder.Default
    private Integer avgWaitTime = 0;  // 平均等待时间(秒)

    @Builder.Default
    private Integer avgResolveTime = 0;  // 平均解决时间(秒)

    @Builder.Default
    private List<Integer> threadsCountByHour = new ArrayList<>();  // 每小时接待人数
}
