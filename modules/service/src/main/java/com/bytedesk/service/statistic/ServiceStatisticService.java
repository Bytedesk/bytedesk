package com.bytedesk.service.statistic;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.time.LocalDateTime;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 计算并更新统计数据
     * @param orgUid 组织ID
     * @param date 日期
     * @param hour 小时
     */
    @Transactional
    public void calculateAndUpdateStatistic(String orgUid, String date, int hour) {
        // 获取或创建统计实体
        ServiceStatisticEntity statistic = serviceStatisticRepository
            .findByOrgUidAndDateAndHour(orgUid, date, hour)
            .orElse(ServiceStatisticEntity.builder()
                // .orgUid(orgUid)
                .date(date)
                .hour(hour)
                .build());
        statistic.setOrgUid(orgUid);

        // 获取指定时间段的队列成员数据
        List<QueueMemberEntity> queueMembers = queueMemberRepository
            .findByOrgUidAndDateAndHour(orgUid, date, hour);

        // 更新各项统计指标
        updateBasicMetrics(statistic, orgUid, date, hour);
        updateThreadFlowMetrics(statistic, queueMembers);
        updateTimeMetrics(statistic, queueMembers);
        updateQualityMetrics(statistic, orgUid, date, hour);
        updateMessageMetrics(statistic, queueMembers);
        updateRobotMetrics(statistic, queueMembers);
        updateWorkloadMetrics(statistic, orgUid, date, hour);

        // 计算各类比率
        statistic.calculateRates();

        // 保存统计数据
        serviceStatisticRepository.save(statistic);
    }

    /**
     * 更新基础会话指标
     */
    private void updateBasicMetrics(ServiceStatisticEntity statistic, String orgUid, String date, int hour) {
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
        }
    }

    /**
     * 更新质量指标
     */
    private void updateQualityMetrics(ServiceStatisticEntity statistic, String orgUid, String date, int hour) {
        // 获取评价数据
        List<RatingEntity> ratings = ratingRepository
            .findByOrgUidAndDateAndHour(orgUid, date, hour);

        // 评价总数
        statistic.setTotalRatingCount(ratings.size());

        // 满意评价数(4-5星为满意)
        long satisfiedCount = ratings.stream()
            .filter(r -> r.getScore() >= 4)
            .count();
        statistic.setSatisfiedRatingCount((int) satisfiedCount);

        // 更新转接相关指标
        List<ThreadTransferEntity> transfers = threadTransferRepository
            .findByOrgUidAndDateAndHour(orgUid, date, hour);
            
        // 转接总量
        statistic.setTransferredThreadCount(transfers.size());
        
        // 超时转接量
        long timeoutTransfers = transfers.stream()
            .filter(t -> t.getType().equals(ThreadTransferTypeEnum.TIMEOUT.name()))
            .count();
        statistic.setTimeoutTransferCount((int) timeoutTransfers);
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
    private void updateWorkloadMetrics(ServiceStatisticEntity statistic, String orgUid, String date, int hour) {
        // 获取时间段内的状态日志
        List<AgentStatusLogEntity> statusLogs = agentStatusLogRepository
            .findByOrgUidAndDateAndHour(orgUid, date, hour);

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
            .thenComparing(AgentStatusLogEntity::getCreateTime));

        // 计算每个状态的持续时间
        String currentAgent = null;
        LocalDateTime statusStart = null;
        String currentStatus = null;

        for (AgentStatusLogEntity log : logs) {
            if (!log.getAgentUid().equals(currentAgent)) {
                // 新客服，重置计数
                currentAgent = log.getAgentUid();
                statusStart = log.getCreateTime();
                currentStatus = log.getStatus();
                continue;
            }

            // 计算持续时间
            if (statusStart != null && currentStatus != null) {
                long seconds = Duration.between(statusStart, log.getCreateTime()).getSeconds();
                duration.merge(currentStatus, seconds, Long::sum);
            }

            // 更新状态
            statusStart = log.getCreateTime();
            currentStatus = log.getStatus();
        }

        return duration;
    }

    /**
     * 按工作组统计
     */
    @Transactional
    public void calculateWorkgroupStatistic(String workgroupUid, String date, int hour) {
        // TODO: 实现工作组维度的统计
    }

    /**
     * 按客服统计
     */
    @Transactional
    public void calculateAgentStatistic(String agentUid, String date, int hour) {
        // TODO: 实现客服维度的统计
    }

    /**
     * 按机器人统计
     */
    @Transactional
    public void calculateRobotStatistic(String robotUid, String date, int hour) {
        // TODO: 实现机器人维度的统计
    }
}
