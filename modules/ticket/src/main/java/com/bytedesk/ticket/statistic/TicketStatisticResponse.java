/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-16 14:58:40
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-20 17:14:10
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.statistic;

import com.bytedesk.core.base.BaseResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TicketStatisticResponse extends BaseResponse {
    
    // 基本统计
    private long totalTickets;           // 总工单数
    private long openTickets;            // 开放工单数
    private long closedTickets;          // 已关闭工单数
    private long unreadTickets;          // 未读工单数

    // 状态统计
    private long newTickets;             // 新建工单数
    private long claimedTickets;         // 已认领工单数
    private long unclaimedTickets;       // 已退回工单数
    private long processingTickets;      // 处理中工单数
    private long pendingTickets;         // 待处理工单数
    private long holdingTickets;         // 挂起工单数
    private long reopenedTickets;        // 重新打开工单数
    private long resolvedTickets;        // 已解决工单数
    private long escalatedTickets;       // 已升级工单数

    // 优先级统计
    private long criticalTickets;        // 紧急工单数
    private long highTickets;            // 高优先级工单数
    private long mediumTickets;          // 中优先级工单数
    private long lowTickets;             // 低优先级工单数

    // 时间统计
    private double averageResolutionTime;    // 平均解决时间(小时)
    private double averageFirstResponseTime; // 平均首次响应时间(分钟)
    private long slaBreachCount;            // SLA违反次数
    private double slaComplianceRate;        // SLA达标率(%)

    // 工作组统计
    private String workgroupUid;            // 工作组ID
    private long workgroupTickets;          // 工作组工单数
    private double workgroupResolutionRate;  // 工作组解决率(%)

    // 处理人统计
    private String assigneeUid;             // 处理人ID
    private long assigneeTickets;           // 处理人工单数
    private double assigneeResolutionRate;   // 处理人解决率(%)

    // 客户满意度
    private double customerSatisfactionRate; // 客户满意度(%)
    private long satisfiedTickets;          // 满意工单数
    private long unsatisfiedTickets;        // 不满意工单数

    // 时间范围
    private String statisticStartTime;    // 统计开始时间
    private String statisticEndTime;      // 统计结束时间

    // 最细统计粒度，用于展示当天工单趋势变化
    private int hour;

     // 日期，每个orgUid，每个日期一个统计
     private String date;
} 