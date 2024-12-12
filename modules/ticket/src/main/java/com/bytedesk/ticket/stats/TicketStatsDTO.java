package com.bytedesk.ticket.stats;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TicketStatsDTO {
    // 工单总量
    private Long totalTickets;
    private Long openTickets;
    private Long resolvedTickets;
    private Long closedTickets;
    
    // 响应时间
    private Double avgFirstResponseTime;  // 平均首次响应时间(分钟)
    private Double avgResolutionTime;     // 平均解决时间(分钟)
    
    // 满意度
    private Double avgSatisfactionRating; // 平均满意度评分
    private Long satisfactionCount;       // 评分数量
    
    // 工单分布
    private Long urgentTickets;
    private Long highTickets;
    private Long normalTickets;
    private Long lowTickets;
    
    // 超时情况
    private Long overdueTickets;
    private Double overdueRate;           // 超时率
    
    // 时间范围
    private LocalDateTime startTime;
    private LocalDateTime endTime;
} 