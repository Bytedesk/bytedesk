package com.bytedesk.ticket.performance;

import com.bytedesk.ticket.performance.dto.AgentPerformanceDTO;
import java.time.LocalDateTime;
import java.util.List;

public interface AgentPerformanceService {
    
    /**
     * 获取客服绩效统计
     * @param agentId 客服ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 绩效统计数据
     */
    AgentPerformanceDTO getAgentPerformance(Long agentId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取团队绩效统计
     * @param teamId 团队ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 团队所有客服的绩效统计数据
     */
    List<AgentPerformanceDTO> getTeamPerformance(Long teamId, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 获取绩效排名
     * @param metric 排名指标(resolution_rate/avg_response_time/avg_rating等)
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 返回数量
     * @return 排名数据
     */
    List<AgentPerformanceDTO> getPerformanceRanking(String metric, LocalDateTime startTime, 
            LocalDateTime endTime, int limit);
    
    /**
     * 导出绩效报表
     * @param agentIds 客服ID列表
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param format 导出格式(excel/pdf)
     * @return 报表文件字节数组
     */
    byte[] exportPerformanceReport(List<Long> agentIds, LocalDateTime startTime, 
            LocalDateTime endTime, String format);
} 