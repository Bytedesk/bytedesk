package com.bytedesk.ticket.agi.skill;

import java.util.List;

public interface AgentSkillService {
    
    AgentSkillEntity addSkill(Long agentId, Long categoryId, Integer proficiency);
    
    void removeSkill(Long skillId);
    
    void updateProficiency(Long skillId, Integer proficiency);
    
    void updateMaxTickets(Long skillId, Integer maxTickets);
    
    void enableSkill(Long skillId);
    
    void disableSkill(Long skillId);
    
    List<AgentSkillEntity> getAgentSkills(Long agentId);
    
    List<AgentSkillEntity> getCategorySkills(Long categoryId);
    
    List<AgentSkillEntity> getBestAgentsForCategory(Long categoryId);
} 