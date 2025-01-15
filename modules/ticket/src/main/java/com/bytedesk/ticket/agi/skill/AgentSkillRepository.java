package com.bytedesk.ticket.agi.skill;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AgentSkillRepository extends JpaRepository<AgentSkillEntity, Long> {
    
    List<AgentSkillEntity> findByAgentId(Long agentId);
    
    List<AgentSkillEntity> findByCategoryId(Long categoryId);
    
    List<AgentSkillEntity> findByAgentIdAndEnabled(Long agentId, Boolean enabled);
    
    @Query("SELECT s FROM AgentSkillEntity s WHERE s.categoryId = ?1 AND s.enabled = true " +
           "ORDER BY s.proficiency DESC")
    List<AgentSkillEntity> findBestAgentsForCategory(Long categoryId);
} 