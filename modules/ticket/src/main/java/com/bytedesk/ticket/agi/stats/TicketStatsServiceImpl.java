package com.bytedesk.ticket.agi.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketStatsServiceImpl implements TicketStatsService {

    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public TicketStatsDTO getOverallStats(LocalDateTime startTime, LocalDateTime endTime) {
        return calculateStats(ticketRepository.findByCreatedAtBetween(startTime, endTime), startTime, endTime);
    }

    @Override
    public TicketStatsDTO getAgentStats(Long agentId, LocalDateTime startTime, LocalDateTime endTime) {
        return calculateStats(ticketRepository.findByAssignedToAndCreatedAtBetween(agentId, startTime, endTime), 
            startTime, endTime);
    }

    @Override
    public TicketStatsDTO getCategoryStats(Long categoryId, LocalDateTime startTime, LocalDateTime endTime) {
        return calculateStats(ticketRepository.findByCategoryIdAndCreatedAtBetween(categoryId, startTime, endTime), 
            startTime, endTime);
    }

    private TicketStatsDTO calculateStats(List<TicketEntity> tickets, LocalDateTime startTime, LocalDateTime endTime) {
        TicketStatsDTO stats = new TicketStatsDTO();
        stats.setStartTime(startTime);
        stats.setEndTime(endTime);
        
        // 计算工单总量
        stats.setTotalTickets((long) tickets.size());
        stats.setOpenTickets(countByStatus(tickets, "open"));
        stats.setResolvedTickets(countByStatus(tickets, "resolved"));
        stats.setClosedTickets(countByStatus(tickets, "closed"));
        
        // 计算响应时间
        stats.setAvgFirstResponseTime(calculateAvgFirstResponseTime(tickets));
        stats.setAvgResolutionTime(calculateAvgResolutionTime(tickets));
        
        // 计算满意度
        Map<String, Double> satisfactionStats = calculateSatisfactionStats(tickets);
        stats.setAvgSatisfactionRating(satisfactionStats.get("average"));
        stats.setSatisfactionCount(satisfactionStats.get("count").longValue());
        
        // 计算工单分布
        stats.setUrgentTickets(countByPriority(tickets, "urgent"));
        stats.setHighTickets(countByPriority(tickets, "high"));
        stats.setNormalTickets(countByPriority(tickets, "normal"));
        stats.setLowTickets(countByPriority(tickets, "low"));
        
        // 计算超时情况
        long overdueCount = tickets.stream()
            .filter(t -> t.getDueDate() != null && t.getDueDate().isBefore(LocalDateTime.now()))
            .count();
        stats.setOverdueTickets(overdueCount);
        stats.setOverdueRate(tickets.isEmpty() ? 0.0 : overdueCount * 100.0 / tickets.size());
        
        return stats;
    }

    private long countByStatus(List<TicketEntity> tickets, String status) {
        return tickets.stream()
            .filter(t -> t.getStatus().equals(status))
            .count();
    }

    private long countByPriority(List<TicketEntity> tickets, String priority) {
        return tickets.stream()
            .filter(t -> t.getPriority().equals(priority))
            .count();
    }

    private double calculateAvgFirstResponseTime(List<TicketEntity> tickets) {
        return tickets.stream()
            .filter(t -> t.getFirstResponseTime() != null)
            .mapToLong(TicketEntity::getFirstResponseTime)
            .average()
            .orElse(0.0);
    }

    private double calculateAvgResolutionTime(List<TicketEntity> tickets) {
        return tickets.stream()
            .filter(t -> t.getResolutionTime() != null)
            .mapToLong(TicketEntity::getResolutionTime)
            .average()
            .orElse(0.0);
    }

    private Map<String, Double> calculateSatisfactionStats(List<TicketEntity> tickets) {
        List<TicketEntity> ratedTickets = tickets.stream()
            .filter(t -> t.getSatisfactionRating() != null)
            .collect(Collectors.toList());
            
        double avgRating = ratedTickets.stream()
            .mapToInt(TicketEntity::getSatisfactionRating)
            .average()
            .orElse(0.0);
            
        Map<String, Double> stats = new HashMap<>();
        stats.put("average", avgRating);
        stats.put("count", (double) ratedTickets.size());
        
        return stats;
    }

    @Override
    public List<Map<String, Object>> getDailyTicketTrend(LocalDateTime startTime, LocalDateTime endTime) {
        List<TicketEntity> tickets = ticketRepository.findByCreatedAtBetween(startTime, endTime);
        List<Map<String, Object>> trend = new ArrayList<>();
        
        // 按日期分组统计工单数
        Map<LocalDateTime, List<TicketEntity>> ticketsByDate = tickets.stream()
            .collect(Collectors.groupingBy(
                t -> t.getCreatedAt().truncatedTo(ChronoUnit.DAYS)
            ));
            
        // 生成趋势数据
        LocalDateTime current = startTime.truncatedTo(ChronoUnit.DAYS);
        while (!current.isAfter(endTime)) {
            Map<String, Object> point = new HashMap<>();
            point.put("date", current);
            point.put("count", ticketsByDate.getOrDefault(current, Collections.emptyList()).size());
            trend.add(point);
            
            current = current.plusDays(1);
        }
        
        return trend;
    }

    @Override
    public List<Map<String, Object>> getCategoryDistribution(LocalDateTime startTime, LocalDateTime endTime) {
        List<TicketEntity> tickets = ticketRepository.findByCreatedAtBetween(startTime, endTime);
        
        // 按分类分组统计
        Map<Long, Long> distribution = tickets.stream()
            .filter(t -> t.getCategoryId() != null)
            .collect(Collectors.groupingBy(
                TicketEntity::getCategoryId,
                Collectors.counting()
            ));
            
        return distribution.entrySet().stream()
            .map(entry -> {
                Map<String, Object> item = new HashMap<>();
                item.put("categoryId", entry.getKey());
                item.put("count", entry.getValue());
                return item;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getAgentWorkloadRanking(LocalDateTime startTime, LocalDateTime endTime) {
        List<TicketEntity> tickets = ticketRepository.findByCreatedAtBetween(startTime, endTime);
        
        // 按客服分组统计
        Map<Long, Long> workload = tickets.stream()
            .filter(t -> t.getAssignedTo() != null)
            .collect(Collectors.groupingBy(
                TicketEntity::getAssignedTo,
                Collectors.counting()
            ));
            
        return workload.entrySet().stream()
            .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
            .map(entry -> {
                Map<String, Object> item = new HashMap<>();
                item.put("agentId", entry.getKey());
                item.put("count", entry.getValue());
                return item;
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getResponseTimeTrend(LocalDateTime startTime, LocalDateTime endTime) {
        List<TicketEntity> tickets = ticketRepository.findByCreatedAtBetween(startTime, endTime);
        List<Map<String, Object>> trend = new ArrayList<>();
        
        // 按日期分组计算平均响应时间
        Map<LocalDateTime, List<TicketEntity>> ticketsByDate = tickets.stream()
            .collect(Collectors.groupingBy(
                t -> t.getCreatedAt().truncatedTo(ChronoUnit.DAYS)
            ));
            
        LocalDateTime current = startTime.truncatedTo(ChronoUnit.DAYS);
        while (!current.isAfter(endTime)) {
            List<TicketEntity> dailyTickets = ticketsByDate.getOrDefault(current, Collections.emptyList());
            
            Map<String, Object> point = new HashMap<>();
            point.put("date", current);
            point.put("avgResponseTime", calculateAvgFirstResponseTime(dailyTickets));
            point.put("avgResolutionTime", calculateAvgResolutionTime(dailyTickets));
            trend.add(point);
            
            current = current.plusDays(1);
        }
        
        return trend;
    }

    @Override
    public Map<Integer, Long> getSatisfactionDistribution(LocalDateTime startTime, LocalDateTime endTime) {
        List<TicketEntity> tickets = ticketRepository.findByCreatedAtBetween(startTime, endTime);
        
        // 统计各评分的数量
        return tickets.stream()
            .filter(t -> t.getSatisfactionRating() != null)
            .collect(Collectors.groupingBy(
                TicketEntity::getSatisfactionRating,
                Collectors.counting()
            ));
    }
} 