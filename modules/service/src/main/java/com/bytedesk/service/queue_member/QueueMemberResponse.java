/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-14 17:57:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-04-09 12:39:06
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

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.thread.ThreadEmotionTypeEnum;
import com.bytedesk.core.thread.ThreadIntentionTypeEnum;
import com.bytedesk.core.thread.ThreadInviteStatusEnum;
import com.bytedesk.core.thread.ThreadQualityCheckResultEnum;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.thread.ThreadSummaryStatusEnum;
import com.bytedesk.core.thread.ThreadTransferStatusEnum;
import com.bytedesk.service.queue.QueueResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class QueueMemberResponse extends BaseResponse {

    // 作为工作组队列成员关系
    private QueueResponse queue;

    private ThreadResponse thread;  // 会话信息

    @Builder.Default
    private Integer queueNumber = 0;  // 排队号码

    @Builder.Default
    private Integer waitTime = 0;  // 排队时间（秒）

    /**
     * 访客消息统计：
     * 记录第一条访客消息的时间
     * 更新最后一条访客消息的时间
     * 统计访客消息总数
     */
    @Builder.Default
    private LocalDateTime visitorEnqueueTime = LocalDateTime.now();  // 加入时间

    private LocalDateTime visitorFirstMessageTime;  // 访客首次发送消息时间

    private LocalDateTime visitorLastMessageTime;  // 访客最后发送消息时间

    @Builder.Default
    private Integer visitorMessageCount = 0;  // 访客消息数量

    private LocalDateTime visitorLeaveTime;  // 离开时间

    @Builder.Default
    private Integer visitorPriority = 0;  // 优先级(0-100)

    /**
     * 客服消息统计：
     * 标记客服是否首次响应
     * 记录首次响应时间
     * 更新最后响应时间
     * 计算平均响应时间（累计平均算法）
     * 追踪最长响应时间
     * 统计客服消息总数
     */
    private String agentAcceptType ;  // 接入方式：自动、手动，不设置默认

    private LocalDateTime agentAcceptTime;  // 开始服务时间

    @Builder.Default
    private Boolean agentFirstResponse = false;  // 人工客服是否首次响应

    private LocalDateTime agentFirstResponseTime;  // 首次响应时间

    private LocalDateTime agentLastResponseTime;  // 最后响应时间

    private LocalDateTime agentCloseTime;  // 结束时间

    /**
     * 响应时间计算：
     * 基于访客最后消息时间和客服响应时间计算响应时长
     * 动态更新平均响应时间和最大响应时间
     */
    @Builder.Default
    private Integer agentAvgResponseTime = 0;  // 平均响应时间(秒)
    
    @Builder.Default
    private Integer agentMaxResponseTime = 0;  // 最长响应时间(秒)

    @Builder.Default
    private Integer agentMessageCount = 0;  // 客服消息数量

    private LocalDateTime agentTimeoutAt; // 人工对话超时时间

    @Builder.Default
    private Boolean agentTimeout = false; // 是否超时

    // 人工是否离线
    @Builder.Default
    private Boolean agentOffline = false;

    /**
     * robot 
     * 响应时间计算：
     */
    private String robotAcceptType ;  // 接入方式：自动、手动，不设置默认

    private LocalDateTime robotAcceptTime;  // 开始服务时间
    
    @Builder.Default
    private Boolean robotFirstResponse = false;  // 人工客服是否首次响应

    private LocalDateTime robotFirstResponseTime;  // 首次响应时间

    private LocalDateTime robotLastResponseTime;  // 最后响应时间

    private LocalDateTime robotCloseTime;  // 结束时间

    @Builder.Default
    private Integer robotAvgResponseTime = 0;  // 平均响应时间(秒)
    
    @Builder.Default
    private Integer robotMaxResponseTime = 0;  // 最长响应时间(秒)

    @Builder.Default
    private Integer robotMessageCount = 0;  // 客服消息数量

    // 机器人对话超时时间
    private LocalDateTime robotTimeoutAt;

    @Builder.Default
    private Boolean robotTimeout = false; // 是否超时

    // -----------------

    @Builder.Default
    private Integer systemMessageCount = 0;  // 系统消息数量

    // 直接在评价表里面根据threadUid查询是否已经评价
    // 是否被评价
    @Builder.Default
    private Boolean rated = false;

    @Builder.Default
    private Integer rateLevel = 0;  // 评分等级

    // 是否留言
    @Builder.Default
    private Boolean leaveMsg = false;

    private LocalDateTime leaveMsgAt;  // 留言时间

    // 直接在小结表里面根据threadUid查询是否已经小结
    // 是否已经小结
    @Builder.Default
    private Boolean summarized = false;

    // 是否已解决
    @Builder.Default
    private Boolean resolved = false;

    // resolved status
    @Builder.Default
    private String resolvedStatus = ThreadSummaryStatusEnum.PENDING.name();

    // 直接在质检表里面根据threadUid查询是否已经质检
    // 是否已经质检
    @Builder.Default
    private Boolean qualityChecked = false;

    // 质检结果
    @Builder.Default
    private String qualityCheckResult = ThreadQualityCheckResultEnum.OTHER.name();

    // 意图类型
    @Builder.Default
    private String intentionType = ThreadIntentionTypeEnum.OTHER.name();

    // 情绪类型
    @Builder.Default
    private String emotionType = ThreadEmotionTypeEnum.OTHER.name();

    // 机器人转人工
    @Builder.Default
    private Boolean robotToAgent = false;

    // 人工转人工
    // transfer status
    @Builder.Default
    private String transferStatus = ThreadTransferStatusEnum.NONE.name();

    // TODO: 可能同时邀请多个人
    // invite status
    @Builder.Default
    private String inviteStatus = ThreadInviteStatusEnum.NONE.name();

    
}
