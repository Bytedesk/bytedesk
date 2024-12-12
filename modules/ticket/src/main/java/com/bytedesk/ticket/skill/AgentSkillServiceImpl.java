package com.bytedesk.ticket.skill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class AgentSkillServiceImpl implements AgentSkillService {

    @Autowired
    private AgentSkillRepository skillRepository;

    @Override
    @Transactional
    public AgentSkillEntity addSkill(Long agentId, Long categoryId, Integer proficiency) {
        AgentSkillEntity skill = new AgentSkillEntity();
        skill.setAgentId(agentId);
        skill.setCategoryId(categoryId);
        skill.setProficiency(proficiency);
        return skillRepository.save(skill);
    }

    @Override
    @Transactional
    public void removeSkill(Long skillId) {
        skillRepository.deleteById(skillId);
    }

    @Override
    @Transactional
    public void updateProficiency(Long skillId, Integer proficiency) {
        AgentSkillEntity skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new RuntimeException("Skill not found"));
        skill.setProficiency(proficiency);
        skillRepository.save(skill);
    }

    @Override
    @Transactional
    public void updateMaxTickets(Long skillId, Integer maxTickets) {
        AgentSkillEntity skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new RuntimeException("Skill not found"));
        skill.setMaxTickets(maxTickets);
        skillRepository.save(skill);
    }

    @Override
    @Transactional
    public void enableSkill(Long skillId) {
        AgentSkillEntity skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new RuntimeException("Skill not found"));
        skill.setEnabled(true);
        skillRepository.save(skill);
    }

    @Override
    @Transactional
    public void disableSkill(Long skillId) {
        AgentSkillEntity skill = skillRepository.findById(skillId)
            .orElseThrow(() -> new RuntimeException("Skill not found"));
        skill.setEnabled(false);
        skillRepository.save(skill);
    }

    @Override
    public List<AgentSkillEntity> getAgentSkills(Long agentId) {
        return skillRepository.findByAgentId(agentId);
    }

    @Override
    public List<AgentSkillEntity> getCategorySkills(Long categoryId) {
        return skillRepository.findByCategoryId(categoryId);
    }

    @Override
    public List<AgentSkillEntity> getBestAgentsForCategory(Long categoryId) {
        return skillRepository.findBestAgentsForCategory(categoryId);
    }
} 