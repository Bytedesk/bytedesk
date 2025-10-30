/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Robot settings repository
 */
package com.bytedesk.ai.robot_settings;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;

/**
 * Repository for RobotSettingsEntity
 */
@Repository
public interface RobotSettingsRepository extends JpaRepository<RobotSettingsEntity, Long>, JpaSpecificationExecutor<RobotSettingsEntity> {

    Optional<RobotSettingsEntity> findByUid(String uid);

    List<RobotSettingsEntity> findByOrgUid(String orgUid);

    Optional<RobotSettingsEntity> findByOrgUidAndIsDefaultTrue(String orgUid);

    List<RobotSettingsEntity> findByOrgUidAndEnabledTrue(String orgUid);

    Boolean existsByNameAndOrgUid(String name, String orgUid);

    /**
     * 使用悲观锁读取默认配置，防止并发下出现多个默认
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from RobotSettingsEntity r where r.orgUid = :orgUid and r.isDefault = true")
    Optional<RobotSettingsEntity> findDefaultForUpdate(@Param("orgUid") String orgUid);
}
