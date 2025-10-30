/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Agent settings repository
 */
package com.bytedesk.service.agent_settings;

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
 * Repository for AgentSettingsEntity
 */
@Repository
public interface AgentSettingsRepository extends JpaRepository<AgentSettingsEntity, Long>, JpaSpecificationExecutor<AgentSettingsEntity> {

    Optional<AgentSettingsEntity> findByUid(String uid);

    List<AgentSettingsEntity> findByOrgUid(String orgUid);

    Optional<AgentSettingsEntity> findByOrgUidAndIsDefaultTrue(String orgUid);

    /**
     * 使用悲观锁读取默认配置，防止并发下重复创建/更新导致的冲突
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AgentSettingsEntity a where a.orgUid = :orgUid and a.isDefault = true")
    Optional<AgentSettingsEntity> findDefaultForUpdate(@Param("orgUid") String orgUid);

    List<AgentSettingsEntity> findByOrgUidAndEnabledTrue(String orgUid);

    Boolean existsByNameAndOrgUid(String name, String orgUid);
}
