/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-11-25
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
public class AgentQueueStatsResponse {
    
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
     * 每小时接待人数（24小时数组）
     */
    @Builder.Default
    private List<Integer> threadsCountByHour = new ArrayList<>();
}
