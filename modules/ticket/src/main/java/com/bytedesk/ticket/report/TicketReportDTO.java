package com.bytedesk.ticket.report;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class TicketReportDTO {
    // 报表基本信息
    private ReportType type;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String title;
    
    // 工单统计
    private Long totalTickets;
    private Long newTickets;
    private Long resolvedTickets;
    private Long closedTickets;
    
    // 响应时间
    private Double avgFirstResponseTime;
    private Double avgResolutionTime;
    private Double slaComplianceRate;
    
    // 客服绩效
    private List<Map<String, Object>> agentPerformance;
    
    // 分类统计
    private List<Map<String, Object>> categoryStats;
    
    // 满意度
    private Double avgSatisfactionRating;
    private Map<Integer, Long> satisfactionDistribution;
    
    // 趋势数据
    private List<Map<String, Object>> ticketTrend;
    private List<Map<String, Object>> responseTrend;
} 