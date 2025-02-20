package com.bytedesk.ticket.statistic;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRepository;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRepository;
import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketPriorityEnum;
import com.bytedesk.ticket.ticket.TicketRepository;
import com.bytedesk.ticket.ticket.TicketStatusEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketStatisticService {
    
    private final TicketRepository ticketRepository;
    
    private final TicketStatisticRepository statisticRepository;

    private final WorkgroupRepository workgroupRepository;

    private final AgentRepository agentRepository;

    /**
     * 计算所有工单统计
     */
    public void calculateAllStatistics() {
        // 当日凌晨，到当前时间
        LocalDateTime startTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endTime = LocalDateTime.now();

        // 计算工作组工单统计
        List<WorkgroupEntity> workgroups = workgroupRepository.findAll();
        for (WorkgroupEntity workgroup : workgroups) {
            calculateWorkgroupStatistics(workgroup.getUid(), startTime, endTime);
        }
        
        // 计算处理人工单统计
        List<AgentEntity> agents = agentRepository.findAll();
        for (AgentEntity agent : agents) {
            calculateAssigneeStatistics(agent.getUid(), startTime, endTime);
        }


    }




    /**
     * 计算工作组的工单统计
     */
    @Transactional
    public TicketStatisticEntity calculateWorkgroupStatistics(String workgroupUid, LocalDateTime startTime, LocalDateTime endTime) {
        List<TicketEntity> tickets = ticketRepository.findByWorkgroupContainingAndCreatedAtBetween(
            "\"uid\":\"" + workgroupUid + "\"", startTime, endTime);

        TicketStatisticEntity statistic = TicketStatisticEntity.builder()
            .workgroupUid(workgroupUid)
            .statisticStartTime(startTime)
            .statisticEndTime(endTime)
            .build();

        // 基本统计
        calculateBasicStatistics(statistic, tickets);
        // 状态统计
        calculateStatusStatistics(statistic, tickets);
        // 优先级统计
        calculatePriorityStatistics(statistic, tickets);
        // 时间统计
        calculateTimeStatistics(statistic, tickets);
        // 满意度统计
        calculateSatisfactionStatistics(statistic, tickets);

        return statisticRepository.save(statistic);
    }

    /**
     * 计算处理人的工单统计
     */
    @Transactional
    public TicketStatisticEntity calculateAssigneeStatistics(String assigneeUid, LocalDateTime startTime, LocalDateTime endTime) {
        List<TicketEntity> tickets = ticketRepository.findByAssigneeContainingAndCreatedAtBetween(
            "\"uid\":\"" + assigneeUid + "\"", startTime, endTime);

        TicketStatisticEntity statistic = TicketStatisticEntity.builder()
            .assigneeUid(assigneeUid)
            .statisticStartTime(startTime)
            .statisticEndTime(endTime)
            .build();

        calculateBasicStatistics(statistic, tickets);
        calculateStatusStatistics(statistic, tickets);
        calculateTimeStatistics(statistic, tickets);
        calculateSatisfactionStatistics(statistic, tickets);

        return statisticRepository.save(statistic);
    }

    /**
     * 计算基本统计
     */
    private void calculateBasicStatistics(TicketStatisticEntity statistic, List<TicketEntity> tickets) {
        statistic.setTotalTickets(tickets.size());
        statistic.setOpenTickets(tickets.stream()
            .filter(t -> !t.getStatus().equals(TicketStatusEnum.CLOSED.name()))
            .count());
        statistic.setClosedTickets(tickets.stream()
            .filter(t -> t.getStatus().equals(TicketStatusEnum.CLOSED.name()))
            .count());
    }

    /**
     * 计算状态统计
     */
    private void calculateStatusStatistics(TicketStatisticEntity statistic, List<TicketEntity> tickets) {
        Map<String, Long> statusCounts = tickets.stream()
            .collect(Collectors.groupingBy(TicketEntity::getStatus, Collectors.counting()));

        statistic.setNewTickets(statusCounts.getOrDefault(TicketStatusEnum.NEW.name(), 0L));
        statistic.setClaimedTickets(statusCounts.getOrDefault(TicketStatusEnum.CLAIMED.name(), 0L));
        statistic.setProcessingTickets(statusCounts.getOrDefault(TicketStatusEnum.PROCESSING.name(), 0L));
        statistic.setResolvedTickets(statusCounts.getOrDefault(TicketStatusEnum.RESOLVED.name(), 0L));
        // ... 其他状态统计
    }

    /**
     * 计算优先级统计
     */
    private void calculatePriorityStatistics(TicketStatisticEntity statistic, List<TicketEntity> tickets) {
        Map<String, Long> priorityCounts = tickets.stream()
            .collect(Collectors.groupingBy(TicketEntity::getPriority, Collectors.counting()));

        statistic.setCriticalTickets(priorityCounts.getOrDefault(TicketPriorityEnum.CRITICAL.name(), 0L));
        statistic.setHighTickets(priorityCounts.getOrDefault(TicketPriorityEnum.HIGH.name(), 0L));
        statistic.setMediumTickets(priorityCounts.getOrDefault(TicketPriorityEnum.MEDIUM.name(), 0L));
        statistic.setLowTickets(priorityCounts.getOrDefault(TicketPriorityEnum.LOW.name(), 0L));
    }

    /**
     * 计算时间统计
     */
    private void calculateTimeStatistics(TicketStatisticEntity statistic, List<TicketEntity> tickets) {
        List<TicketEntity> resolvedTickets = tickets.stream()
            .filter(t -> t.getResolvedTime() != null)
            .toList();

        if (!resolvedTickets.isEmpty()) {
            // 计算平均解决时间（小时）
            double avgResolutionTime = resolvedTickets.stream()
                .mapToLong(t -> Duration.between(t.getCreatedAt(), t.getResolvedTime()).toHours())
                .average()
                .orElse(0.0);
            statistic.setAverageResolutionTime(avgResolutionTime);

            // 计算SLA达标率
            long slaCompliantTickets = resolvedTickets.stream()
                .filter(this::isSLACompliant)
                .count();
            statistic.setSlaComplianceRate((double) slaCompliantTickets / resolvedTickets.size() * 100);
        }
    }

    /**
     * 计算满意度统计
     */
    private void calculateSatisfactionStatistics(TicketStatisticEntity statistic, List<TicketEntity> tickets) {
        List<TicketEntity> verifiedTickets = tickets.stream()
            .filter(t -> t.getVerified() != null)
            .toList();

        if (!verifiedTickets.isEmpty()) {
            long satisfiedCount = verifiedTickets.stream()
                .filter(TicketEntity::getVerified)
                .count();
            
            statistic.setSatisfiedTickets(satisfiedCount);
            statistic.setUnsatisfiedTickets(verifiedTickets.size() - satisfiedCount);
            statistic.setCustomerSatisfactionRate((double) satisfiedCount / verifiedTickets.size() * 100);
        }
    }

    /**
     * 判断工单是否符合SLA
     */
    private boolean isSLACompliant(TicketEntity ticket) {
        if (ticket.getResolvedTime() == null) {
            return false;
        }

        long resolutionHours = Duration.between(ticket.getCreatedAt(), ticket.getResolvedTime()).toHours();
        
        // 根据优先级判断SLA
        return switch (ticket.getPriority()) {
            case "CRITICAL" -> resolutionHours <= 0.5;  // 30分钟
            case "HIGH" -> resolutionHours <= 1;        // 1小时
            case "MEDIUM" -> resolutionHours <= 4;      // 4小时
            case "LOW" -> resolutionHours <= 8;         // 8小时
            default -> resolutionHours <= 24;           // 1天
        };
    }

    /**
     * 更新未读统计
     */
    @Transactional
    public void updateUnreadStatistics(String ticketUid, String userUid) {
        TicketEntity ticket = ticketRepository.findByUid(ticketUid)
            .orElseThrow(() -> new RuntimeException("工单不存在"));
            
        String workgroupUid = ticket.getWorkgroup().getUid();
        TicketStatisticEntity statistic = statisticRepository.findByWorkgroupUid(workgroupUid)
            .orElseThrow(() -> new RuntimeException("统计记录不存在"));

        statistic.setUnreadTickets(statistic.getUnreadTickets() + 1);
        statisticRepository.save(statistic);
    }

    /**
     * 清除未读统计
     */
    @Transactional
    public void clearUnreadStatistics(String ticketUid, String userUid) {
        TicketEntity ticket = ticketRepository.findByUid(ticketUid)
            .orElseThrow(() -> new RuntimeException("工单不存在"));
            
        String workgroupUid = ticket.getWorkgroup().getUid();
        TicketStatisticEntity statistic = statisticRepository.findByWorkgroupUid(workgroupUid)
            .orElseThrow(() -> new RuntimeException("统计记录不存在"));

        if (statistic.getUnreadTickets() > 0) {
            statistic.setUnreadTickets(statistic.getUnreadTickets() - 1);
        }
        statisticRepository.save(statistic);
    }
}
