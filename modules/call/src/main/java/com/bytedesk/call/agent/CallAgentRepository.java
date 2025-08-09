package com.bytedesk.call.agent;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Call坐席数据访问接口
 */
@Repository
public interface CallAgentRepository extends JpaRepository<CallAgentEntity, Long> {
    
    /**
     * 根据坐席ID查找坐席
     */
    Optional<CallAgentEntity> findByAgentId(String agentId);
    
    /**
     * 根据坐席状态查找坐席列表
     */
    List<CallAgentEntity> findByStatus(CallAgentEntity.AgentStatus status);
    
    /**
     * 根据坐席模式查找坐席列表
     */
    List<CallAgentEntity> findByMode(CallAgentEntity.AgentMode mode);
    
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
    List<CallAgentEntity> findByStatusAndMode(
        CallAgentEntity.AgentStatus status, 
        CallAgentEntity.AgentMode mode);
} 