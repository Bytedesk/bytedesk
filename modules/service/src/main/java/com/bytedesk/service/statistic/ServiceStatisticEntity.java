/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-07-10 09:17:39
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2025-02-21 14:31:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.service.statistic;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * 客服对话统计数据：
 * 组织、工作组、客服、机器人
 */
@Entity
@Data
@Builder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bytedesk_service_statistic", uniqueConstraints = {
    @UniqueConstraint(
        columnNames = {"orgUid", "workgroupUid", "agentUid", "robotUid", "date", "hour"},
        name = "uk_org_uid_workgroup_uid_agent_uid_robot_uid_date_hour"
    )
})
public class ServiceStatisticEntity extends BaseEntity {

    //////////////////////////////// 基础会话指标 /////////////////////////////////
    
    // 当前在线客服数量
    @Builder.Default
    private int onlineAgentCount = 0;
    
    // 当前离线客服数量
    @Builder.Default
    private int offlineAgentCount = 0;

    // 当前排队人数
    @Builder.Default
    private int queuingThreadCount = 0;
    
    // 当前最长等待时间(秒)
    @Builder.Default
    private int maxWaitingTime = 0;

    // 当前会话数量
    @Builder.Default
    private int currentThreadCount = 0;

    //////////////////////////////// 会话流转指标 /////////////////////////////////

    // 总流入会话量
    @Builder.Default
    private int totalIncomingThreads = 0;
    
    // 已接入会话量
    @Builder.Default
    private int acceptedThreadCount = 0;
    
    // 放弃排队会话量
    @Builder.Default
    private int abandonedThreadCount = 0;
    
    // 转接会话量
    @Builder.Default
    private int transferredThreadCount = 0;
    
    // 邀请会话量(主动邀请)
    @Builder.Default
    private int invitedThreadCount = 0;
    
    // 超时转接会话量
    @Builder.Default
    private int timeoutTransferCount = 0;

    //////////////////////////////// 时间指标 /////////////////////////////////
    
    // 平均等待时间(秒)
    @Builder.Default
    private int avgWaitingTime = 0;
    
    // 平均首次响应时间(秒)
    @Builder.Default
    private int avgFirstResponseTime = 0;
    
    // 平均会话时长(秒)
    @Builder.Default
    private int avgConversationTime = 0;
    
    // 最长响应时间(秒)
    @Builder.Default
    private int maxResponseTime = 0;
    
    // 最短响应时间(秒)
    @Builder.Default
    private int minResponseTime = 0;

    //////////////////////////////// 质量指标 /////////////////////////////////
    
    // 接通率(%) = 已接入会话量/总流入会话量
    @Builder.Default
    private double acceptRate = 0.0;
    
    // 放弃率(%) = 放弃排队会话量/总流入会话量
    @Builder.Default
    private double abandonRate = 0.0;
    
    // 转接率(%) = 转接会话量/已接入会话量
    @Builder.Default
    private double transferRate = 0.0;
    
    // 满意度评价总数
    @Builder.Default
    private int totalRatingCount = 0;
    
    // 满意评价数
    @Builder.Default
    private int satisfiedRatingCount = 0;
    
    // 满意率(%) = 满意评价数/评价总数
    @Builder.Default
    private double satisfactionRate = 0.0;
    
    // 参评率(%) = 评价总数/已接入会话量
    @Builder.Default
    private double ratingRate = 0.0;

    //////////////////////////////// 消息指标 /////////////////////////////////
    
    // 客服发送消息数
    @Builder.Default
    private int agentMessageCount = 0;
    
    // 访客发送消息数 
    @Builder.Default
    private int visitorMessageCount = 0;
    
    // 平均会话消息数
    @Builder.Default
    private int avgMessagePerThread = 0;

    //////////////////////////////// 机器人指标 /////////////////////////////////
    
    // 机器人会话量
    @Builder.Default
    private int robotThreadCount = 0;
    
    // 机器人转人工量
    @Builder.Default
    private int robotToHumanCount = 0;
    
    // 机器人问题解决率(%)
    @Builder.Default
    private double robotSolveRate = 0.0;

    //////////////////////////////// 工作量指标 /////////////////////////////////
    
    // 在线时长(秒)
    @Builder.Default
    private int onlineTime = 0;
    
    // 忙碌时长(秒)
    @Builder.Default
    private int busyTime = 0;
    
    // 离线时长(秒)
    @Builder.Default
    private int offlineTime = 0;

    //////////////////////////////// 统计维度 /////////////////////////////////

    // 统计类型: ORG/WORKGROUP/AGENT/ROBOT
    @Builder.Default
    private String type = ServiceStatisticTypeEnum.ORG.name();

    private String workgroupUid;
    private String agentUid; 
    private String robotUid;

    // 统计时间维度
    @Builder.Default
    private int hour = 0;
    private String date;

    //////////////////////////////// 辅助方法 /////////////////////////////////

    public void incrementThreadCount() {
        this.currentThreadCount++;
    }

    public void decrementThreadCount() {
        this.currentThreadCount--;
    }

    // 计算各类率值
    public void calculateRates() {
        // 接通率
        this.acceptRate = totalIncomingThreads > 0 ? 
            (double) acceptedThreadCount / totalIncomingThreads * 100 : 0;
            
        // 放弃率
        this.abandonRate = totalIncomingThreads > 0 ?
            (double) abandonedThreadCount / totalIncomingThreads * 100 : 0;
            
        // 转接率
        this.transferRate = acceptedThreadCount > 0 ?
            (double) transferredThreadCount / acceptedThreadCount * 100 : 0;
            
        // 满意率
        this.satisfactionRate = totalRatingCount > 0 ?
            (double) satisfiedRatingCount / totalRatingCount * 100 : 0;
            
        // 参评率
        this.ratingRate = acceptedThreadCount > 0 ?
            (double) totalRatingCount / acceptedThreadCount * 100 : 0;
    }
}
