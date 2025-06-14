package com.bytedesk.freeswitch.acd.agent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * FreeSwitch坐席服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FreeSwitchAgentService {
    
    private final FreeSwitchAgentRepository agentRepository;
    
    /**
     * 创建坐席
     */
    @Transactional
    public FreeSwitchAgentEntity createAgent(FreeSwitchAgentEntity agent) {
        if (agentRepository.existsByAgentId(agent.getAgentId())) {
            throw new RuntimeException("坐席ID已存在: " + agent.getAgentId());
        }
        
        agent.setStatus(FreeSwitchAgentEntity.AgentStatus.OFFLINE);
        agent.setMode(FreeSwitchAgentEntity.AgentMode.MANUAL);
        agent.setCreatedAt(LocalDateTime.now());
        agent.setUpdatedAt(LocalDateTime.now());
        
        return agentRepository.save(agent);
    }
    
    /**
     * 更新坐席信息
     */
    @Transactional
    public FreeSwitchAgentEntity updateAgent(FreeSwitchAgentEntity agent) {
        FreeSwitchAgentEntity existingAgent = agentRepository.findByAgentId(agent.getAgentId())
            .orElseThrow(() -> new RuntimeException("坐席不存在: " + agent.getAgentId()));
            
        existingAgent.setName(agent.getName());
        existingAgent.setSkills(agent.getSkills());
        existingAgent.setNotes(agent.getNotes());
        existingAgent.setUpdatedAt(LocalDateTime.now());
        
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
    public Optional<FreeSwitchAgentEntity> getAgent(String agentId) {
        return agentRepository.findByAgentId(agentId);
    }
    
    /**
     * 获取所有坐席
     */
    public List<FreeSwitchAgentEntity> getAllAgents() {
        return agentRepository.findAll();
    }
    
    /**
     * 更新坐席状态
     */
    @Transactional
    public FreeSwitchAgentEntity updateAgentStatus(String agentId, FreeSwitchAgentEntity.AgentStatus status) {
        FreeSwitchAgentEntity agent = agentRepository.findByAgentId(agentId)
            .orElseThrow(() -> new RuntimeException("坐席不存在: " + agentId));
            
        agent.setStatus(status);
        agent.setLastStatusChange(LocalDateTime.now());
        agent.setUpdatedAt(LocalDateTime.now());
        
        return agentRepository.save(agent);
    }
    
    /**
     * 更新坐席模式
     */
    @Transactional
    public FreeSwitchAgentEntity updateAgentMode(String agentId, FreeSwitchAgentEntity.AgentMode mode) {
        FreeSwitchAgentEntity agent = agentRepository.findByAgentId(agentId)
            .orElseThrow(() -> new RuntimeException("坐席不存在: " + agentId));
            
        agent.setMode(mode);
        agent.setUpdatedAt(LocalDateTime.now());
        
        return agentRepository.save(agent);
    }
    
    /**
     * 更新坐席技能
     */
    @Transactional
    public FreeSwitchAgentEntity updateAgentSkills(String agentId, String skills) {
        FreeSwitchAgentEntity agent = agentRepository.findByAgentId(agentId)
            .orElseThrow(() -> new RuntimeException("坐席不存在: " + agentId));
            
        agent.setSkills(skills);
        agent.setUpdatedAt(LocalDateTime.now());
        
        return agentRepository.save(agent);
    }
    
    /**
     * 获取所有就绪状态的坐席
     */
    public List<FreeSwitchAgentEntity> getReadyAgents() {
        return agentRepository.findByStatusAndMode(
            FreeSwitchAgentEntity.AgentStatus.READY, 
            FreeSwitchAgentEntity.AgentMode.AUTO);
    }
    
    /**
     * 检查坐席是否可用
     */
    public boolean isAgentAvailable(String agentId) {
        return agentRepository.findByAgentId(agentId)
            .map(agent -> agent.getStatus() == FreeSwitchAgentEntity.AgentStatus.READY 
                && agent.getMode() == FreeSwitchAgentEntity.AgentMode.AUTO)
            .orElse(false);
    }
} 