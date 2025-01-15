package com.bytedesk.ticket.agi.performance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bytedesk.ticket.agi.performance.dto.AgentPerformanceDTO;

import lombok.extern.slf4j.Slf4j;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class AgentPerformanceServiceImpl implements AgentPerformanceService {

    @Autowired
    private AgentPerformanceQuery performanceQuery;
    
    // @Autowired
    // private UserService userService;
    
    // @Autowired
    // private TicketService ticketService;
    
    // @Autowired
    // private TicketSatisfactionService satisfactionService;
    
    // @Autowired
    // private ExcelExporter excelExporter;
    
    // @Autowired
    // private PdfExporter pdfExporter;

    @Override
    public AgentPerformanceDTO getAgentPerformance(Long agentId, LocalDateTime startTime, LocalDateTime endTime) {
        AgentPerformanceDTO performance = new AgentPerformanceDTO();
        performance.setAgentId(agentId);
        // performance.setAgentName(userService.getAgentName(agentId));
        // performance.setAgentStatus(userService.getAgentStatus(agentId));
        
        // 获取工单统计
        Map<String, Object> ticketStats = performanceQuery.getTicketStats(agentId, startTime, endTime);
        performance.setTotalTickets((Integer) ticketStats.get("total_tickets"));
        performance.setResolvedTickets((Integer) ticketStats.get("resolved_tickets"));
        performance.setPendingTickets((Integer) ticketStats.get("pending_tickets"));
        performance.setOverdueTickets((Integer) ticketStats.get("overdue_tickets"));
        
        // 计算比率
        if (performance.getTotalTickets() > 0) {
            performance.setResolutionRate((double) performance.getResolvedTickets() / performance.getTotalTickets());
            performance.setOverdueRate((double) performance.getOverdueTickets() / performance.getTotalTickets());
        }
        
        // 获取时间统计
        Map<String, Object> timeStats = performanceQuery.getTimeStats(agentId, startTime, endTime);
        performance.setAvgResponseTime((Long) timeStats.get("avg_response_time"));
        performance.setAvgResolutionTime((Long) timeStats.get("avg_resolution_time"));
        performance.setAvgHandlingTime((Long) timeStats.get("avg_handling_time"));
        performance.setSlaComplianceRate((Double) timeStats.get("sla_compliance_rate"));
        
        // 获取满意度统计
        Map<String, Object> satisfactionStats = performanceQuery.getSatisfactionStats(agentId, startTime, endTime);
        performance.setAvgRating((Double) satisfactionStats.get("avg_rating"));
        performance.setTotalRatings((Integer) satisfactionStats.get("total_ratings"));
        performance.setRatingDistribution(getRatingDistribution(agentId, startTime, endTime));
        performance.setAvgResponseTimeSatisfaction((Double) satisfactionStats.get("avg_response_time_satisfaction"));
        performance.setAvgSolutionSatisfaction((Double) satisfactionStats.get("avg_solution_satisfaction"));
        performance.setAvgServiceAttitudeSatisfaction((Double) satisfactionStats.get("avg_service_attitude_satisfaction"));
        
        // 获取工作量统计
        Map<String, Object> workloadStats = performanceQuery.getWorkloadStats(agentId, startTime, endTime);
        performance.setOnlineHours((Integer) workloadStats.get("online_hours"));
        performance.setHandledTickets((Integer) workloadStats.get("handled_tickets"));
        performance.setAvgHandledPerHour((Double) workloadStats.get("avg_handled_per_hour"));
        performance.setWorkloadBalance((Double) workloadStats.get("workload_balance"));
        
        return performance;
    }

    @Override
    public List<AgentPerformanceDTO> getTeamPerformance(Long teamId, LocalDateTime startTime, LocalDateTime endTime) {
        // List<Long> teamAgents = userService.getTeamAgents(teamId);
        // return teamAgents.stream()
        //     .map(agentId -> getAgentPerformance(agentId, startTime, endTime))
        //     .collect(Collectors.toList());
        return null;
    }

    @Override
    public List<AgentPerformanceDTO> getPerformanceRanking(String metric, LocalDateTime startTime, 
            LocalDateTime endTime, int limit) {
        // List<Map<String, Object>> rankings = performanceQuery.getPerformanceRanking(metric, startTime, endTime, limit);
        
        // return rankings.stream()
        //     .map(row -> {
        //         Long agentId = (Long) row.get("agent_id");
        //         return getAgentPerformance(agentId, startTime, endTime);
        //     })
        //     .collect(Collectors.toList());
        return null;
    }

    @Override
    public byte[] exportPerformanceReport(List<Long> agentIds, LocalDateTime startTime, 
            LocalDateTime endTime, String format) {
        // List<AgentPerformanceDTO> performances = agentIds.stream()
        //     .map(agentId -> getAgentPerformance(agentId, startTime, endTime))
        //     .collect(Collectors.toList());
            
        // try {
        //     if ("excel".equals(format)) {
        //         // return excelExporter.exportPerformanceReport(performances);
        //     } else if ("pdf".equals(format)) {
        //         // return pdfExporter.exportPerformanceReport(performances);
        //     } else {
        //         throw new IllegalArgumentException("Unsupported format: " + format);
        //     }
        // } catch (Exception e) {
        //     log.error("Failed to export performance report", e);
        //     throw new RuntimeException("Failed to export performance report", e);
        // }
        return null;
    }

    private Map<Integer, Long> getRatingDistribution(Long agentId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Map<String, Object>> distribution = performanceQuery.getRatingDistribution(agentId, startTime, endTime);
        
        Map<Integer, Long> result = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            result.put(i, 0L);
        }
        
        distribution.forEach(row -> 
            result.put((Integer) row.get("rating"), (Long) row.get("count")));
            
        return result;
    }
} 