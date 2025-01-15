package com.bytedesk.ticket.agi.assign;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface AssignmentConfigRepository extends JpaRepository<AssignmentConfigEntity, Long> {
    
    @Query("SELECT c FROM AssignmentConfigEntity c WHERE " +
           "c.enabled = true AND " +
           "(c.categoryId = ?1 OR c.categoryId IS NULL) AND " +
           "(c.priority = ?2 OR c.priority IS NULL) " +
           "ORDER BY c.categoryId DESC, c.priority DESC LIMIT 1")
    Optional<AssignmentConfigEntity> findMatchingConfig(Long categoryId, String priority);
} 