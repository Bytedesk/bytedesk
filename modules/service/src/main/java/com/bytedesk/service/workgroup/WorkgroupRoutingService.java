package com.bytedesk.service.workgroup;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.bytedesk.core.redis.RedisConsts;
import com.bytedesk.core.thread.ThreadEntity;
import com.bytedesk.core.topic.TopicUtils;
import com.bytedesk.service.agent.AgentEntity;
import com.bytedesk.service.queue.QueueEntity;
import com.bytedesk.service.queue.QueueRestService;

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

    private final StringRedisTemplate redisTemplate;

    private static final String COUNTER_KEY_PREFIX = RedisConsts.BYTEDESK_REDIS_PREFIX + "roundRobinCounter:";

    private final QueueRestService queueRestService;

    // 获取当天日期
    private String getCurrentDay() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    /**
     * 根据工作组路由模式选择客服
     */
    public AgentEntity selectAgent(WorkgroupEntity workgroup, ThreadEntity thread) {
        List<AgentEntity> availableAgents = workgroup.getAvailableAgents();
        // 
        switch (workgroup.getRoutingMode()) {
            case "ROUND_ROBIN":
                return selectByRoundRobin(workgroup.getUid(), availableAgents);
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
            default:
                return selectByRoundRobin(workgroup.getUid(), availableAgents); // 默认使用轮询
        }
    }

    /**
     * 轮询算法
     * 按顺序将请求分配给每个客服
     */
    private AgentEntity selectByRoundRobin(String workgroupUid, List<AgentEntity> agents) {
        // 判断agents.size()是否大于0
        if (agents.isEmpty()) return null;
        // 获取当前计数器值
        String counterKey = COUNTER_KEY_PREFIX + workgroupUid;
        Long counter = redisTemplate.opsForValue().increment(counterKey, 1);
        // 计算索引
        int index = Math.toIntExact(counter % agents.size());
        return agents.get(index);
    }

    /**
     * 最小活动数算法
     * 选择当前会话数最少的客服
     */
    private AgentEntity selectByLeastActive(List<AgentEntity> agents) {
        String today = getCurrentDay();
        
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
        if (agents.isEmpty()) return null;
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
        if (agents.isEmpty()) return null;
        
        // 使用访客ID的哈希值
        int hash = Math.abs(visitorUid.hashCode());
        return agents.get(hash % agents.size());
    }

    /**
     * 最快响应算法
     * 选择平均响应时间最短的客服
     */
    private AgentEntity selectByFastestResponse(List<AgentEntity> agents) {
        String today = getCurrentDay();
        
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
        String today = getCurrentDay();
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
     * 获取平均响应时间(秒)
     */
    private double getAverageResponseTime(AgentEntity agent) {
        // TODO: 从统计数据中获取实际响应时间
        return 30.0; // 默认30秒
    }

    /**
     * 计算会话优先级
     */
    public int calculatePriority(ThreadEntity thread) {
        int priority = 0;
        
        // 1. VIP访客优先级
        // if(thread.getVisitor().isVip()) {
        //     priority += 100;
        // }
        
        // 2. 老客户优先级
        // if(thread.getVisitor().getVisitCount() > 10) {
        //     priority += 50;
        // }
        
        // 3. 紧急程度
        // if(thread.isUrgent()) {
        //     priority += 30;
        // }
        
        return priority;
    }
}