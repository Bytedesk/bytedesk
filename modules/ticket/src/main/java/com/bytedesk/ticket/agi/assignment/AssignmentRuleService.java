package com.bytedesk.ticket.agi.assignment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bytedesk.ticket.agi.assignment.dto.AssignmentRuleRequest;

import java.util.List;

public interface AssignmentRuleService {
    
    /**
     * 搜索分配规则
     */
    Page<AssignmentRuleEntity> searchRules(String keyword, Long categoryId, 
            String priorityLevel, Boolean enabled, Pageable pageable);
    
    /**
     * 获取分配规则
     */
    AssignmentRuleEntity getRule(Long ruleId);
    
    /**
     * 创建分配规则
     */
    AssignmentRuleEntity createRule(AssignmentRuleRequest request);
    
    /**
     * 更新分配规则
     */
    AssignmentRuleEntity updateRule(Long ruleId, AssignmentRuleRequest request);
    
    /**
     * 删除分配规则
     */
    void deleteRule(Long ruleId);
    
    /**
     * 启用规则
     */
    void enableRule(Long ruleId);
    
    /**
     * 禁用规则
     */
    void disableRule(Long ruleId);
    
    /**
     * 重新排序规则
     */
    void reorderRules(List<Long> ruleIds);
} 