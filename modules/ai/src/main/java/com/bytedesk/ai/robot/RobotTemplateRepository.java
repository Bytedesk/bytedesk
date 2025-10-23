/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Repository for RobotTemplateEntity
 */
package com.bytedesk.ai.robot;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for robot configuration templates
 */
@Repository
public interface RobotTemplateRepository extends JpaRepository<RobotTemplateEntity, Long> {

    /**
     * Find template by UID
     */
    Optional<RobotTemplateEntity> findByUid(String uid);

    /**
     * Find all templates by organization
     */
    List<RobotTemplateEntity> findByOrgUid(String orgUid);

    /**
     * Find default template by organization
     */
    Optional<RobotTemplateEntity> findByOrgUidAndIsDefaultTrue(String orgUid);

    /**
     * Find enabled templates by organization
     */
    List<RobotTemplateEntity> findByOrgUidAndEnabledTrue(String orgUid);

    /**
     * Check if template exists by name and organization
     */
    boolean existsByNameAndOrgUid(String name, String orgUid);
}
