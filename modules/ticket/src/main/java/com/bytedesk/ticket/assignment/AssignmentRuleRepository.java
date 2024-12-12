package com.bytedesk.ticket.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface AssignmentRuleRepository extends JpaRepository<AssignmentRuleEntity, Long>, 
    JpaSpecificationExecutor<AssignmentRuleEntity> {
    
    boolean existsByName(String name);
    
    List<AssignmentRuleEntity> findByEnabledTrueOrderByWeightDesc();
    
    List<AssignmentRuleEntity> findByCategoryIdAndEnabledTrue(Long categoryId);
    
    List<AssignmentRuleEntity> findByPriorityLevelAndEnabledTrue(String priorityLevel);
    
    @Query("SELECT r FROM AssignmentRuleEntity r WHERE r.enabled = true AND " +
           "(r.categoryId = ?1 OR r.categoryId IS NULL) AND " +
           "(r.priorityLevel = ?2 OR r.priorityLevel IS NULL)")
    List<AssignmentRuleEntity> findMatchingRules(Long categoryId, String priorityLevel);
    
    boolean existsByNameAndIdNot(String name, Long id);
} 