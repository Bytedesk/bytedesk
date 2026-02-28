/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-02-22 16:12:53
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-28 10:08:23
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客服队列统计响应
 * 用于前端显示客服的完整队列统计信息
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueueAgentStatsResponse {
    
    /**
     * 客服UID
     */
    private String agentUid;

    /**
     * 今日服务总人次
     */
    @Builder.Default
    private Integer totalCount = 0;

    /**
     * 当前排队中人数（包括一对一和工作组未分配的）
     */
    @Builder.Default
    private Integer queuingCount = 0;

    /**
     * 一对一排队人数
     */
    @Builder.Default
    private Integer directQueuingCount = 0;

    /**
     * 工作组未分配排队人数
     */
    @Builder.Default
    private Integer workgroupUnassignedCount = 0;

    /**
     * 正在服务中人数
     */
    @Builder.Default
    private Integer chattingCount = 0;

    /**
     * 客服离线时的请求人数
     */
    @Builder.Default
    private Integer offlineCount = 0;

    /**
     * 已结束会话人数
     */
    @Builder.Default
    private Integer closedCount = 0;

    /**
     * 留言数
     */
    @Builder.Default
    private Integer leaveMsgCount = 0;

    /**
     * 转人工数
     */
    @Builder.Default
    private Integer robotToAgentCount = 0;

    /**
     * 机器人服务中人次
     */
    @Builder.Default
    private Integer robotingCount = 0;

    /**
     * 客服接待人数（至少回复过一次）
     */
    @Builder.Default
    private Integer agentServedCount = 0;

    /**
     * 首次响应时长(秒)：统计口径为“该客服今日会话中，已发生首次响应的会话的首次响应时长平均值”。
     */
    @Builder.Default
    private Integer agentFirstResponseLength = 0;

    /**
     * 平均响应时长(秒)：统计口径为“该客服今日会话中，已产生有效响应统计的会话的平均响应时长平均值”。
     */
    @Builder.Default
    private Integer agentAvgResponseLength = 0;

    /**
     * 30秒应答率（%）：有访客首条消息的会话中，客服在30秒内首次响应的占比。
     */
    @Builder.Default
    private Double answerRate30s = 0D;

    /**
     * 3分钟人工回复率（%）：有访客首条消息的会话中，客服在3分钟内首次响应的占比。
     */
    @Builder.Default
    private Double agentReplyRate3m = 0D;

    /**
     * 询单转化率（%）：有访客消息会话中，已解决会话的占比（当前口径）。
     */
    @Builder.Default
    private Double inquiryConversionRate = 0D;

    /**
     * 不满意会话数：满意度评分≤3的会话数。
     */
    @Builder.Default
    private Integer dissatisfiedCount = 0;

    /**
     * 不满意会话率（%）：不满意会话数 / 已评分会话数。
     */
    @Builder.Default
    private Double dissatisfiedRate = 0D;

    /**
     * 每小时接待人数（24小时数组）
     */
    @Builder.Default
    private List<Integer> threadsCountByHour = new ArrayList<>();
}
