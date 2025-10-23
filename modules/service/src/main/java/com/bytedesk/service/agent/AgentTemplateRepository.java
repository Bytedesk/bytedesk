/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Repository for AgentTemplateEntity
 */
package com.bytedesk.service.agent;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for agent configuration templates
 */
@Repository
public interface AgentTemplateRepository extends JpaRepository<AgentTemplateEntity, Long> {

    /**
     * Find template by UID
     */
    Optional<AgentTemplateEntity> findByUid(String uid);

    /**
     * Find all templates by organization
     */
    List<AgentTemplateEntity> findByOrgUid(String orgUid);

    /**
     * Find default template by organization
     */
    Optional<AgentTemplateEntity> findByOrgUidAndIsDefaultTrue(String orgUid);

    /**
     * Find enabled templates by organization
     */
    List<AgentTemplateEntity> findByOrgUidAndEnabledTrue(String orgUid);

    /**
     * Check if template exists by name and organization
     */
    boolean existsByNameAndOrgUid(String name, String orgUid);
}
