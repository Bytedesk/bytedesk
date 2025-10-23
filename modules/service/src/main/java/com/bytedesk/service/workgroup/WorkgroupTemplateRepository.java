/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Repository for WorkgroupTemplateEntity
 */
package com.bytedesk.service.workgroup;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for workgroup configuration templates
 */
@Repository
public interface WorkgroupTemplateRepository extends JpaRepository<WorkgroupTemplateEntity, Long> {

    /**
     * Find template by UID
     */
    Optional<WorkgroupTemplateEntity> findByUid(String uid);

    /**
     * Find all templates by organization
     */
    List<WorkgroupTemplateEntity> findByOrgUid(String orgUid);

    /**
     * Find default template by organization
     */
    Optional<WorkgroupTemplateEntity> findByOrgUidAndIsDefaultTrue(String orgUid);

    /**
     * Find enabled templates by organization
     */
    List<WorkgroupTemplateEntity> findByOrgUidAndEnabledTrue(String orgUid);

    /**
     * Check if template exists by name and organization
     */
    boolean existsByNameAndOrgUid(String name, String orgUid);
}
