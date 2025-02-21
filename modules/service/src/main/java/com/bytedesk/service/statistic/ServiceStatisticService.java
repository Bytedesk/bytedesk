package com.bytedesk.service.statistic;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.time.Duration;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bytedesk.service.queue_member.QueueMemberEntity;
import com.bytedesk.service.queue_member.QueueMemberRepository;
import com.bytedesk.service.queue_member.QueueMemberStatusEnum;
import com.bytedesk.ai.robot.RobotEntity;
import com.bytedesk.ai.robot.RobotRepository;
import com.bytedesk.core.rbac.organization.OrganizationEntity;
import com.bytedesk.core.rbac.organization.OrganizationRepository;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.agent.AgentRepository;
import com.bytedesk.service.agent_status.AgentStatusLogEntity;
import com.bytedesk.service.agent_status.AgentStatusLogRepository;
import com.bytedesk.service.rating.RatingEntity;
import com.bytedesk.service.rating.RatingRepository;
import com.bytedesk.service.thread_transfer.ThreadTransferEntity;
import com.bytedesk.service.thread_transfer.ThreadTransferRepository;
import com.bytedesk.service.thread_transfer.ThreadTransferTypeEnum;
import com.bytedesk.service.utils.ServiceConvertUtils;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServiceStatisticService {
    
    private final ServiceStatisticRepository serviceStatisticRepository;

    private final QueueMemberRepository queueMemberRepository;
    
    private final AgentRepository agentRepository;
    
    private final AgentStatusLogRepository agentStatusLogRepository;
    
    private final RatingRepository ratingRepository;
    
    private final ThreadTransferRepository threadTransferRepository;

    private final OrganizationRepository organizationRepository;

    private final WorkgroupRepository workgroupRepository;

    private final RobotRepository robotRepository;


    /**
     * 查询某时间段统计
     */
    public ServiceStatisticResponse queryByDate(ServiceStatisticRequest request) {
        // 解析时间
        LocalDateTime startTime = BdDateUtils.parseLocalDateTime(request.getStatisticStartTime());
        LocalDateTime endTime = BdDateUtils.parseLocalDateTime(request.getStatisticEndTime());
        // 根据类型，调用不同的方法
        if (request.getType().equals(ServiceStatisticTypeEnum.ORG.name())) {
            return queryOrgStatistics(request.getOrgUid(), startTime, endTime);
        } else if (request.getType().equals(ServiceStatisticTypeEnum.WORKGROUP.name())) {
            return queryWorkgroupStatistics(request.getWorkgroupUid(), request.getOrgUid(), startTime, endTime);
        } else if (request.getType().equals(ServiceStatisticTypeEnum.AGENT.name())) {
            return queryAgentStatistics(request.getAgentUid(), request.getOrgUid(), startTime, endTime);
        } else if (request.getType().equals(ServiceStatisticTypeEnum.ROBOT.name())) {
            return queryRobotStatistics(request.getRobotUid(), request.getOrgUid(), startTime, endTime);
        } else {
            throw new RuntimeException("类型错误");
        }
    }

    /**
     * 查询组织统计
     */
    public ServiceStatisticResponse queryOrgStatistics(String orgUid, LocalDateTime startTime, LocalDateTime endTime) {
        return calculateOrgStatistic(orgUid, startTime, endTime, false);
    }

    /**
     * 查询工作组统计
     */
    public ServiceStatisticResponse queryWorkgroupStatistics(String orgUid, String workgroupUid, LocalDateTime startTime, LocalDateTime endTime) {
        return calculateWorkgroupStatistic(orgUid, workgroupUid, startTime, endTime, false);
    }

    /**
     * 查询处理人统计
     */
    public ServiceStatisticResponse queryAgentStatistics(String orgUid, String agentUid, LocalDateTime startTime, LocalDateTime endTime) {
        return calculateAgentStatistic(orgUid, agentUid, startTime, endTime, false);
    }

    /**
     * 查询机器人统计
     */
    public ServiceStatisticResponse queryRobotStatistics(String orgUid, String robotUid, LocalDateTime startTime, LocalDateTime endTime) {
        return calculateRobotStatistic(orgUid, robotUid, startTime, endTime, false);
    }


    /**
     * 计算今日统计
     */
    public void calculateTodayStatistics() {
        
        // 当日凌晨，到当前时间
        LocalDateTime startTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endTime = LocalDateTime.now();

        // 计算组织统计
        List<OrganizationEntity> organizations = organizationRepository.findAll();
        for (OrganizationEntity organization : organizations) {
            calculateOrgStatistic(organization.getUid(), startTime, endTime, true);
        }

        // 计算工作组统计
        List<WorkgroupEntity> workgroups = workgroupRepository.findAll();
        for (WorkgroupEntity workgroup : workgroups) {
            calculateWorkgroupStatistic(workgroup.getOrgUid(), workgroup.getUid(), startTime, endTime, true);
        }

        // 计算处理人统计
        List<AgentEntity> agents = agentRepository.findAll();
        for (AgentEntity agent : agents) {
            calculateAgentStatistic(agent.getOrgUid(), agent.getUid(), startTime, endTime, true);
        }

        // 计算机器人统计
        List<RobotEntity> robots = robotRepository.findAll();
        for (RobotEntity robot : robots) {
            calculateRobotStatistic(robot.getOrgUid(), robot.getUid(), startTime, endTime, true);
        }
    }

    /**
     * 计算组织统计
     */
    @Transactional
    public ServiceStatisticResponse calculateOrgStatistic(String orgUid, LocalDateTime startTime, LocalDateTime endTime, boolean shouldSave) {
        return calculateStatistic(ServiceStatisticTypeEnum.ORG.name(), orgUid, null, null, null, startTime, endTime, shouldSave);
    }

    /**
     * 计算工作组统计
     */
    @Transactional
    public ServiceStatisticResponse calculateWorkgroupStatistic(String orgUid, String workgroupUid, LocalDateTime startTime, LocalDateTime endTime, boolean shouldSave) {
        return calculateStatistic(ServiceStatisticTypeEnum.WORKGROUP.name(), orgUid, workgroupUid, null, null, startTime, endTime, shouldSave);
    }

    /**
     * 计算客服统计
     */
    @Transactional
    public ServiceStatisticResponse calculateAgentStatistic(String orgUid, String agentUid, LocalDateTime startTime, LocalDateTime endTime, boolean shouldSave) {
        return calculateStatistic(ServiceStatisticTypeEnum.AGENT.name(), orgUid, null, agentUid, null, startTime, endTime, shouldSave);
    }

    /**
     * 计算机器人统计
     */
    @Transactional
    public ServiceStatisticResponse calculateRobotStatistic(String orgUid, String robotUid, LocalDateTime startTime, LocalDateTime endTime, boolean shouldSave) {
        return calculateStatistic(ServiceStatisticTypeEnum.ROBOT.name(), orgUid, null, null, robotUid, startTime, endTime, shouldSave);
    }


    /**
     * 统一的统计计算方法
     */
    @Transactional
    private ServiceStatisticResponse calculateStatistic(String type, String orgUid, String workgroupUid, 
            String agentUid, String robotUid, LocalDateTime startTime, LocalDateTime endTime, boolean shouldSave) {
            
        // 当前日期和小时
        String date = BdDateUtils.formatToday();
        int hour = LocalDateTime.now().getHour();

        // 获取或创建统计实体
        ServiceStatisticEntity statistic = serviceStatisticRepository
            .findByTypeAndOrgUidAndWorkgroupUidAndAgentUidAndRobotUidAndDateAndHour(
                type, orgUid, workgroupUid, agentUid, robotUid, date, hour)
            .orElse(ServiceStatisticEntity.builder()
                .type(type)
                .workgroupUid(workgroupUid)
                .agentUid(agentUid)
                .robotUid(robotUid)
                .date(date)
                .hour(hour)
                .build());
        statistic.setOrgUid(orgUid);

        // 获取相关数据
        List<QueueMemberEntity> queueMembers = queueMemberRepository.findByOrgUidAndWorkgroupUidAndAgentUidAndCreatedAtBetween(orgUid, workgroupUid, agentUid, startTime, endTime);

        // 更新基础指标
        updateBasicMetrics(statistic, orgUid, startTime, endTime);
        updateThreadFlowMetrics(statistic, queueMembers);
        updateTimeMetrics(statistic, queueMembers);
        updateQualityMetrics(statistic, orgUid, startTime, endTime);
        updateMessageMetrics(statistic, queueMembers);

        // 根据类型更新特定指标
        if (agentUid != null) {
            AgentEntity agent = agentRepository.findByUid(agentUid).orElse(null);
            if (agent != null) {
                statistic.setCurrentThreadCount(agent.getCurrentThreadCount());
                List<AgentStatusLogEntity> statusLogs = agentStatusLogRepository
                    .findByAgentUidAndCreatedAtBetween(agentUid, startTime, endTime);
                updateAgentStatusMetrics(statistic, calculateStatusDuration(statusLogs));
            }
        } else if (robotUid != null) {
            updateRobotSpecificMetrics(statistic, queueMembers);
        }

        statistic.calculateRates();

        if (shouldSave) {
            serviceStatisticRepository.save(statistic);
        }

        return ServiceConvertUtils.convertToServiceStatisticResponse(statistic);
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
            .findByOrgUidAndWorkgroupUidAndAgentUidAndCreatedAtBetween(orgUid, null, null, startTime, endTime);

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
    // private void updateRobotMetrics(ServiceStatisticEntity statistic, List<QueueMemberEntity> queueMembers) {
    //     // TODO: 需要机器人会话标记来统计机器人相关指标
    //     // statistic.setRobotThreadCount();
    //     // statistic.setRobotToHumanCount();
    //     // statistic.setRobotSolveRate();
    // }

    /**
     * 更新工作量指标
     */
    // private void updateWorkloadMetrics(ServiceStatisticEntity statistic, String orgUid, 
    //         LocalDateTime startTime, LocalDateTime endTime) {
    //     // 获取时间段内的状态日志
    //     List<AgentStatusLogEntity> statusLogs = agentStatusLogRepository
    //         .findByOrgUidAndCreatedAtBetween(orgUid, startTime, endTime);
    //     // 计算各状态时长
    //     Map<String, Long> statusDuration = calculateStatusDuration(statusLogs);
        
    //     statistic.setOnlineTime(statusDuration.getOrDefault("AVAILABLE", 0L).intValue());
    //     statistic.setBusyTime(statusDuration.getOrDefault("BUSY", 0L).intValue());
    //     statistic.setOfflineTime(statusDuration.getOrDefault("OFFLINE", 0L).intValue());
    // }

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
