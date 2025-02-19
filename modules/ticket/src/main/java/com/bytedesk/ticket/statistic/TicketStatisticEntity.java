/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-01-20 17:01:24
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-02-20 17:07:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license. 
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 * 
 * Copyright (c) 2025 by bytedesk.com, All Rights Reserved. 
 */
package com.bytedesk.ticket.statistic;

import com.bytedesk.core.base.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({TicketStatisticEntityListener.class})
@Table(name = "bytedesk_ticket_statistics", uniqueConstraints = {
    @UniqueConstraint(
        columnNames = {"org_uid", "workgroup_uid", "assignee_uid", "date", "hour"},
        name = "uk_org_uid_workgroup_uid_assignee_uid_date_hour"
    )
})
public class TicketStatisticEntity extends BaseEntity {

    @Builder.Default
    @Column(name = "statistic_type")
    private String type = TicketStatisticTypeEnum.WORKGROUP.name(); // 统计类型，workgroup/agent/org

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
    private LocalDateTime statisticStartTime;    // 统计开始时间
    private LocalDateTime statisticEndTime;      // 统计结束时间

    // 最细统计粒度，用于展示当天工单趋势变化
    @Builder.Default
    private int hour = 0;

    // 日期，每个orgUid，每个日期一个统计
    private String date;

    // 未读消息统计
    // @Builder.Default
    // private Map<String, Integer> unreadCountMap = new HashMap<>();  // 每个用户的未读数 <UserUid, UnreadCount>

    /**
     * 增加用户未读数
     */
    // public void incrementUnreadCount(String userUid) {
    //     unreadCountMap.compute(userUid, (k, v) -> v == null ? 1 : v + 1);
    //     this.unreadTickets++;
    // }

    /**
     * 清除用户未读数
     */
    // public void clearUnreadCount(String userUid) {
    //     Integer count = unreadCountMap.remove(userUid);
    //     if (count != null) {
    //         this.unreadTickets -= count;
    //     }
    // }

    /**
     * 获取用户未读数
     */
    // public int getUnreadCount(String userUid) {
    //     return unreadCountMap.getOrDefault(userUid, 0);
    // }
}
