package com.bytedesk.freeswitch.agent;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * FreeSwitch坐席数据访问接口
 */
@Repository
public interface FreeSwitchAgentRepository extends JpaRepository<FreeSwitchAgentEntity, Long> {
    
    /**
     * 根据坐席ID查找坐席
     */
    Optional<FreeSwitchAgentEntity> findByAgentId(String agentId);
    
    /**
     * 根据坐席状态查找坐席列表
     */
    List<FreeSwitchAgentEntity> findByStatus(FreeSwitchAgentEntity.AgentStatus status);
    
    /**
     * 根据坐席模式查找坐席列表
     */
    List<FreeSwitchAgentEntity> findByMode(FreeSwitchAgentEntity.AgentMode mode);
    
    /**
     * 检查坐席ID是否存在
     */
    boolean existsByAgentId(String agentId);
    
    /**
     * 根据坐席ID删除坐席
     */
    void deleteByAgentId(String agentId);
    
    /**
     * 查找所有就绪状态的坐席
     */
    List<FreeSwitchAgentEntity> findByStatusAndMode(
        FreeSwitchAgentEntity.AgentStatus status, 
        FreeSwitchAgentEntity.AgentMode mode);
} 