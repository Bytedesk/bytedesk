package com.bytedesk.ticket.agi.assign;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketService;
import com.bytedesk.ticket.agi.event.TicketEventService;
import com.bytedesk.ticket.skill.AgentSkillEntity;
import com.bytedesk.ticket.skill.AgentSkillService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TicketAssignmentServiceImpl implements TicketAssignmentService {

    @Autowired
    private AssignmentConfigRepository configRepository;
    
    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private TicketEventService eventService;

    @Autowired
    private AgentSkillService skillService;

    private Map<Long, Integer> roundRobinCounters = new HashMap<>();

    @Override
    public Long assignTicket(TicketEntity ticket) {
        AssignmentConfigEntity config = getMatchingConfig(ticket.getCategoryId(), ticket.getPriority());
        List<Long> availableAgents = Arrays.stream(config.getAgentIds().split(","))
            .map(Long::valueOf)
            .collect(Collectors.toList());
        
        Long assignedTo = null;
        switch (config.getStrategy()) {
            case ROUND_ROBIN:
                assignedTo = assignByRoundRobin(availableAgents);
                break;
            case LEAST_ACTIVE:
                assignedTo = assignByLeastActive(availableAgents);
                break;
            case LOAD_BALANCED:
                assignedTo = assignByLoadBalanced(availableAgents, config.getMaxActiveTickets());
                break;
            case SKILL_BASED:
                assignedTo = assignBySkill(availableAgents, ticket.getCategoryId());
                break;
            case CATEGORY_BASED:
                assignedTo = assignByCategory(availableAgents, ticket.getCategoryId());
                break;
        }
        
        if (assignedTo != null) {
            ticketService.assignTicket(ticket.getId(), assignedTo);
        }
        
        return assignedTo;
    }

    private Long assignByRoundRobin(List<Long> agents) {
        if (agents.isEmpty()) return null;
        
        Long categoryId = 0L; // 使用0作为默认分类
        int counter = roundRobinCounters.getOrDefault(categoryId, 0);
        Long assignedTo = agents.get(counter % agents.size());
        roundRobinCounters.put(categoryId, counter + 1);
        
        return assignedTo;
    }

    private Long assignByLeastActive(List<Long> agents) {
        if (agents.isEmpty()) return null;
        
        return agents.stream()
            .min(Comparator.comparingInt(agentId -> 
                ticketService.getAssignedTickets(agentId, null).getNumberOfElements()))
            .orElse(null);
    }

    private Long assignByLoadBalanced(List<Long> agents, int maxTickets) {
        if (agents.isEmpty()) return null;
        
        return agents.stream()
            .filter(agentId -> 
                ticketService.getAssignedTickets(agentId, null).getNumberOfElements() < maxTickets)
            .findFirst()
            .orElse(null);
    }

    private Long assignBySkill(List<Long> agents, Long categoryId) {
        // 获取该分类最熟练的客服
        List<AgentSkillEntity> skills = skillService.getBestAgentsForCategory(categoryId);
        
        // 过滤出可用的客服
        List<Long> skilledAgents = skills.stream()
            .filter(skill -> agents.contains(skill.getAgentId()))
            .map(AgentSkillEntity::getAgentId)
            .collect(Collectors.toList());
        
        if (skilledAgents.isEmpty()) {
            // 如果没有熟练客服，使用轮询分配
            return assignByRoundRobin(agents);
        }
        
        // 在熟练客服中找到最合适的客服
        return findBestAgent(skilledAgents, skills);
    }

    private Long findBestAgent(List<Long> agents, List<AgentSkillEntity> skills) {
        // 按熟练度和当前工单数计算得分
        return agents.stream()
            .map(agentId -> {
                AgentSkillEntity skill = skills.stream()
                    .filter(s -> s.getAgentId().equals(agentId))
                    .findFirst()
                    .orElse(null);
                
                if (skill == null) return null;
                
                int currentTickets = ticketService.getAssignedTickets(agentId, null)
                    .getNumberOfElements();
                
                // 如果超过最大工单数，跳过该客服
                if (currentTickets >= skill.getMaxTickets()) {
                    return null;
                }
                
                // 计算得分：熟练度 * 10 - 当前工单数
                int score = skill.getProficiency() * 10 - currentTickets;
                
                return new AbstractMap.SimpleEntry<>(agentId, score);
            })
            .filter(entry -> entry != null)
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    private Long assignByCategory(List<Long> agents, Long categoryId) {
        // 基于分类的分配实际上就是基于技能的分配
        return assignBySkill(agents, categoryId);
    }

    @Override
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次
    @Transactional
    public void redistributeTickets() {
        ticketService.getUnassignedTickets().forEach(ticket -> {
            Long assignedTo = assignTicket(ticket);
            if (assignedTo != null) {
                eventService.recordEvent(ticket.getId(), assignedTo, 
                    "auto_assigned", null, assignedTo.toString(),
                    "Ticket automatically assigned to agent #" + assignedTo);
            }
        });
    }

    @Override
    @Transactional
    public AssignmentConfigEntity createConfig(AssignmentConfigEntity config) {
        return configRepository.save(config);
    }

    @Override
    @Transactional
    public AssignmentConfigEntity updateConfig(Long configId, AssignmentConfigEntity config) {
        AssignmentConfigEntity existingConfig = getConfig(configId);
        existingConfig.setStrategy(config.getStrategy());
        existingConfig.setCategoryId(config.getCategoryId());
        existingConfig.setPriority(config.getPriority());
        existingConfig.setAgentIds(config.getAgentIds());
        existingConfig.setMaxActiveTickets(config.getMaxActiveTickets());
        existingConfig.setEnabled(config.getEnabled());
        return configRepository.save(existingConfig);
    }

    @Override
    @Transactional
    public void deleteConfig(Long configId) {
        configRepository.deleteById(configId);
    }

    @Override
    public AssignmentConfigEntity getConfig(Long configId) {
        return configRepository.findById(configId)
            .orElseThrow(() -> new RuntimeException("Assignment config not found"));
    }

    @Override
    public List<AssignmentConfigEntity> getAllConfigs() {
        return configRepository.findAll();
    }

    @Override
    public AssignmentConfigEntity getMatchingConfig(Long categoryId, String priority) {
        return configRepository.findMatchingConfig(categoryId, priority)
            .orElseThrow(() -> new RuntimeException("No matching assignment config found"));
    }

    // ... 实现其他配置管理方法
} 