package com.bytedesk.ticket.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.criteria.Predicate;

import com.bytedesk.ticket.assignment.dto.AssignmentRuleRequest;
import com.bytedesk.ticket.assignment.exception.RuleNotFoundException;
import com.bytedesk.ticket.assignment.exception.DuplicateRuleNameException;

import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.ArrayList;

@Slf4j
@Service
public class AssignmentRuleServiceImpl implements AssignmentRuleService {

    @Autowired
    private AssignmentRuleRepository ruleRepository;

    @Override
    public Page<AssignmentRuleEntity> searchRules(String keyword, Long categoryId, 
            String priorityLevel, Boolean enabled, Pageable pageable) {
        return ruleRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (keyword != null && !keyword.isEmpty()) {
                String pattern = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
                ));
            }
            
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("categoryId"), categoryId));
            }
            
            if (priorityLevel != null) {
                predicates.add(cb.equal(root.get("priorityLevel"), priorityLevel));
            }
            
            if (enabled != null) {
                predicates.add(cb.equal(root.get("enabled"), enabled));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Override
    public AssignmentRuleEntity getRule(Long ruleId) {
        return ruleRepository.findById(ruleId)
            .orElseThrow(() -> new RuleNotFoundException(ruleId));
    }

    @Override
    @Transactional
    public AssignmentRuleEntity createRule(AssignmentRuleRequest request) {
        // 检查名称是否重复
        if (ruleRepository.existsByName(request.getName())) {
            throw new DuplicateRuleNameException(request.getName());
        }
        
        AssignmentRuleEntity rule = new AssignmentRuleEntity();
        updateRuleFromRequest(rule, request);
        
        return ruleRepository.save(rule);
    }

    @Override
    @Transactional
    public AssignmentRuleEntity updateRule(Long ruleId, AssignmentRuleRequest request) {
        AssignmentRuleEntity rule = getRule(ruleId);
        
        // 检查名称是否重复
        if (!rule.getName().equals(request.getName()) && 
            ruleRepository.existsByName(request.getName())) {
            throw new DuplicateRuleNameException(request.getName());
        }
        
        updateRuleFromRequest(rule, request);
        return ruleRepository.save(rule);
    }

    @Override
    @Transactional
    public void deleteRule(Long ruleId) {
        if (!ruleRepository.existsById(ruleId)) {
            throw new RuleNotFoundException(ruleId);
        }
        ruleRepository.deleteById(ruleId);
    }

    @Override
    @Transactional
    public void enableRule(Long ruleId) {
        AssignmentRuleEntity rule = getRule(ruleId);
        rule.setEnabled(true);
        ruleRepository.save(rule);
    }

    @Override
    @Transactional
    public void disableRule(Long ruleId) {
        AssignmentRuleEntity rule = getRule(ruleId);
        rule.setEnabled(false);
        ruleRepository.save(rule);
    }

    @Override
    @Transactional
    public void reorderRules(List<Long> ruleIds) {
        double weight = 1.0;
        double step = 1.0 / (ruleIds.size() + 1);
        
        for (Long ruleId : ruleIds) {
            AssignmentRuleEntity rule = getRule(ruleId);
            rule.setWeight(weight);
            ruleRepository.save(rule);
            weight -= step;
        }
    }

    private void updateRuleFromRequest(AssignmentRuleEntity rule, AssignmentRuleRequest request) {
        rule.setName(request.getName());
        rule.setDescription(request.getDescription());
        rule.setCategoryId(request.getCategoryId());
        rule.setPriorityLevel(request.getPriorityLevel());
        rule.setRequiredSkills(String.join(",", request.getRequiredSkills()));
        rule.setMaxActiveTickets(request.getMaxActiveTickets());
        rule.setConsiderWorkload(request.getConsiderWorkload());
        rule.setConsiderOnlineStatus(request.getConsiderOnlineStatus());
        rule.setWeight(request.getWeight());
        rule.setEnabled(request.getEnabled());
    }
} 