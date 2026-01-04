package com.bytedesk.service.workgroup_routing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.core.config.BytedeskEventPublisher;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.presence.PresenceFacadeService;
import com.bytedesk.service.queue.QueueEntity;
import com.bytedesk.service.queue.QueueRestService;
import com.bytedesk.service.workgroup.WorkgroupEntity;
import com.bytedesk.service.workgroup.WorkgroupRepository;
import com.bytedesk.service.workgroup_routing.event.WorkgroupRoutingAdvanceEvent;
import com.bytedesk.service.workgroup_routing.event.WorkgroupRoutingRefreshEvent;
import com.bytedesk.core.thread.ThreadRestService;
import com.bytedesk.core.rbac.user.UserProtobuf;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 工作组路由服务
 * 根据工作组路由模式选择客服
 */
@Slf4j
@Component
@AllArgsConstructor
public class WorkgroupRoutingService {

    private final WorkgroupRoutingRepository workgroupRoutingRepository;

    private final WorkgroupRepository workgroupRepository;

    private final BytedeskEventPublisher bytedeskEventPublisher;

    private final UidUtils uidUtils;

    private final QueueRestService queueRestService;

    private final ThreadRestService threadRestService;

    private final PresenceFacadeService presenceFacadeService;

    /**
     * 根据工作组路由模式选择客服
     */
    @Transactional
    public AgentEntity selectAgent(WorkgroupEntity workgroup, ThreadEntity thread) {
        List<AgentEntity> availableAgents = sortAgentsByUid(presenceFacadeService.getAvailableAgents(workgroup));
        if (availableAgents == null || availableAgents.isEmpty()) {
            return null;
        }

        String routingMode = workgroup.getRoutingMode();
        if (!isEntityBackedMode(routingMode)) {
            // 对于依赖访客上下文的模式（如 CONSISTENT_HASH/RECENT），仍沿用原实时计算逻辑
            return selectAgentRealtimeFallback(workgroup, thread, availableAgents, routingMode);
        }

        // 读 WorkgroupRoutingEntity 中预计算好的 nextAgent；推进/刷新由异步任务处理
        WorkgroupRoutingEntity state = getOrCreateRoutingState(workgroup);
        if (state.getRoutingMode() == null || !Objects.equals(state.getRoutingMode(), routingMode)) {
            // routingMode 变化：不在这里写库，交给异步 refresh 统一重算
            bytedeskEventPublisher.publishEvent(new WorkgroupRoutingRefreshEvent(this, workgroup.getUid(), "mode_changed"));
        }

        AgentEntity selected = findAgentByUid(availableAgents, state.getNextAgentUid());
        if (selected == null) {
            // nextAgent 不存在/不在线：触发异步 refresh，同时本次走一个轻量 fallback，避免阻塞访客路径
            bytedeskEventPublisher.publishEvent(new WorkgroupRoutingRefreshEvent(this, workgroup.getUid(), "next_missing"));
            selected = computeNextAgentForDisplay(routingMode, workgroup, thread, availableAgents, state);
        }

        if (selected != null) {
            // 分配完成后异步推进 cursor 并刷新 nextAgent
            bytedeskEventPublisher.publishEvent(new WorkgroupRoutingAdvanceEvent(this, workgroup.getUid(), selected.getUid()));
        }

        return selected;
    }

    /**
     * 获取工作组路由状态（用于前端可视化，不推进 cursor）。
     */
    @Transactional
    public WorkgroupRoutingStateResponse getRoutingState(WorkgroupEntity workgroup) {
        List<AgentEntity> availableAgents = sortAgentsByUid(presenceFacadeService.getAvailableAgents(workgroup));
        String routingMode = workgroup.getRoutingMode();

        WorkgroupRoutingEntity state = getOrCreateRoutingState(workgroup);

        // 只读展示：不在这里做“计算+落库”，避免前端轮询带来写放大；缺失则触发异步 refresh
        if (state.getRoutingMode() == null || !Objects.equals(state.getRoutingMode(), routingMode)) {
            bytedeskEventPublisher.publishEvent(new WorkgroupRoutingRefreshEvent(this, workgroup.getUid(), "mode_changed"));
        }

        AgentEntity next = findAgentByUid(availableAgents, state.getNextAgentUid());
        if (next == null && isEntityBackedMode(routingMode)) {
            bytedeskEventPublisher.publishEvent(new WorkgroupRoutingRefreshEvent(this, workgroup.getUid(), "next_missing"));
        }

        return WorkgroupRoutingStateResponse.builder()
                .workgroupUid(workgroup.getUid())
                .routingMode(routingMode)
                .nextAgentUid(next == null ? state.getNextAgentUid() : next.getUid())
                .nextAgent(next == null ? null : next.toUserProtobuf())
                .availableAgents(availableAgents == null ? List.of() : availableAgents.stream().map(AgentEntity::toUserProtobuf).toList())
                .build();
    }

    /**
     * 异步预计算：刷新 workgroup 的 nextAgent（不推进 cursor）。
     * 典型触发：presence/接待状态变化、定时任务兜底。
     */
    @Transactional
    public void refreshRoutingStateByWorkgroupUid(String workgroupUid) {
        if (workgroupUid == null) {
            return;
        }
        Optional<WorkgroupEntity> workgroupOpt = workgroupRepository.findByUid(workgroupUid);
        if (workgroupOpt.isEmpty()) {
            return;
        }
        refreshRoutingState(workgroupOpt.get());
    }

    @Transactional
    public void refreshRoutingState(WorkgroupEntity workgroup) {
        if (workgroup == null) {
            return;
        }

        String routingMode = workgroup.getRoutingMode();
        WorkgroupRoutingEntity state = getOrCreateRoutingState(workgroup);

        // routingMode 发生变化时，重置后再刷新 next
        if (state.getRoutingMode() == null || !Objects.equals(state.getRoutingMode(), routingMode)) {
            resetStateForMode(state, workgroup, routingMode);
        }

        // 对不可持久化预计算的模式，清空 next（避免误导）
        if (!isEntityBackedMode(routingMode)) {
            state.setNextAgentUid(null);
            workgroupRoutingRepository.save(state);
            return;
        }

        List<AgentEntity> availableAgents = sortAgentsByUid(presenceFacadeService.getAvailableAgents(workgroup));
        AgentEntity next = computeNextAgentForState(routingMode, workgroup, null, availableAgents, state);
        if (next == null) {
            state.setNextAgentUid(null);
        }
        workgroupRoutingRepository.save(state);
    }

    /**
     * 异步预计算：在一次分配后推进 cursor 并刷新 nextAgent。
     */
    @Transactional
    public void advanceAfterAssignment(String workgroupUid, String assignedAgentUid) {
        if (workgroupUid == null) {
            return;
        }
        Optional<WorkgroupEntity> workgroupOpt = workgroupRepository.findByUid(workgroupUid);
        if (workgroupOpt.isEmpty()) {
            return;
        }
        WorkgroupEntity workgroup = workgroupOpt.get();
        String routingMode = workgroup.getRoutingMode();
        if (!isEntityBackedMode(routingMode)) {
            return;
        }

        WorkgroupRoutingEntity state = getOrCreateRoutingState(workgroup);
        if (state.getRoutingMode() == null || !Objects.equals(state.getRoutingMode(), routingMode)) {
            resetStateForMode(state, workgroup, routingMode);
        }

        List<AgentEntity> availableAgents = sortAgentsByUid(presenceFacadeService.getAvailableAgents(workgroup));
        AgentEntity assigned = findAgentByUid(availableAgents, assignedAgentUid);

        // 复用既有推进逻辑（ROUND_ROBIN 递增 cursor，其他模式重新挑选）
        advanceStateAfterAssignment(routingMode, workgroup, null, availableAgents, state, assigned);
        workgroupRoutingRepository.save(state);
    }

    private boolean isEntityBackedMode(String routingMode) {
        if (routingMode == null) {
            return true;
        }
        return switch (routingMode) {
            case "ROUND_ROBIN", "LEAST_ACTIVE", "RANDOM", "WEIGHTED_RANDOM", "FASTEST_RESPONSE" -> true;
            default -> false;
        };
    }

    private AgentEntity selectAgentRealtimeFallback(WorkgroupEntity workgroup, ThreadEntity thread,
            List<AgentEntity> availableAgents, String routingMode) {
        if (routingMode == null) {
            return selectByRoundRobinInMemory(0L, availableAgents);
        }
        switch (routingMode) {
            case "ROUND_ROBIN":
                return selectByRoundRobinInMemory(0L, availableAgents);
            case "LEAST_ACTIVE":
                return selectByLeastActive(availableAgents);
            case "RANDOM":
                return selectByRandom(availableAgents);
            case "WEIGHTED_RANDOM":
                return selectByWeightedRandom(availableAgents);
            case "CONSISTENT_HASH":
                return selectByConsistentHash(thread.getUserProtobuf().getUid(), availableAgents);
            case "FASTEST_RESPONSE":
                return selectByFastestResponse(availableAgents);
            case "RECENT":
                return selectByRecent(workgroup, thread);
            default:
                return selectByRoundRobinInMemory(0L, availableAgents);
        }
    }

    private WorkgroupRoutingEntity getOrCreateRoutingState(WorkgroupEntity workgroup) {
        return workgroupRoutingRepository
                .findByWorkgroupUidAndDeletedFalse(workgroup.getUid())
                .orElseGet(() -> {
                    WorkgroupRoutingEntity created = WorkgroupRoutingEntity.builder()
                            .uid(uidUtils.getUid())
                            .orgUid(workgroup.getOrgUid())
                            .userUid(workgroup.getUserUid())
                            .name("routing_state_" + workgroup.getUid())
                            .workgroupUid(workgroup.getUid())
                            .cursor(0L)
                            .build();
                    return workgroupRoutingRepository.save(created);
                });
    }

    private void resetStateForMode(WorkgroupRoutingEntity state, WorkgroupEntity workgroup, String routingMode) {
        state.setWorkgroupUid(workgroup.getUid());
        state.setOrgUid(workgroup.getOrgUid());
        state.setRoutingMode(routingMode);
        state.setCursor(0L);
        state.setNextAgentUid(null);
    }

    private AgentEntity findAgentByUid(List<AgentEntity> agents, String agentUid) {
        if (agents == null || agents.isEmpty() || agentUid == null) {
            return null;
        }
        for (AgentEntity agent : agents) {
            if (agent != null && agentUid.equals(agent.getUid())) {
                return agent;
            }
        }
        return null;
    }

    private AgentEntity computeNextAgentForState(String routingMode, WorkgroupEntity workgroup, ThreadEntity thread,
            List<AgentEntity> availableAgents, WorkgroupRoutingEntity state) {
        AgentEntity selected = computeNextAgentForDisplay(routingMode, workgroup, thread, availableAgents, state);
        if (selected != null) {
            state.setNextAgentUid(selected.getUid());
        }
        return selected;
    }

    private AgentEntity computeNextAgentForDisplay(String routingMode, WorkgroupEntity workgroup, ThreadEntity thread,
            List<AgentEntity> availableAgents, WorkgroupRoutingEntity state) {
        if (availableAgents == null || availableAgents.isEmpty()) {
            return null;
        }
        if (routingMode == null) {
            return selectByRoundRobinInMemory(state.getCursor() == null ? 0L : state.getCursor(), availableAgents);
        }
        return switch (routingMode) {
            case "ROUND_ROBIN" -> selectByRoundRobinInMemory(state.getCursor() == null ? 0L : state.getCursor(), availableAgents);
            case "LEAST_ACTIVE" -> selectByLeastActive(availableAgents);
            case "RANDOM" -> selectByRandom(availableAgents);
            case "WEIGHTED_RANDOM" -> selectByWeightedRandom(availableAgents);
            case "FASTEST_RESPONSE" -> selectByFastestResponse(availableAgents);
            default -> selectAgentRealtimeFallback(workgroup, thread, availableAgents, routingMode);
        };
    }

    private void advanceStateAfterAssignment(String routingMode, WorkgroupEntity workgroup, ThreadEntity thread,
            List<AgentEntity> availableAgents, WorkgroupRoutingEntity state, AgentEntity assignedAgent) {
        if (availableAgents == null || availableAgents.isEmpty()) {
            state.setNextAgentUid(null);
            return;
        }
        state.setRoutingMode(routingMode);
        state.setWorkgroupUid(workgroup.getUid());
        state.setOrgUid(workgroup.getOrgUid());

        // 对不可持久化预计算的模式，不推进（保持 nextAgentUid 为空）
        if (!isEntityBackedMode(routingMode)) {
            state.setNextAgentUid(null);
            return;
        }

        switch (routingMode) {
            case "ROUND_ROBIN": {
                Long cursor = state.getCursor() == null ? 0L : state.getCursor();
                // 当前 assignedAgent 理论上等于 cursor%size 对应的 agent；这里直接推进到下一个
                cursor = cursor + 1;
                state.setCursor(cursor);
                AgentEntity next = selectByRoundRobinInMemory(cursor, availableAgents);
                state.setNextAgentUid(next == null ? null : next.getUid());
                break;
            }
            case "LEAST_ACTIVE": {
                AgentEntity next = selectByLeastActive(availableAgents);
                state.setNextAgentUid(next == null ? null : next.getUid());
                break;
            }
            case "RANDOM": {
                AgentEntity next = selectByRandom(availableAgents);
                state.setNextAgentUid(next == null ? null : next.getUid());
                break;
            }
            case "WEIGHTED_RANDOM": {
                AgentEntity next = selectByWeightedRandom(availableAgents);
                state.setNextAgentUid(next == null ? null : next.getUid());
                break;
            }
            case "FASTEST_RESPONSE": {
                AgentEntity next = selectByFastestResponse(availableAgents);
                state.setNextAgentUid(next == null ? null : next.getUid());
                break;
            }
            default: {
                AgentEntity next = computeNextAgentForDisplay(routingMode, workgroup, thread, availableAgents, state);
                state.setNextAgentUid(next == null ? null : next.getUid());
                break;
            }
        }

        // 若 next 与当前 assigned 一致（例如只有 1 个客服），保持即可
        if (assignedAgent != null && Objects.equals(state.getNextAgentUid(), assignedAgent.getUid()) && availableAgents.size() > 1) {
            // 这里不额外处理，保持可预测即可
        }
    }

    /**
     * 轮询算法
     * 按顺序将请求分配给每个客服
     */
    private AgentEntity selectByRoundRobinInMemory(Long cursor, List<AgentEntity> agents) {
        if (agents == null || agents.isEmpty()) {
            return null;
        }
        long safeCursor = cursor == null ? 0L : cursor;
        int index = Math.toIntExact(Math.floorMod(safeCursor, agents.size()));
        return agents.get(index);
    }

    private List<AgentEntity> sortAgentsByUid(List<AgentEntity> agents) {
        if (agents == null || agents.isEmpty()) {
            return agents;
        }
        List<AgentEntity> copied = new ArrayList<>(agents);
        copied.sort(Comparator.comparing(a -> a == null || a.getUid() == null ? "" : a.getUid()));
        return copied;
    }

    /**
     * 最小活动数算法
     * 选择当前会话数最少的客服
     */
    private AgentEntity selectByLeastActive(List<AgentEntity> agents) {
        String today = BdDateUtils.getCurrentDay();

        return agents.stream()
                .filter(agent -> {
                    String queueTopic = TopicUtils.getQueueTopicFromUid(agent.getUid());
                    Optional<QueueEntity> queueEntity = queueRestService.findByTopicAndDay(queueTopic, today);
                    return queueEntity.map(queue -> queue.getQueuingCount() < agent.getMaxThreadCount())
                            .orElse(true); // 如果没有队列信息，默认可以接受更多会话
                })
                .min((a1, a2) -> {
                    String queueTopic1 = TopicUtils.getQueueTopicFromUid(a1.getUid());
                    String queueTopic2 = TopicUtils.getQueueTopicFromUid(a2.getUid());

                    Optional<QueueEntity> queueEntity1 = queueRestService.findByTopicAndDay(queueTopic1, today);
                    Optional<QueueEntity> queueEntity2 = queueRestService.findByTopicAndDay(queueTopic2, today);

                    int count1 = queueEntity1.map(QueueEntity::getChattingCount).orElse(0);
                    int count2 = queueEntity2.map(QueueEntity::getChattingCount).orElse(0);

                    return Integer.compare(count1, count2);
                })
                .orElse(null);
    }

    /**
     * 随机算法
     * 随机选择一个可用客服
     */
    private AgentEntity selectByRandom(List<AgentEntity> agents) {
        if (agents.isEmpty())
            return null;
        int randomIndex = (int) (Math.random() * agents.size());
        return agents.get(randomIndex);
    }

    /**
     * 加权随机算法
     * 根据客服评分和性能给予不同权重
     */
    private AgentEntity selectByWeightedRandom(List<AgentEntity> agents) {
        double totalWeight = agents.stream()
                .mapToDouble(this::calculateWeight)
                .sum();

        double random = Math.random() * totalWeight;
        double weightSum = 0;

        for (AgentEntity agent : agents) {
            weightSum += calculateWeight(agent);
            if (weightSum >= random) {
                return agent;
            }
        }

        return agents.get(agents.size() - 1);
    }

    /**
     * 一致性哈希算法
     * 相同访客尽量分配给同一个客服
     */
    private AgentEntity selectByConsistentHash(String visitorUid, List<AgentEntity> agents) {
        if (agents.isEmpty())
            return null;

        // 使用访客ID的哈希值
        int hash = Math.abs(visitorUid.hashCode());
        return agents.get(hash % agents.size());
    }

    /**
     * 最快响应算法
     * 选择平均响应时间最短的客服
     */
    private AgentEntity selectByFastestResponse(List<AgentEntity> agents) {
        String today = BdDateUtils.getCurrentDay();

        return agents.stream()
                .filter(agent -> {
                    String queueTopic = TopicUtils.getQueueTopicFromUid(agent.getUid());
                    Optional<QueueEntity> queueEntity = queueRestService.findByTopicAndDay(queueTopic, today);
                    return queueEntity.map(queue -> queue.getQueuingCount() < agent.getMaxThreadCount())
                            .orElse(true); // 如果没有队列信息，默认可以接受更多会话
                })
                .min((a1, a2) -> Double.compare(
                        getAverageResponseTime(a1),
                        getAverageResponseTime(a2)))
                .orElse(null);
    }

    /**
     * 计算客服权重
     */
    private double calculateWeight(AgentEntity agent) {
        String today = BdDateUtils.getCurrentDay();
        double weight = 1.0;

        // 1. 评分权重
        // double rating = agent.getAverageRating();
        // weight *= (rating / 5.0);

        // 2. 响应时间权重
        double avgResponseTime = getAverageResponseTime(agent);
        weight *= (1.0 / (avgResponseTime + 1));

        // 3. 工作负载权重
        String queueTopic = TopicUtils.getQueueTopicFromUid(agent.getUid());
        Optional<QueueEntity> queueEntity = queueRestService.findByTopicAndDay(queueTopic, today);
        int currentLoad = queueEntity.map(QueueEntity::getChattingCount).orElse(0);
        weight *= (1.0 / (currentLoad + 1));

        return weight;
    }

    /**
     * TODO: 获取平均响应时间(秒)
     */
    private double getAverageResponseTime(AgentEntity agent) {
        return 30.0; // 默认30秒
    }

    /**
     * 计算会话优先级
     */
    public int calculatePriority(ThreadEntity thread) {
        int priority = 0;

        // 1. VIP访客优先级
        // if(thread.getVisitor().isVip()) {
        // priority += 100;
        // }

        // 2. 老客户优先级
        // if(thread.getVisitor().getVisitCount() > 10) {
        // priority += 50;
        // }

        // 3. 紧急程度
        // if(thread.isUrgent()) {
        // priority += 30;
        // }

        return priority;
    }

    /**
     * 选择最近一次会话ThreadEntity接待的客服。首先选择最近一次接待的客服 thread.agent
     */
    private AgentEntity selectByRecent(WorkgroupEntity workgroup, ThreadEntity thread) {
        // 获取访客ID
        String visitorUid = thread.getUserProtobuf().getUid();
        if (visitorUid == null || presenceFacadeService.getAvailableAgents(workgroup).isEmpty()) {
            return null;
        }

        // 查找访客最近的客服会话记录
        List<ThreadEntity> recentThreads = threadRestService.findRecentAgentThreadsByVisitorUid(visitorUid);

        // 遍历最近的会话记录，查找第一个有客服信息的会话
        for (ThreadEntity recentThread : recentThreads) {
            if (recentThread.getAgent() != null && !recentThread.getAgent().isEmpty()) {
                try {
                    // 解析客服信息
                    UserProtobuf agentProtobuf = UserProtobuf.fromJson(recentThread.getAgent());
                    if (agentProtobuf != null && agentProtobuf.getUid() != null) {
                        // 检查该客服是否在当前可用客服列表中
                        Optional<AgentEntity> recentAgent = presenceFacadeService.getAvailableAgents(workgroup).stream()
                                .filter(agent -> agent.getUid().equals(agentProtobuf.getUid()))
                                .findFirst();

                        if (recentAgent.isPresent()) {
                            // 检查客服是否在线且可以接受新会话
                            AgentEntity agent = recentAgent.get();
                            if (presenceFacadeService.isAgentOnlineAndAvailable(agent)) {
                                String today = BdDateUtils.getCurrentDay();
                                String queueTopic = TopicUtils.getQueueTopicFromUid(agent.getUid());
                                Optional<QueueEntity> queueEntity = queueRestService.findByTopicAndDay(queueTopic,
                                        today);

                                // 检查客服当前会话数是否未超过最大限制
                                if (queueEntity.map(queue -> queue.getChattingCount() < agent.getMaxThreadCount())
                                        .orElse(true)) {
                                    log.info("选择最近一次会话的客服: {}", agent.getUid());
                                    return agent;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("解析最近会话的客服信息失败: {}", e.getMessage());
                    continue;
                }
            }
        }

        // 如果没有找到合适的最近客服，或者最近客服不在线，则使用轮询算法
        log.info("未找到合适的最近客服，使用轮询算法");
        return selectByRoundRobinInMemory(0L, presenceFacadeService.getAvailableAgents(workgroup));
    }
}