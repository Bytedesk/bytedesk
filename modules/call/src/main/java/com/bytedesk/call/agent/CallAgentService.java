package com.bytedesk.call.agent;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bytedesk.core.utils.BdDateUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Call坐席服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CallAgentService {
    
    private final CallAgentRepository agentRepository;
    
    /**
     * 创建坐席
     */
    @Transactional
    public CallAgentEntity createAgent(CallAgentEntity agent) {
        if (agentRepository.existsByAgentId(agent.getAgentId())) {
            throw new RuntimeException("坐席ID已存在: " + agent.getAgentId());
        }
        
        agent.setStatus(CallAgentEntity.AgentStatus.OFFLINE.name());
        agent.setMode(CallAgentEntity.AgentMode.MANUAL.name());
        agent.setCreatedAt(BdDateUtils.now());
        agent.setUpdatedAt(BdDateUtils.now());
        
        return agentRepository.save(agent);
    }
    
    /**
     * 更新坐席信息
     */
    @Transactional
    public CallAgentEntity updateAgent(CallAgentEntity agent) {
        CallAgentEntity existingAgent = agentRepository.findByAgentId(agent.getAgentId())
            .orElseThrow(() -> new RuntimeException("坐席不存在: " + agent.getAgentId()));
            
        existingAgent.setName(agent.getName());
        existingAgent.setSkills(agent.getSkills());
        existingAgent.setNotes(agent.getNotes());
        existingAgent.setUpdatedAt(BdDateUtils.now());
        
        return agentRepository.save(existingAgent);
    }
    
    /**
     * 删除坐席
     */
    @Transactional
    public void deleteAgent(String agentId) {
        agentRepository.deleteByAgentId(agentId);
    }
    
    /**
     * 获取坐席信息
     */
    public Optional<CallAgentEntity> getAgent(String agentId) {
        return agentRepository.findByAgentId(agentId);
    }
    
    /**
     * 获取所有坐席
     */
    public List<CallAgentEntity> getAllAgents() {
        return agentRepository.findAll();
    }
    
    /**
     * 更新坐席状态
     */
    @Transactional
    public CallAgentEntity updateAgentStatus(String agentId, CallAgentEntity.AgentStatus status) {
        CallAgentEntity agent = agentRepository.findByAgentId(agentId)
            .orElseThrow(() -> new RuntimeException("坐席不存在: " + agentId));
            
        agent.setStatus(status.name());
        agent.setLastStatusChange(BdDateUtils.now());
        agent.setUpdatedAt(BdDateUtils.now());
        
        return agentRepository.save(agent);
    }
    
    /**
     * 更新坐席模式
     */
    @Transactional
    public CallAgentEntity updateAgentMode(String agentId, CallAgentEntity.AgentMode mode) {
        CallAgentEntity agent = agentRepository.findByAgentId(agentId)
            .orElseThrow(() -> new RuntimeException("坐席不存在: " + agentId));
            
        agent.setMode(mode.name());
        agent.setUpdatedAt(BdDateUtils.now());
        
        return agentRepository.save(agent);
    }
    
    /**
     * 更新坐席技能
     */
    @Transactional
    public CallAgentEntity updateAgentSkills(String agentId, String skills) {
        CallAgentEntity agent = agentRepository.findByAgentId(agentId)
            .orElseThrow(() -> new RuntimeException("坐席不存在: " + agentId));
            
        agent.setSkills(skills);
        agent.setUpdatedAt(BdDateUtils.now());
        
        return agentRepository.save(agent);
    }
    
    /**
     * 获取所有就绪状态的坐席
     */
    public List<CallAgentEntity> getReadyAgents() {
        return agentRepository.findByStatusAndMode(
            CallAgentEntity.AgentStatus.READY, 
            CallAgentEntity.AgentMode.AUTO);
    }
    
    /**
     * 检查坐席是否可用
     */
    public boolean isAgentAvailable(String agentId) {
        return agentRepository.findByAgentId(agentId)
            .map(agent -> agent.getStatus().equals(CallAgentEntity.AgentStatus.READY.name()) 
                && agent.getMode().equals(CallAgentEntity.AgentMode.AUTO.name()))
            .orElse(false);
    }
} 