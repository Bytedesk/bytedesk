/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Robot settings repository
 */
package com.bytedesk.ai.robot_settings;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for RobotSettingsEntity
 */
@Repository
public interface RobotSettingsRepository extends JpaRepository<RobotSettingsEntity, Long> {

    Optional<RobotSettingsEntity> findByUid(String uid);

    List<RobotSettingsEntity> findByOrgUid(String orgUid);

    Optional<RobotSettingsEntity> findByOrgUidAndIsDefaultTrue(String orgUid);

    List<RobotSettingsEntity> findByOrgUidAndEnabledTrue(String orgUid);

    Boolean existsByNameAndOrgUid(String name, String orgUid);
}
