package com.bytedesk.freeswitch.routing;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.bytedesk.freeswitch.agent.FreeSwitchAgentEntity;
import com.bytedesk.freeswitch.agent.FreeSwitchAgentService;
import com.bytedesk.freeswitch.call.FreeSwitchCallEntity;
import com.bytedesk.freeswitch.queue.FreeSwitchQueueEntity;
import com.bytedesk.freeswitch.queue.FreeSwitchQueueService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeSwitch路由服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FreeSwitchRoutingService {
    
    private final FreeSwitchAgentService agentService;
    private final FreeSwitchQueueService queueService;
    
    /**
     * 为呼叫寻找合适的坐席
     */
    public Optional<FreeSwitchAgentEntity> findAgentForCall(FreeSwitchCallEntity call) {
        // 获取所有就绪状态的坐席
        List<FreeSwitchAgentEntity> readyAgents = agentService.getReadyAgents();
        
        // 根据呼叫类型和技能要求选择合适的坐席
        return readyAgents.stream()
            .filter(agent -> hasRequiredSkills(call, agent))
            .findFirst();
    }
    
    /**
     * 路由呼入呼叫
     */
    public void routeInboundCall(FreeSwitchCallEntity call) {
        // 1. 根据呼叫技能找到合适的队列
        FreeSwitchQueueEntity queue = findQueueForCall(call)
            .orElseThrow(() -> new RuntimeException("未找到合适的队列"));
            
        // 2. 将呼叫加入队列
        queueService.enqueueCall(call, queue.getName());
        
        // 3. 尝试立即分配坐席
        findAgentForCall(call).ifPresent(agent -> {
            // call.setAgentId(agent.getAgentId());
            call.setStatus(FreeSwitchCallEntity.CallStatus.RINGING);
        });
    }
    
    /**
     * 路由呼出呼叫
     */
    public void routeOutboundCall(FreeSwitchCallEntity call) {
        // 1. 根据呼叫技能找到合适的队列
        FreeSwitchQueueEntity queue = findQueueForCall(call)
            .orElseThrow(() -> new RuntimeException("未找到合适的队列"));
            
        // 2. 将呼叫加入队列
        queueService.enqueueCall(call, queue.getName());
        
        // 3. 尝试立即分配坐席
        findAgentForCall(call).ifPresent(agent -> {
            // call.setAgentId(agent.getAgentId());
            call.setStatus(FreeSwitchCallEntity.CallStatus.RINGING);
        });
    }
    
    /**
     * 路由内部呼叫
     */
    public void routeInternalCall(FreeSwitchCallEntity call) {
        // 1. 根据呼叫技能找到合适的队列
        FreeSwitchQueueEntity queue = findQueueForCall(call)
            .orElseThrow(() -> new RuntimeException("未找到合适的队列"));
            
        // 2. 将呼叫加入队列
        queueService.enqueueCall(call, queue.getName());
        
        // 3. 尝试立即分配坐席
        findAgentForCall(call).ifPresent(agent -> {
            // call.setAgentId(agent.getAgentId());
            call.setStatus(FreeSwitchCallEntity.CallStatus.RINGING);
        });
    }
    
    /**
     * 检查坐席是否具备呼叫所需的技能
     */
    private boolean hasRequiredSkills(FreeSwitchCallEntity call, FreeSwitchAgentEntity agent) {
        // TODO: 实现技能匹配逻辑
        return true;
    }
    
    /**
     * 为呼叫找到合适的队列
     */
    private Optional<FreeSwitchQueueEntity> findQueueForCall(FreeSwitchCallEntity call) {
        // 获取所有激活状态的队列
        List<FreeSwitchQueueEntity> activeQueues = queueService.getQueuesByType(
            FreeSwitchQueueEntity.QueueType.valueOf(call.getType().name()));
            
        // 根据呼叫技能要求选择合适的队列
        return activeQueues.stream()
            .filter(queue -> queueService.matchesQueueSkills(call, queue))
            .findFirst();
    }
    
    /**
     * 路由呼叫
     */
    public void routeCall(FreeSwitchCallEntity call) {
        switch (call.getType()) {
            case INBOUND:
                routeInboundCall(call);
                break;
            case OUTBOUND:
                routeOutboundCall(call);
                break;
            case INTERNAL:
                routeInternalCall(call);
                break;
            default:
                throw new RuntimeException("不支持的呼叫类型: " + call.getType());
        }
    }
} 