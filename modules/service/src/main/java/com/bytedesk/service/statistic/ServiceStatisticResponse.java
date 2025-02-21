/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-17 17:09:17
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-21 17:29:05
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic;

import com.bytedesk.core.base.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ServiceStatisticResponse extends BaseResponse {

    //////////////////////////////// 基础会话指标 /////////////////////////////////

    // 当前在线客服数量
    private Integer onlineAgentCount;

    // 当前离线客服数量
    private Integer offlineAgentCount;

    // 当前排队人数
    private Integer queuingThreadCount;

    // 当前最长等待时间(秒)
    private Integer maxWaitingTime;

    // 当前会话数量
    private Integer currentThreadCount;

    //////////////////////////////// 会话流转指标 /////////////////////////////////

    // 总流入会话量

    private Integer totalIncomingThreads;

    // 已接入会话量

    private Integer acceptedThreadCount;

    // 放弃排队会话量

    private Integer abandonedThreadCount;

    // 转接会话量

    private Integer transferredThreadCount;

    // 邀请会话量(主动邀请)

    private Integer invitedThreadCount;

    // 超时转接会话量

    private Integer timeoutTransferCount;

    //////////////////////////////// 时间指标 /////////////////////////////////

    // 平均等待时间(秒)

    private Integer avgWaitingTime;

    // 平均首次响应时间(秒)

    private Integer avgFirstResponseTime;

    // 平均会话时长(秒)

    private Integer avgConversationTime;

    // 最长响应时间(秒)

    private Integer maxResponseTime;

    // 最短响应时间(秒)

    private Integer minResponseTime;

    //////////////////////////////// 质量指标 /////////////////////////////////

    // 接通率(%) = 已接入会话量/总流入会话量

    private double acceptRate.0;

    // 放弃率(%) = 放弃排队会话量/总流入会话量

    private double abandonRate.0;

    // 转接率(%) = 转接会话量/已接入会话量

    private double transferRate.0;

    // 满意度评价总数

    private Integer totalRatingCount;

    // 满意评价数

    private Integer satisfiedRatingCount;

    // 满意率(%) = 满意评价数/评价总数

    private double satisfactionRate.0;

    // 参评率(%) = 评价总数/已接入会话量

    private double ratingRate.0;

    //////////////////////////////// 消息指标 /////////////////////////////////

    // 客服发送消息数

    private Integer agentMessageCount;

    // 访客发送消息数

    private Integer visitorMessageCount;

    // 平均会话消息数

    private Integer avgMessagePerThread;

    //////////////////////////////// 机器人指标 /////////////////////////////////

    // 机器人会话量

    private Integer robotThreadCount;

    // 机器人转人工量

    private Integer robotToHumanCount;

    // 机器人问题解决率(%)

    private double robotSolveRate.0;

    //////////////////////////////// 工作量指标 /////////////////////////////////

    // 在线时长(秒)

    private Integer onlineTime;

    // 忙碌时长(秒)

    private Integer busyTime;

    // 离线时长(秒)

    private Integer offlineTime;

    //////////////////////////////// 扩展指标 /////////////////////////////////

    // 客服工作状态分布

    private Integer availableAgentCount; // 空闲客服数

    private Integer busyAgentCount; // 忙碌客服数

    private Integer awayAgentCount; // 离开客服数

    // 会话分配

    private Integer autoAssignedCount; // 自动分配会话数

    private Integer manualAssignedCount; // 手动分配会话数

    // 会话质量

    private Integer firstSolveCount; // 首次解决会话数

    private double firstSolveRate.0; // 首次解决率

    // 响应时间分布

    private Integer responseWithin1Min; // 1分钟内响应数

    private Integer responseWithin5Min; // 5分钟内响应数

    private Integer responseOver5Min; // 超过5分钟响应数

    // 会话时长分布

    private Integer durationWithin5Min; // 5分钟内结束会话数

    private Integer durationWithin15Min; // 15分钟内结束会话数

    private Integer durationOver15Min; // 超过15分钟会话数

    //////////////////////////////// 统计维度 /////////////////////////////////

    // 统计类型: ORG/WORKGROUP/AGENT/ROBOT

    private String type = ServiceStatisticTypeEnum.ORG.name();

    private String workgroupUid;
    private String agentUid;
    private String robotUid;

    // 统计时间维度
    private Integer hour;
    private String date;

}
