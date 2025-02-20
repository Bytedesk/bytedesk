package com.bytedesk.ticket.statistic;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.constant.BytedeskConsts;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationRepository;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.DateUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRepository;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRepository;
import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketPriorityEnum;
import com.bytedesk.ticket.ticket.TicketRepository;
import com.bytedesk.ticket.ticket.TicketStatusEnum;
import com.bytedesk.ticket.utils.TicketConvertUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketStatisticService {
    
    private final TicketRepository ticketRepository;
    
    private final TicketStatisticRepository statisticRepository;

    private final OrganizationRepository organizationRepository;

    private final WorkgroupRepository workgroupRepository;

    private final AgentRepository agentRepository;

    private final UidUtils uidUtils;

    /**
     * 查询某时间段统计
     */
    public TicketStatisticResponse queryByDate(TicketStatisticRequest request) {
        // 解析时间
        LocalDateTime startTime = DateUtils.parseLocalDateTime(request.getStatisticStartTime());
        LocalDateTime endTime = DateUtils.parseLocalDateTime(request.getStatisticEndTime());
        // 根据类型，调用不同的方法
        if (request.getType().equals(BytedeskConsts.STATISTIC_FILTER_TYPE_ORG)) {
            return queryOrgStatistics(request.getOrgUid(), startTime, endTime);
        } else if (request.getType().equals(BytedeskConsts.STATISTIC_FILTER_TYPE_WORKGROUP)) {
            return queryWorkgroupStatistics(request.getWorkgroupUid(), request.getOrgUid(), startTime, endTime);
        } else if (request.getType().equals(BytedeskConsts.STATISTIC_FILTER_TYPE_AGENT)) {
            return queryAssigneeStatistics(request.getAssigneeUid(), request.getOrgUid(), startTime, endTime);
        } else {
            throw new RuntimeException("类型错误");
        }
    }

    /**
     * 查询组织统计
     */
    public TicketStatisticResponse queryOrgStatistics(String orgUid, LocalDateTime startTime, LocalDateTime endTime) {
        return calculateOrgStatistics(orgUid, startTime, endTime, false);
    }

    /**
     * 查询工作组统计
     */
    public TicketStatisticResponse queryWorkgroupStatistics(String workgroupUid, String orgUid, LocalDateTime startTime, LocalDateTime endTime) {
        return calculateWorkgroupStatistics(workgroupUid, orgUid, startTime, endTime, false);
    }

    /**
     * 查询处理人统计
     */
    public TicketStatisticResponse queryAssigneeStatistics(String assigneeUid, String orgUid, LocalDateTime startTime, LocalDateTime endTime) {
        return calculateAssigneeStatistics(assigneeUid, orgUid, startTime, endTime, false);
    }

    /**
     * 计算所有工单统计
     */
    public void calculateTodayStatistics() {
        // 当日凌晨，到当前时间
        LocalDateTime startTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endTime = LocalDateTime.now();

        // 计算组织工单统计
        List<OrganizationEntity> organizations = organizationRepository.findAll();
        for (OrganizationEntity organization : organizations) {
            calculateOrgStatistics(organization.getUid(), startTime, endTime, true);
        }

        // 计算工作组工单统计
        List<WorkgroupEntity> workgroups = workgroupRepository.findAll();
        for (WorkgroupEntity workgroup : workgroups) {
            calculateWorkgroupStatistics(workgroup.getUid(), workgroup.getOrgUid(), startTime, endTime, true);
        }

        // 计算处理人工单统计
        List<AgentEntity> agents = agentRepository.findAll();
        for (AgentEntity agent : agents) {
            calculateAssigneeStatistics(agent.getUid(), agent.getOrgUid(), startTime, endTime, true);
        }
    }

    /**
     * 计算组织的工单统计
     */
    @Transactional
    public TicketStatisticResponse calculateOrgStatistics(
            String orgUid, 
            LocalDateTime startTime, 
            LocalDateTime endTime, 
            boolean shouldSave) {

        List<TicketEntity> tickets = ticketRepository.findByOrgUidAndCreatedAtBetween(orgUid, startTime, endTime);

        return calculateStatistics(tickets, orgUid, null, null, startTime, endTime, TicketStatisticTypeEnum.ORG.name(), shouldSave);
    }
    
    /**
     * 计算工作组的工单统计
     */
    @Transactional
    public TicketStatisticResponse calculateWorkgroupStatistics(
            String workgroupUid, 
            String orgUid, 
            LocalDateTime startTime, 
            LocalDateTime endTime, 
            boolean shouldSave) {

        List<TicketEntity> tickets = ticketRepository.findByWorkgroupContainingAndCreatedAtBetween(
            "\"uid\":\"" + workgroupUid + "\"", startTime, endTime);

        return calculateStatistics(tickets, orgUid, workgroupUid, null, startTime, endTime, TicketStatisticTypeEnum.WORKGROUP.name(), shouldSave);
    }

    /**
     * 计算处理人的工单统计
     */
    @Transactional
    public TicketStatisticResponse calculateAssigneeStatistics(
            String assigneeUid, 
            String orgUid, 
            LocalDateTime startTime, 
            LocalDateTime endTime, 
            boolean shouldSave) {

        List<TicketEntity> tickets = ticketRepository.findByAssigneeContainingAndCreatedAtBetween(
            "\"uid\":\"" + assigneeUid + "\"", startTime, endTime);

        return calculateStatistics(tickets, orgUid, null, assigneeUid, startTime, endTime, TicketStatisticTypeEnum.AGENT.name(), shouldSave);
    }

     /**
     * 计算统计数据
     */
    private TicketStatisticResponse calculateStatistics(
            List<TicketEntity> tickets, 
            String orgUid,
            String workgroupUid,
            String assigneeUid,
            LocalDateTime startTime, 
            LocalDateTime endTime,
            String type,
            boolean shouldSave) {

        // 根据orgUid、workgroupUid/assigneeUid、date判断是否已经存在
        Optional<TicketStatisticEntity> statisticOptional = statisticRepository
            .findByTypeAndOrgUidAndWorkgroupUidAndAssigneeUidAndDate(
                type, orgUid, workgroupUid, assigneeUid, DateUtils.formatToday());

        TicketStatisticEntity statistic;
        if (statisticOptional.isPresent()) {
            // 更新现有记录
            statistic = statisticOptional.get();
            statistic.setStatisticStartTime(startTime);
            statistic.setStatisticEndTime(endTime);
        } else {
            // 创建新记录
            statistic = TicketStatisticEntity.builder()
                .workgroupUid(workgroupUid)
                .assigneeUid(assigneeUid)
                .statisticStartTime(startTime)
                .statisticEndTime(endTime)
                .type(type)
                .build();
            statistic.setUid(uidUtils.getUid());
            statistic.setOrgUid(orgUid);
        }

        // 计算各项统计指标
        calculateBasicStatistics(statistic, tickets);
        calculateStatusStatistics(statistic, tickets);
        calculatePriorityStatistics(statistic, tickets);
        calculateTimeStatistics(statistic, tickets);
        calculateSatisfactionStatistics(statistic, tickets);

        // 判断是否所有统计指标都为0
        if (isAllStatisticsZero(statistic)) {
            log.info("所有统计指标为0，不保存统计记录 workgroupUid: {}, assigneeUid: {}", 
                workgroupUid, assigneeUid);
            return null;
        }

        if (shouldSave) {
            statistic.setDate(DateUtils.formatToday());
            TicketStatisticEntity savedStatistic = statisticRepository.save(statistic);
            if (savedStatistic == null) {
                throw new RuntimeException("保存统计记录失败");
            }
            return TicketConvertUtils.convertToStatisticResponse(savedStatistic);
        }

        return TicketConvertUtils.convertToStatisticResponse(statistic);
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
        statistic.setUnclaimedTickets(statusCounts.getOrDefault(TicketStatusEnum.UNCLAIMED.name(), 0L));
        statistic.setProcessingTickets(statusCounts.getOrDefault(TicketStatusEnum.PROCESSING.name(), 0L));
        statistic.setPendingTickets(statusCounts.getOrDefault(TicketStatusEnum.PENDING.name(), 0L));
        statistic.setHoldingTickets(statusCounts.getOrDefault(TicketStatusEnum.HOLDING.name(), 0L));
        statistic.setReopenedTickets(statusCounts.getOrDefault(TicketStatusEnum.REOPENED.name(), 0L));
        statistic.setResolvedTickets(statusCounts.getOrDefault(TicketStatusEnum.RESOLVED.name(), 0L));
        statistic.setEscalatedTickets(statusCounts.getOrDefault(TicketStatusEnum.ESCALATED.name(), 0L));
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

    /**
     * 判断统计指标是否全为0
     */
    private boolean isAllStatisticsZero(TicketStatisticEntity statistic) {
        return statistic.getTotalTickets() == 0 &&
               statistic.getOpenTickets() == 0 &&
               statistic.getClosedTickets() == 0 &&
               statistic.getUnreadTickets() == 0 &&
               statistic.getNewTickets() == 0 &&
               statistic.getClaimedTickets() == 0 &&
               statistic.getUnclaimedTickets() == 0 &&
               statistic.getProcessingTickets() == 0 &&
               statistic.getPendingTickets() == 0 &&
               statistic.getHoldingTickets() == 0 &&
               statistic.getReopenedTickets() == 0 &&
               statistic.getResolvedTickets() == 0 &&
               statistic.getEscalatedTickets() == 0 &&
               statistic.getCriticalTickets() == 0 &&
               statistic.getHighTickets() == 0 &&
               statistic.getMediumTickets() == 0 &&
               statistic.getLowTickets() == 0 &&
               statistic.getSlaBreachCount() == 0 &&
               statistic.getWorkgroupTickets() == 0 &&
               statistic.getAssigneeTickets() == 0 &&
               statistic.getSatisfiedTickets() == 0 &&
               statistic.getUnsatisfiedTickets() == 0;
    }

}
