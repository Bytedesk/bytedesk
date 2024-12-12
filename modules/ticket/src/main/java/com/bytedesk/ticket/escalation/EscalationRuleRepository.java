package com.bytedesk.ticket.escalation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface EscalationRuleRepository extends JpaRepository<EscalationRuleEntity, Long> {
    
    List<EscalationRuleEntity> findByEnabledTrue();
    
    @Query("SELECT r FROM EscalationRuleEntity r WHERE r.enabled = true AND " +
           "(r.categoryId = ?1 OR r.categoryId IS NULL) AND " +
           "(r.priority = ?2 OR r.priority IS NULL)")
    List<EscalationRuleEntity> findMatchingRules(Long categoryId, String priority);
} 