package com.bytedesk.ticket.performance.dto;

import lombok.Data;
import java.util.Map;

@Data
public class AgentPerformanceDTO {
    // 基本信息
    private Long agentId;
    private String agentName;
    private String agentStatus;  // online/offline/busy
    
    // 工单统计
    private Integer totalTickets;        // 总工单数
    private Integer resolvedTickets;     // 已解决工单数
    private Integer pendingTickets;      // 待处理工单数
    private Integer overdueTickets;      // 逾期工单数
    private Double resolutionRate;       // 解决率
    private Double overdueRate;          // 逾期率
    
    // 时间统计
    private Long avgResponseTime;        // 平均首次响应时间(分钟)
    private Long avgResolutionTime;      // 平均解决时间(分钟)
    private Long avgHandlingTime;        // 平均处理时间(分钟)
    private Double slaComplianceRate;    // SLA达标率
    
    // 满意度统计
    private Double avgRating;            // 平均评分
    private Integer totalRatings;        // 评价总数
    private Map<Integer, Long> ratingDistribution;  // 评分分��
    private Double avgResponseTimeSatisfaction;     // 响应时间满意度
    private Double avgSolutionSatisfaction;         // 解决方案满意度
    private Double avgServiceAttitudeSatisfaction;  // 服务态度满意度
    
    // 工作量统计
    private Integer onlineHours;         // 在线时长(小时)
    private Integer handledTickets;      // 处理工单数
    private Double avgHandledPerHour;    // 每小时处理工单数
    private Double workloadBalance;      // 工作量均衡度
} 