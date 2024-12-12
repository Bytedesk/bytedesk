package com.bytedesk.service.thread;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.bytedesk.service.agent.AgentEntity;

@Service 
public class SkillBasedAssignmentService {

    public AgentEntity selectAgent(List<AgentEntity> agents, List<String> requiredSkills) {
        // 1. 按技能匹配度筛选
        List<AgentEntity> qualified = agents.stream()
            .filter(a -> a.hasRequiredSkills(requiredSkills))
            .collect(Collectors.toList());
            
        // 2. 在匹配的客服中选择负载最小的
        return qualified.stream()
            .min(Comparator.comparingInt(AgentEntity::getCurrentThreadCount))
            .orElse(null);
    }
}