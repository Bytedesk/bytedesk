/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-10-14 17:57:16
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-07-28 11:40:25
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.queue_member;

import java.time.ZonedDateTime;

import com.bytedesk.core.base.BaseResponse;
import com.bytedesk.core.thread.ThreadEmotionTypeEnum;
import com.bytedesk.core.thread.ThreadIntentionTypeEnum;
import com.bytedesk.core.thread.ThreadInviteStatusEnum;
import com.bytedesk.core.thread.ThreadQualityCheckResultEnum;
import com.bytedesk.core.thread.ThreadResponse;
import com.bytedesk.core.thread.ThreadSummaryStatusEnum;
import com.bytedesk.core.thread.ThreadTransferStatusEnum;
import com.bytedesk.core.utils.BdDateUtils;
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
    private Integer waitLength = 0;  // 排队时间（秒）

    /**
     * 访客消息统计：
     * 记录第一条访客消息的时间
     * 更新最后一条访客消息的时间
     * 统计访客消息总数
     */
    @Builder.Default
    private ZonedDateTime visitorEnqueueAt = BdDateUtils.now();  // 加入时间

    private ZonedDateTime visitorFirstMessageAt;  // 访客首次发送消息时间

    private ZonedDateTime visitorLastMessageAt;  // 访客最后发送消息时间

    @Builder.Default
    private Integer visitorMessageCount = 0;  // 访客消息数量

    private ZonedDateTime visitorLeavedAt;  // 离开时间

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
    private String agentAcceptType;  // 接入方式：自动、手动，不设置默认

    private ZonedDateTime agentAcceptedAt;  // 开始服务时间

    @Builder.Default
    private Boolean agentFirstResponse = false;  // 人工客服是否首次响应

    private ZonedDateTime agentFirstResponseAt;  // 首次响应时间

    private ZonedDateTime agentLastResponseAt;  // 最后响应时间

    private ZonedDateTime agentClosedAt;  // 结束时间
    
    @Builder.Default
    private Boolean agentClose = false;  // 是否客服手动结束

    /**
     * 响应时间计算：
     * 基于访客最后消息时间和客服响应时间计算响应时长
     * 动态更新平均响应时间和最大响应时间
     */
    @Builder.Default
    private Integer agentAvgResponseLength = 0;  // 平均响应时间(秒)
    
    @Builder.Default
    private Integer agentMaxResponseLength = 0;  // 最长响应时间(秒)

    @Builder.Default
    private Integer agentMessageCount = 0;  // 客服消息数量

    private ZonedDateTime agentTimeoutAt; // 人工对话超时时间

    @Builder.Default
    private Boolean agentTimeout = false; // 是否超时
    
    // agent timeout count
    @Builder.Default
    private Integer agentTimeoutCount = 0;  // 超时次数

    // 人工是否离线
    @Builder.Default
    private Boolean agentOffline = false;

    /**
     * robot 
     * 响应时间计算：
     */
    private String robotAcceptType;  // 接入方式：自动、手动，不设置默认

    private ZonedDateTime robotAcceptedAt;  // 开始服务时间
    
    @Builder.Default
    private Boolean robotFirstResponse = false;  // 机器人客服是否首次响应

    private ZonedDateTime robotFirstResponseAt;  // 首次响应时间

    private ZonedDateTime robotLastResponseAt;  // 最后响应时间

    private ZonedDateTime robotClosedAt;  // 结束时间

    @Builder.Default
    private Integer robotAvgResponseLength = 0;  // 平均响应时间(秒)
    
    @Builder.Default
    private Integer robotMaxResponseLength = 0;  // 最长响应时间(秒)

    @Builder.Default
    private Integer robotMessageCount = 0;  // 客服消息数量

    // 机器人对话超时时间
    private ZonedDateTime robotTimeoutAt;

    @Builder.Default
    private Boolean robotTimeout = false; // 是否超时

    // -----------------
    private ZonedDateTime systemFirstResponseAt;  // 系统首次响应时间

    private ZonedDateTime systemLastResponseAt;  // 系统最后响应时间

    private ZonedDateTime systemCloseAt;  // 系统结束时间

    @Builder.Default
    private Boolean systemClose = false;  // 是否系统自动结束

    @Builder.Default
    private Integer systemMessageCount = 0;  // 系统消息数量

    // 直接在评价表里面根据threadUid查询是否已经评价
    // 是否被评价
    @Builder.Default
    private Boolean rated = false;

    private Integer rateScore;  // 评分

    // 评分时间
    private ZonedDateTime rateAt;  // 评分时间

    // 是否已解决
    @Builder.Default
    private Boolean resolved = false;

    // 是否留言
    @Builder.Default
    private Boolean messageLeave = false;

    private ZonedDateTime messageLeaveAt;  // 留言时间

    // 直接在小结表里面根据threadUid查询是否已经小结
    // 是否已经小结
    @Builder.Default
    private Boolean summarized = false;
    
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

    // 机器人转人工时间
    private ZonedDateTime robotToAgentAt;

    // 人工转人工
    // transfer status
    @Builder.Default
    private String transferStatus = ThreadTransferStatusEnum.NONE.name();

    // TODO: 可能同时邀请多个人
    // invite status
    @Builder.Default
    private String inviteStatus = ThreadInviteStatusEnum.NONE.name();

    // ZonedDateTime 字段的格式化 getter 方法
    public String getVisitorEnqueueAt() {
        return BdDateUtils.formatDatetimeToString(visitorEnqueueAt);
    }

    public String getVisitorFirstMessageAt() {
        return BdDateUtils.formatDatetimeToString(visitorFirstMessageAt);
    }

    public String getVisitorLastMessageAt() {
        return BdDateUtils.formatDatetimeToString(visitorLastMessageAt);
    }

    public String getVisitorLeavedAt() {
        return BdDateUtils.formatDatetimeToString(visitorLeavedAt);
    }

    public String getAgentAcceptedAt() {
        return BdDateUtils.formatDatetimeToString(agentAcceptedAt);
    }

    public String getAgentFirstResponseAt() {
        return BdDateUtils.formatDatetimeToString(agentFirstResponseAt);
    }

    public String getAgentLastResponseAt() {
        return BdDateUtils.formatDatetimeToString(agentLastResponseAt);
    }

    public String getAgentClosedAt() {
        return BdDateUtils.formatDatetimeToString(agentClosedAt);
    }

    public String getAgentTimeoutAt() {
        return BdDateUtils.formatDatetimeToString(agentTimeoutAt);
    }

    public String getRobotAcceptedAt() {
        return BdDateUtils.formatDatetimeToString(robotAcceptedAt);
    }

    public String getRobotFirstResponseAt() {
        return BdDateUtils.formatDatetimeToString(robotFirstResponseAt);
    }

    public String getRobotLastResponseAt() {
        return BdDateUtils.formatDatetimeToString(robotLastResponseAt);
    }

    public String getRobotClosedAt() {
        return BdDateUtils.formatDatetimeToString(robotClosedAt);
    }

    public String getRobotTimeoutAt() {
        return BdDateUtils.formatDatetimeToString(robotTimeoutAt);
    }

    public String getSystemFirstResponseAt() {
        return BdDateUtils.formatDatetimeToString(systemFirstResponseAt);
    }

    public String getSystemLastResponseAt() {
        return BdDateUtils.formatDatetimeToString(systemLastResponseAt);
    }

    public String getSystemCloseAt() {
        return BdDateUtils.formatDatetimeToString(systemCloseAt);
    }

    public String getRateAt() {
        return BdDateUtils.formatDatetimeToString(rateAt);
    }

    public String getMessageLeaveAt() {
        return BdDateUtils.formatDatetimeToString(messageLeaveAt);
    }

    public String getRobotToAgentAt() {
        return BdDateUtils.formatDatetimeToString(robotToAgentAt);
    }
}
