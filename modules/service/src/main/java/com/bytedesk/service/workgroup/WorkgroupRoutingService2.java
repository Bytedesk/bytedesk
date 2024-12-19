package com.bytedesk.service.workgroup;

// import java.util.Comparator;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import com.bytedesk.core.thread.ThreadEntity;
// import com.bytedesk.core.thread.ThreadRepository;
// import com.bytedesk.service.agent.AgentEntity;
// import com.bytedesk.service.agent.AgentRepository;
// import com.bytedesk.service.agent.AgentService;
// import com.bytedesk.service.queue.QueueService;

// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Service
// public class WorkgroupRoutingService2 {

//     @Autowired
//     private ThreadRepository threadRepository;

//     @Autowired
//     private AgentRepository agentRepository;

//     @Autowired
//     private AgentService agentService;

//     @Autowired
//     private QueueService queueService;

//     /**
//      * 处理新会话分配
//      */
//     @Transactional
//     public void handleNewThread(ThreadEntity thread) {
//         try {
//             // 1. 获取可用客服
//             List<AgentEntity> availableAgents = agentService.getAvailableAgents();
            
//             if(availableAgents.isEmpty()) {
//                 // 无可用客服,加入排队队列
//                 int priority = calculatePriority(thread);
//                 queueService.enqueue(thread.getUid(), priority);
//                 // thread.setStatus("queued");
//                 threadRepository.save(thread);
//                 log.info("No available agents, thread {} added to queue", thread.getUid());
//                 return;
//             }
            
//             // 2. 根据分配策略选择客服
//             AgentEntity selectedAgent = selectAgent(availableAgents, thread);
            
//             // 3. 分配会话
//             assignToAgent(thread, selectedAgent);
            
//         } catch(Exception e) {
//             log.error("Failed to handle new thread", e);
//             // 异常情况加入队列
//             queueService.enqueue(thread.getUid(), 0);
//             // thread.setStatus("queued");
//             threadRepository.save(thread);
//         }
//     }

//     /**
//      * 计算会话优先级
//      */
//     private int calculatePriority(ThreadEntity thread) {
//         int priority = 0;
        
//         // 1. VIP访客优先级
//         // if(thread.getVisitor().isVip()) {
//         //     priority += 100;
//         // }
        
//         // 2. 老客户优先级
//         // if(thread.getVisitor().getVisitCount() > 10) {
//         //     priority += 50;
//         // }
        
//         // 3. 紧急程度
//         // if(thread.isUrgent()) {
//         //     priority += 30;
//         // }
        
//         return priority;
//     }

//     // 从可用客服中选择最合适的客服
//     public AgentEntity selectAgent(List<AgentEntity> availableAgents, ThreadEntity thread) {
//         // 获取会话所需技能
//         List<String> requiredSkills = thread.getRequiredSkillList();
        
//         // 过滤出具备所需技能的客服
//         List<AgentEntity> qualifiedAgents = availableAgents.stream()
//             .filter(agent -> agent.hasRequiredSkills(requiredSkills))
//             .collect(Collectors.toList());
            
//         if (qualifiedAgents.isEmpty()) {
//             // 如果没有完全匹配的客服,可以选择部分匹配的
//             qualifiedAgents = findPartiallyMatchedAgents(availableAgents, requiredSkills);
//         }
        
//         // 在具备技能的客服中选择工作负载最小的
//         return qualifiedAgents.stream()
//             .filter(AgentEntity::canAcceptMore)
//             .min(Comparator.comparingInt(AgentEntity::getCurrentThreadCount))
//             .orElseThrow(() -> new RuntimeException("No qualified agent available"));
//     }
    
//     // 查找部分技能匹配的客服
//     private List<AgentEntity> findPartiallyMatchedAgents(
//             List<AgentEntity> agents, List<String> requiredSkills) {
        
//         // 计算每个客服的技能匹配度
//         Map<AgentEntity, Integer> matchScores = new HashMap<>();
//         for (AgentEntity agent : agents) {
//             int score = calculateSkillMatchScore(agent, requiredSkills);
//             matchScores.put(agent, score);
//         }
        
//         // 选择匹配度最高的客服
//         int maxScore = matchScores.values().stream()
//             .mapToInt(Integer::intValue)
//             .max()
//             .orElse(0);
            
//         return agents.stream()
//             .filter(agent -> matchScores.get(agent) == maxScore)
//             .collect(Collectors.toList());
//     }
    
//     // 计算技能匹配分数
//     private int calculateSkillMatchScore(AgentEntity agent, List<String> requiredSkills) {
//         return (int) requiredSkills.stream()
//             .filter(agent::hasSkill)
//             .count();
//     }

//     // 将会话分配给指定客服
//     public AgentEntity assignThread(ThreadEntity thread) {
//         // 1. 先按工作组筛选
//         // WorkgroupEntity workgroup = thread.getWorkgroup();
//         // List<AgentEntity> workgroupAgents = workgroup.getAvailableAgents();
        
//         // 2. 再按技能筛选
//         // List<String> requiredSkills = thread.getRequiredSkills();
//         // List<AgentEntity> qualifiedAgents = workgroupAgents.stream()
//         //     .filter(a -> a.hasRequiredSkills(requiredSkills))
//         //     .collect(Collectors.toList());
            
//         // 3. 最后按工作组路由规则选择
//         // return workgroup.getRoutingStrategy().select(qualifiedAgents);

//         return null;
//     }

    
//     @Transactional
//     public void assignToAgent(ThreadEntity thread, AgentEntity agent) {
//         // 如果在队列中,先移出队列
//         // if("queued".equals(thread.getStatus())) {
//         //     queueService.dequeue(thread.getUid(), QueueStatusEnum.COMPLETED);
//         // }

//         // 更新会话信息
//         // thread.setAgentUid(agent.getUid());
//         // thread.setAssignTime(LocalDateTime.now());
//         // thread.setStatus("assigned");
//         threadRepository.save(thread);

//         // 更新客服工作负载
//         agent.increaseThreadCount();
//         agentRepository.save(agent);
        
//         log.info("Thread {} assigned to agent {}", thread.getUid(), agent.getUid());
//     }

//     // 取消会话分配
//     @Transactional
//     public void unassignThread(ThreadEntity thread) {
//         String currentAgentUid = "";// thread.getAgentUid();
//         if (currentAgentUid != null) {
//             // 更新客服工作负载
//             AgentEntity agent = agentRepository.findByUid(currentAgentUid)
//                 .orElseThrow(() -> new RuntimeException("Agent not found"));
//             agent.decreaseThreadCount();
//             agentRepository.save(agent);
//         }

//         // 更新会话信息
//         // thread.setAgentUid(null);
//         // thread.setStatus("unassigned");
//         threadRepository.save(thread);

//         log.info("Thread {} unassigned from agent {}", thread.getUid(), currentAgentUid);
//     }

//     // 重新分配会话
//     @Transactional
//     public boolean reassignThread(ThreadEntity thread) {
//         try {
//             // 获取可用客服
//             List<AgentEntity> availableAgents = agentService.getAvailableAgents();
//             if (availableAgents.isEmpty()) {
//                 // 无可用客服,加入排队队列
//                 queueService.enqueue(thread.getUid(), calculatePriority(thread));
//                 // thread.setStatus("queued");
//                 threadRepository.save(thread);
//                 return false;
//             }

//             // 取消当前分配
//             unassignThread(thread);

//             // 选择新客服并分配
//             AgentEntity newAgent = selectAgent(availableAgents, thread);
//             assignToAgent(thread, newAgent);

//             return true;
//         } catch (Exception e) {
//             log.error("Failed to reassign thread {}", thread.getUid(), e);
//             // 异常情况加入队列
//             queueService.enqueue(thread.getUid(), 0);
//             // thread.setStatus("queued");
//             threadRepository.save(thread);
//             return false;
//         }
//     }
// } 