/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Workgroup settings repository
 */
package com.bytedesk.service.workgroup_settings;

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
 * Repository for WorkgroupSettingsEntity
 */
@Repository
public interface WorkgroupSettingsRepository extends JpaRepository<WorkgroupSettingsEntity, Long>, JpaSpecificationExecutor<WorkgroupSettingsEntity> {

    Optional<WorkgroupSettingsEntity> findByUid(String uid);

    List<WorkgroupSettingsEntity> findByOrgUid(String orgUid);

    Optional<WorkgroupSettingsEntity> findByOrgUidAndIsDefaultTrue(String orgUid);

    List<WorkgroupSettingsEntity> findByOrgUidAndEnabledTrue(String orgUid);

    Boolean existsByNameAndOrgUid(String name, String orgUid);

    /**
     * 使用悲观锁读取默认配置，防止并发下出现多个默认
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from WorkgroupSettingsEntity w where w.orgUid = :orgUid and w.isDefault = true")
    Optional<WorkgroupSettingsEntity> findDefaultForUpdate(@Param("orgUid") String orgUid);
}
