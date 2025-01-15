package com.bytedesk.ticket.agi.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bytedesk.ticket.ticket.TicketEntity;
import com.bytedesk.ticket.ticket.TicketService;
import com.bytedesk.ticket.agi.agent.TicketAgentService;
import com.bytedesk.ticket.agi.notification.TicketNotificationService;

import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AutoAssignmentServiceImpl implements AutoAssignmentService {

    @Autowired
    private AssignmentRuleRepository ruleRepository;
    
    @Autowired
    private TicketService ticketService;
    
    @Autowired
    private TicketAgentService ticketAgentService;
    
    @Autowired
    private TicketNotificationService notificationService;

    @Override
    @Transactional
    public void autoAssignTicket(TicketEntity ticket) {
        try {
            // 获取所有启用的规则
            List<AssignmentRuleEntity> rules = ruleRepository.findByEnabledTrueOrderByWeightDesc();
            
            // 选适用的规则
            rules = filterApplicableRules(ticket, rules);
            
            if (rules.isEmpty()) {
                log.warn("No applicable assignment rules found for ticket {}", ticket.getId());
                return;
            }
            
            // 获取候选客服列表
            List<Long> candidates = getCandidateAgents(rules);
            
            if (candidates.isEmpty()) {
                log.warn("No available agents found for ticket {}", ticket.getId());
                return;
            }
            
            // 根据规则选择最佳客服
            Long selectedAgent = selectBestAgent(candidates, rules, ticket);
            
            if (selectedAgent != null) {
                ticketService.assignTicket(ticket.getId(), selectedAgent);
                notificationService.sendTicketAssignedNotification(selectedAgent, ticket.getId());
                log.info("Auto assigned ticket {} to agent {}", ticket.getId(), selectedAgent);
            }
            
        } catch (Exception e) {
            log.error("Failed to auto assign ticket " + ticket.getId(), e);
        }
    }

    private List<AssignmentRuleEntity> filterApplicableRules(
            TicketEntity ticket, List<AssignmentRuleEntity> rules) {
        return rules.stream()
            .filter(rule -> {
                // 检查分类
                if (rule.getCategoryId() != null && 
                    !rule.getCategoryId().equals(ticket.getCategoryId())) {
                    return false;
                }
                
                // 检查优先级
                if (rule.getPriorityLevel() != null && 
                    !rule.getPriorityLevel().equals(ticket.getPriority())) {
                    return false;
                }
                
                return true;
            })
            .collect(Collectors.toList());
    }

    private List<Long> getCandidateAgents(List<AssignmentRuleEntity> rules) {
        Set<Long> candidates = new HashSet<>();
        
        for (AssignmentRuleEntity rule : rules) {
            List<Long> agents = new ArrayList<>();
            
            if (rule.getRequiredSkills() != null) {
                List<String> skills = Arrays.asList(rule.getRequiredSkills().split(","));
                agents.addAll(ticketAgentService.getAgentsBySkills(skills));
            } else {
                agents.addAll(ticketAgentService.getAvailableAgents());
            }
            
            if (rule.getConsiderOnlineStatus()) {
                agents.retainAll(ticketAgentService.getOnlineAgents());
            }
            
            if (rule.getConsiderWorkload()) {
                // agents = agents.stream()
                //     .filter(agent -> 
                //         ticketService.countActiveTickets(agent) < rule.getMaxActiveTickets())
                //     .collect(Collectors.toList());
            }
            
            candidates.addAll(agents);
        }
        
        return new ArrayList<>(candidates);
    }

    private Long selectBestAgent(List<Long> candidates, List<AssignmentRuleEntity> rules, 
            TicketEntity ticket) {
        // 计算每个客服的综合得分
        Map<Long, Double> scores = new HashMap<>();
        
        for (Long agent : candidates) {
            double score = 0.0;
            
            // 工作量得分（工作量越小分数越高）
            int workload = 0;//ticketService.countActiveTickets(agent);
            score += (10.0 - workload) / 10.0;
            
            // 技能匹配得分
            score += calculateSkillMatchScore(agent, rules);
            
            // 在线状态得分
            if (ticketAgentService.isAgentOnline(agent)) {
                score += 1.0;
            }
            
            // 响应时间得分
            score += calculateResponseTimeScore(agent);
            
            // 满意度得分
            score += calculateSatisfactionScore(agent);
            
            scores.put(agent, score);
        }
        
        // 选择得分最高的客服
        return scores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }

    private double calculateSkillMatchScore(Long agent, List<AssignmentRuleEntity> rules) {
        double score = 0.0;
        Set<String> agentSkills = new HashSet<>(ticketAgentService.getAgentSkills(agent));
        
        for (AssignmentRuleEntity rule : rules) {
            if (rule.getRequiredSkills() != null) {
                List<String> requiredSkills = Arrays.asList(rule.getRequiredSkills().split(","));
                long matchedSkills = requiredSkills.stream()
                    .filter(agentSkills::contains)
                    .count();
                score += (double) matchedSkills / requiredSkills.size() * rule.getWeight();
            }
        }
        
        return score;
    }

    private double calculateResponseTimeScore(Long agent) {
        long avgResponseTime = ticketAgentService.getAverageResponseTime(agent);
        if (avgResponseTime <= 0) {
            return 1.0;
        }
        return Math.min(1.0, 300000.0 / avgResponseTime);
    }

    private double calculateSatisfactionScore(Long agent) {
        Double avgRating = ticketAgentService.getAverageSatisfactionRating(agent);
        if (avgRating == null) {
            return 0.5;
        }
        return avgRating / 5.0;
    }

    @Override
    public Long selectAgentByWorkload() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selectAgentByWorkload'");
    }

    @Override
    public Long selectAgentBySkills(List<String> requiredSkills) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selectAgentBySkills'");
    }

    @Override
    public Long selectOnlineAgent() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selectOnlineAgent'");
    }

    @Override
    public Long selectAgentByPriority(String priority) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'selectAgentByPriority'");
    }
} 