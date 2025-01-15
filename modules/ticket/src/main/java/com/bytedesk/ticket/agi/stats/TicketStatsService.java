package com.bytedesk.ticket.stats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface TicketStatsService {
    
    // 获取总体统计
    TicketStatsDTO getOverallStats(LocalDateTime startTime, LocalDateTime endTime);
    
    // 获取客服统计
    TicketStatsDTO getAgentStats(Long agentId, LocalDateTime startTime, LocalDateTime endTime);
    
    // 获取分类统计
    TicketStatsDTO getCategoryStats(Long categoryId, LocalDateTime startTime, LocalDateTime endTime);
    
    // 获取每日工单量趋势
    List<Map<String, Object>> getDailyTicketTrend(LocalDateTime startTime, LocalDateTime endTime);
    
    // 获取工单分类分布
    List<Map<String, Object>> getCategoryDistribution(LocalDateTime startTime, LocalDateTime endTime);
    
    // 获取客服工作量排名
    List<Map<String, Object>> getAgentWorkloadRanking(LocalDateTime startTime, LocalDateTime endTime);
    
    // 获取平均响应时间趋势
    List<Map<String, Object>> getResponseTimeTrend(LocalDateTime startTime, LocalDateTime endTime);
    
    // 获取满意度分布
    Map<Integer, Long> getSatisfactionDistribution(LocalDateTime startTime, LocalDateTime endTime);
} 