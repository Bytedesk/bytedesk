package com.bytedesk.service.leave_msg.quality;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface QualityRuleRepository extends JpaRepository<QualityRuleEntity, Long> {

    List<QualityRuleEntity> findByEnabledTrue();
    
    List<QualityRuleEntity> findByCategoryUidAndEnabledTrue(String categoryUid);
    
    @Query("SELECT r FROM QualityRuleEntity r WHERE " +
           "(r.categoryUid = ?1 OR r.categoryUid IS NULL) AND r.enabled = true")
    List<QualityRuleEntity> findMatchingRules(String categoryUid);
    
    boolean existsByNameAndIdNot(String name, Long id);
} 