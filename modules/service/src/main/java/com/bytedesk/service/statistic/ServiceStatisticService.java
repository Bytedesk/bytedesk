package com.bytedesk.service.statistic;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.ArrayList;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRepository;
import com.bytedesk.service.queue_member.QueueMemberStatusEnum;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRepository;
import com.bytedesk.service.agent_status.AgentStatusLogEntity;
import com.bytedesk.service.agent_status.AgentStatusLogRepository;
import com.bytedesk.service.rating.RatingEntity;
import com.bytedesk.service.rating.RatingRepository;
import com.bytedesk.service.thread_transfer.ThreadTransferEntity;
import com.bytedesk.service.thread_transfer.ThreadTransferRepository;
import com.bytedesk.service.thread_transfer.ThreadTransferTypeEnum;

@Service
public class ServiceStatisticService {
    
    @Autowired
    private ServiceStatisticRepository serviceStatisticRepository;

    @Autowired
    private QueueMemberRepository queueMemberRepository;
    
    @Autowired
    private AgentRepository agentRepository;
    
    @Autowired
    private AgentStatusLogRepository agentStatusLogRepository;
    
    @Autowired
    private RatingRepository ratingRepository;
    
    @Autowired
    private ThreadTransferRepository threadTransferRepository;

    // 添加定时任务支持
    @Scheduled(cron = "0 0 * * * *") // 每小时执行
    public void hourlyStatistic() {
        // TODO: 获取所有需要统计的组织
        List<String> orgUids = new ArrayList<>();
        String date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        int hour = LocalDateTime.now().getHour();
        
        for (String orgUid : orgUids) {
            calculateAndUpdateStatistic(orgUid, date, hour);
        }
    }
    
    @Scheduled(cron = "0 0 0 * * *") // 每天零点执行
    public void dailyStatistic() {
        // TODO: 获取所有需要统计的组织
        List<String> orgUids = new ArrayList<>();
        String date = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        
        for (String orgUid : orgUids) {
            // 汇总前一天24小时的数据
            for (int hour = 0; hour < 24; hour++) {
                calculateAndUpdateStatistic(orgUid, date, hour);
            }
        }
    }

    /**
     * 计算并更新统计数据
     */
    @Transactional
    public void calculateAndUpdateStatistic(String orgUid, String date, int hour) {
        // 计算时间范围
        LocalDateTime startTime = LocalDateTime.parse(date + "T" + String.format("%02d:00:00", hour));
        LocalDateTime endTime = startTime.plusHours(1);

        // 获取或创建统计实体
        ServiceStatisticEntity statistic = serviceStatisticRepository
            .findByOrgUidAndDateAndHour(orgUid, date, hour)
            .orElse(ServiceStatisticEntity.builder()
                .date(date)
                .hour(hour)
                .build());
        statistic.setOrgUid(orgUid);

        // 获取指定时间段的队列成员数据
        List<QueueMemberEntity> queueMembers = queueMemberRepository
            .findByOrgUidAndCreatedAtBetween(orgUid, startTime, endTime);

        // 更新各项统计指标
        updateBasicMetrics(statistic, orgUid, startTime, endTime);
        updateThreadFlowMetrics(statistic, queueMembers);
        updateTimeMetrics(statistic, queueMembers);
        updateQualityMetrics(statistic, orgUid, startTime, endTime);
        updateMessageMetrics(statistic, queueMembers);
        updateRobotMetrics(statistic, queueMembers);
        updateWorkloadMetrics(statistic, orgUid, startTime, endTime);

        statistic.calculateRates();
        serviceStatisticRepository.save(statistic);
    }

    /**
     * 更新基础会话指标
     */
    private void updateBasicMetrics(ServiceStatisticEntity statistic, String orgUid, 
            LocalDateTime startTime, LocalDateTime endTime) {
        // 当前在线/离线客服数
        List<AgentEntity> agents = agentRepository.findByOrgUid(orgUid);
        statistic.setOnlineAgentCount((int) agents.stream()
            .filter(AgentEntity::isConnectedAndAvailable)
            .count());
        statistic.setOfflineAgentCount((int) agents.stream()
            .filter(AgentEntity::isOffline)
            .count());

        // 当前会话总数
        int currentThreads = agents.stream()
            .mapToInt(AgentEntity::getCurrentThreadCount)
            .sum();
        statistic.setCurrentThreadCount(currentThreads);

        // 获取队列成员数据
        List<QueueMemberEntity> queueMembers = queueMemberRepository
            .findByOrgUidAndCreatedAtBetween(orgUid, startTime, endTime);

        // 当前排队人数
        statistic.setQueuingThreadCount((int) queueMembers.stream()
            .filter(m -> QueueMemberStatusEnum.WAITING.name().equals(m.getStatus()))
            .count());

        // 当前最长等待时间
        queueMembers.stream()
            .filter(m -> QueueMemberStatusEnum.WAITING.name().equals(m.getStatus()))
            .mapToLong(QueueMemberEntity::getWaitTime)
            .max()
            .ifPresent(maxWait -> statistic.setMaxWaitingTime((int) maxWait));
    }

    /**
     * 更新会话流转指标
     */
    private void updateThreadFlowMetrics(ServiceStatisticEntity statistic, List<QueueMemberEntity> queueMembers) {
        // 总流入会话量
        statistic.setTotalIncomingThreads(queueMembers.size());

        // 已接入会话量
        statistic.setAcceptedThreadCount((int) queueMembers.stream()
            .filter(m -> m.getAcceptTime() != null)
            .count());

        // 放弃排队会话量
        statistic.setAbandonedThreadCount((int) queueMembers.stream()
            .filter(m -> QueueMemberStatusEnum.ABANDONED.name().equals(m.getStatus()))
            .count());

        // 添加邀请会话量统计
        statistic.setInvitedThreadCount((int) queueMembers.stream()
        .filter(m -> "INVITE".equals(m.getAcceptType()))
        .count());
    }

    /**
     * 更新时间指标
     */
    private void updateTimeMetrics(ServiceStatisticEntity statistic, List<QueueMemberEntity> queueMembers) {
        List<QueueMemberEntity> acceptedMembers = queueMembers.stream()
            .filter(m -> m.getAcceptTime() != null)
            .collect(Collectors.toList());

        if (!acceptedMembers.isEmpty()) {
            // 平均等待时间
            double avgWaitTime = acceptedMembers.stream()
                .mapToLong(QueueMemberEntity::getWaitTime)
                .average()
                .orElse(0);
            statistic.setAvgWaitingTime((int) avgWaitTime);

            // 平均首次响应时间
            double avgFirstResponse = acceptedMembers.stream()
                .filter(m -> m.getFirstResponseTime() != null)
                .mapToLong(m -> m.getFirstResponseTime().getSecond() - m.getAcceptTime().getSecond())
                .average()
                .orElse(0);
            statistic.setAvgFirstResponseTime((int) avgFirstResponse);

            // 最长/最短响应时间
            acceptedMembers.stream()
                .mapToInt(QueueMemberEntity::getMaxResponseTime)
                .max()
                .ifPresent(statistic::setMaxResponseTime);

            // 添加平均会话时长计算
            double avgConversationTime = acceptedMembers.stream()
                .filter(m -> m.getCloseTime() != null)
                .mapToLong(m -> Duration.between(m.getAcceptTime(), m.getCloseTime()).getSeconds())
                .average()
                .orElse(0);
            statistic.setAvgConversationTime((int) avgConversationTime);

            // 添加响应时间分布统计
            int within1Min = 0, within5Min = 0, over5Min = 0;
            for (QueueMemberEntity member : acceptedMembers) {
                int responseTime = member.getFirstResponseTime() != null ? 
                    (int) Duration.between(member.getAcceptTime(), member.getFirstResponseTime()).getSeconds() : 0;
                if (responseTime <= 60) within1Min++;
                else if (responseTime <= 300) within5Min++;
                else over5Min++;
            }
            statistic.setResponseWithin1Min(within1Min);
            statistic.setResponseWithin5Min(within5Min);
            statistic.setResponseOver5Min(over5Min);

            // 添加会话时长分布统计
            int durationWithin5Min = 0, durationWithin15Min = 0, durationOver15Min = 0;
            for (QueueMemberEntity member : acceptedMembers) {
                if (member.getCloseTime() != null) {
                    int duration = (int) Duration.between(member.getAcceptTime(), member.getCloseTime()).getSeconds();
                    if (duration <= 300) durationWithin5Min++;
                    else if (duration <= 900) durationWithin15Min++;
                    else durationOver15Min++;
                }
            }
            statistic.setDurationWithin5Min(durationWithin5Min);
            statistic.setDurationWithin15Min(durationWithin15Min);
            statistic.setDurationOver15Min(durationOver15Min);
        }
    }

    /**
     * 更新质量指标
     */
    private void updateQualityMetrics(ServiceStatisticEntity statistic, String orgUid, 
            LocalDateTime startTime, LocalDateTime endTime) {
        // 获取评价数据
        List<RatingEntity> ratings = ratingRepository
            .findByOrgUidAndCreatedAtBetween(orgUid, startTime, endTime);

        // 评价总数
        statistic.setTotalRatingCount(ratings.size());

        // 满意评价数(4-5星为满意)
        long satisfiedCount = ratings.stream()
            .filter(r -> r.getScore() >= 4)
            .count();
        statistic.setSatisfiedRatingCount((int) satisfiedCount);

        // 更新转接相关指标
        List<ThreadTransferEntity> transfers = threadTransferRepository
            .findByOrgUidAndCreatedAtBetween(orgUid, startTime, endTime);
            
        // 转接总量
        statistic.setTransferredThreadCount(transfers.size());
        
        // 超时转接量
        long timeoutTransfers = transfers.stream()
            .filter(t -> t.getType().equals(ThreadTransferTypeEnum.TIMEOUT.name()))
            .count();
        statistic.setTimeoutTransferCount((int) timeoutTransfers);

        // 添加首次解决率统计
        List<QueueMemberEntity> solvedMembers = queueMemberRepository
            .findByOrgUidAndCreatedAtBetweenAndSolved(orgUid, startTime, endTime, true);
        statistic.setFirstSolveCount(solvedMembers.size());

        // 添加会话分配方式统计
        List<QueueMemberEntity> autoAssigned = queueMemberRepository
            .findByOrgUidAndCreatedAtBetweenAndAcceptType(orgUid, startTime, endTime, "AUTO");
        List<QueueMemberEntity> manualAssigned = queueMemberRepository
            .findByOrgUidAndCreatedAtBetweenAndAcceptType(orgUid, startTime, endTime, "MANUAL");
        
        statistic.setAutoAssignedCount(autoAssigned.size());
        statistic.setManualAssignedCount(manualAssigned.size());
    }

    /**
     * 更新消息指标
     */
    private void updateMessageMetrics(ServiceStatisticEntity statistic, List<QueueMemberEntity> queueMembers) {
        // 客服消息总数
        int totalAgentMessages = queueMembers.stream()
            .mapToInt(QueueMemberEntity::getAgentMessageCount)
            .sum();
        statistic.setAgentMessageCount(totalAgentMessages);

        // 访客消息总数
        int totalVisitorMessages = queueMembers.stream()
            .mapToInt(QueueMemberEntity::getVisitorMessageCount)
            .sum();
        statistic.setVisitorMessageCount(totalVisitorMessages);

        // 平均会话消息数
        if (!queueMembers.isEmpty()) {
            int avgMessages = (totalAgentMessages + totalVisitorMessages) / queueMembers.size();
            statistic.setAvgMessagePerThread(avgMessages);
        }
    }

    /**
     * 更新机器人指标
     */
    private void updateRobotMetrics(ServiceStatisticEntity statistic, List<QueueMemberEntity> queueMembers) {
        // TODO: 需要机器人会话标记来统计机器人相关指标
        // statistic.setRobotThreadCount();
        // statistic.setRobotToHumanCount();
        // statistic.setRobotSolveRate();
    }

    /**
     * 更新工作量指标
     */
    private void updateWorkloadMetrics(ServiceStatisticEntity statistic, String orgUid, 
            LocalDateTime startTime, LocalDateTime endTime) {
        // 获取时间段内的状态日志
        List<AgentStatusLogEntity> statusLogs = agentStatusLogRepository
            .findByOrgUidAndCreatedAtBetween(orgUid, startTime, endTime);

        // 计算各状态时长
        Map<String, Long> statusDuration = calculateStatusDuration(statusLogs);
        
        statistic.setOnlineTime(statusDuration.getOrDefault("AVAILABLE", 0L).intValue());
        statistic.setBusyTime(statusDuration.getOrDefault("BUSY", 0L).intValue());
        statistic.setOfflineTime(statusDuration.getOrDefault("OFFLINE", 0L).intValue());
    }

    /**
     * 计算状态持续时长
     */
    private Map<String, Long> calculateStatusDuration(List<AgentStatusLogEntity> logs) {
        Map<String, Long> duration = new HashMap<>();
        
        if (logs.isEmpty()) {
            return duration;
        }

        // 按客服和时间排序
        logs.sort(Comparator.comparing(AgentStatusLogEntity::getAgentUid)
            .thenComparing(AgentStatusLogEntity::getCreatedAt));

        // 计算每个状态的持续时间
        String currentAgent = null;
        LocalDateTime statusStart = null;
        String currentStatus = null;

        for (AgentStatusLogEntity log : logs) {
            if (!log.getAgentUid().equals(currentAgent)) {
                // 新客服，重置计数
                currentAgent = log.getAgentUid();
                statusStart = log.getCreatedAt();
                currentStatus = log.getStatus();
                continue;
            }

            // 计算持续时间
            if (statusStart != null && currentStatus != null) {
                long seconds = Duration.between(statusStart, log.getCreatedAt()).getSeconds();
                duration.merge(currentStatus, seconds, Long::sum);
            }

            // 更新状态
            statusStart = log.getCreatedAt();
            currentStatus = log.getStatus();
        }

        return duration;
    }

    /**
     * 按工作组统计
     */
    @Transactional
    public void calculateWorkgroupStatistic(String workgroupUid, String date, int hour) {
        // 计算时间范围
        LocalDateTime startTime = LocalDateTime.parse(date + "T" + String.format("%02d:00:00", hour));
        LocalDateTime endTime = startTime.plusHours(1);

        // 获取或创建工作组统计实体
        ServiceStatisticEntity statistic = serviceStatisticRepository
            .findByWorkgroupUidAndDateAndHour(workgroupUid, date, hour)
            .orElse(ServiceStatisticEntity.builder()
                .workgroupUid(workgroupUid)
                .type(ServiceStatisticTypeEnum.WORKGROUP.name())
                .date(date)
                .hour(hour)
                .build());

        // 获取工作组相关数据
        List<QueueMemberEntity> workgroupMembers = queueMemberRepository
            .findByWorkgroupUidAndCreatedAtBetween(workgroupUid, startTime, endTime);

        // 更新统计指标
        updateThreadFlowMetrics(statistic, workgroupMembers);
        updateTimeMetrics(statistic, workgroupMembers);
        updateQualityMetrics(statistic, workgroupUid, startTime, endTime);
        updateMessageMetrics(statistic, workgroupMembers);
        updateWorkloadMetrics(statistic, workgroupUid, startTime, endTime);

        statistic.calculateRates();
        serviceStatisticRepository.save(statistic);
    }

    /**
     * 按客服统计
     */
    @Transactional
    public void calculateAgentStatistic(String agentUid, String date, int hour) {
        // 计算时间范围
        LocalDateTime startTime = LocalDateTime.parse(date + "T" + String.format("%02d:00:00", hour));
        LocalDateTime endTime = startTime.plusHours(1);

        ServiceStatisticEntity statistic = serviceStatisticRepository
            .findByAgentUidAndDateAndHour(agentUid, date, hour)
            .orElse(ServiceStatisticEntity.builder()
                .agentUid(agentUid)
                .type(ServiceStatisticTypeEnum.AGENT.name())
                .date(date)
                .hour(hour)
                .build());

        // 获取客服相关数据
        List<QueueMemberEntity> agentMembers = queueMemberRepository
            .findByAgentUidAndCreatedAtBetween(agentUid, startTime, endTime);
        AgentEntity agent = agentRepository.findByUid(agentUid).orElse(null);

        if (agent != null) {
            // 更新客服特有指标
            statistic.setCurrentThreadCount(agent.getCurrentThreadCount());
            
            // 计算在线状态分布
            List<AgentStatusLogEntity> statusLogs = agentStatusLogRepository
                .findByAgentUidAndCreatedAtBetween(agentUid, startTime, endTime);
            Map<String, Long> statusDuration = calculateStatusDuration(statusLogs);
            updateAgentStatusMetrics(statistic, statusDuration);
        }

        // 更新通用指标
        updateThreadFlowMetrics(statistic, agentMembers);
        updateTimeMetrics(statistic, agentMembers);
        updateQualityMetrics(statistic, agentUid, startTime, endTime);
        updateMessageMetrics(statistic, agentMembers);

        statistic.calculateRates();
        serviceStatisticRepository.save(statistic);
    }

    /**
     * 按机器人统计
     */
    @Transactional
    public void calculateRobotStatistic(String robotUid, String date, int hour) {
        // 计算时间范围
        LocalDateTime startTime = LocalDateTime.parse(date + "T" + String.format("%02d:00:00", hour));
        LocalDateTime endTime = startTime.plusHours(1);

        ServiceStatisticEntity statistic = serviceStatisticRepository
            .findByRobotUidAndDateAndHour(robotUid, date, hour)
            .orElse(ServiceStatisticEntity.builder()
                .robotUid(robotUid)
                .type(ServiceStatisticTypeEnum.ROBOT.name())
                .date(date)
                .hour(hour)
                .build());

        // 获取机器人相关数据
        List<QueueMemberEntity> robotMembers = queueMemberRepository
            .findByRobotUidAndCreatedAtBetween(robotUid, startTime, endTime);

        // 更新机器人特有指标
        updateRobotSpecificMetrics(statistic, robotMembers);

        // 更新通用指标
        updateThreadFlowMetrics(statistic, robotMembers);
        updateTimeMetrics(statistic, robotMembers);
        updateMessageMetrics(statistic, robotMembers);

        statistic.calculateRates();
        serviceStatisticRepository.save(statistic);
    }

    /**
     * 更新客服状态分布指标
     */
    private void updateAgentStatusMetrics(ServiceStatisticEntity statistic, Map<String, Long> statusDuration) {
        statistic.setAvailableAgentCount(statusDuration.getOrDefault("AVAILABLE", 0L).intValue());
        statistic.setBusyAgentCount(statusDuration.getOrDefault("BUSY", 0L).intValue());
        statistic.setAwayAgentCount(statusDuration.getOrDefault("AWAY", 0L).intValue());
    }

    /**
     * 更新机器人特有指标
     */
    private void updateRobotSpecificMetrics(ServiceStatisticEntity statistic, List<QueueMemberEntity> robotMembers) {
        // 机器人会话总量
        statistic.setRobotThreadCount(robotMembers.size());

        // 转人工数量
        long transferToHuman = robotMembers.stream()
            .filter(m -> m.getAcceptTime() != null)
            .count();
        statistic.setRobotToHumanCount((int) transferToHuman);

        // 机器人解决率
        if (!robotMembers.isEmpty()) {
            double solveRate = (robotMembers.size() - transferToHuman) * 100.0 / robotMembers.size();
            statistic.setRobotSolveRate(solveRate);
        }
    }
}
