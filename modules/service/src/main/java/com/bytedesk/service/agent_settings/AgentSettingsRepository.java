/*
 * @Author: jackning 270580156@qq.com
 * @Date: 2025-10-23
 * @Description: Agent settings repository
 */
package com.bytedesk.service.agent_settings;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for AgentSettingsEntity
 */
@Repository
public interface AgentSettingsRepository extends JpaRepository<AgentSettingsEntity, Long> {

    Optional<AgentSettingsEntity> findByUid(String uid);

    List<AgentSettingsEntity> findByOrgUid(String orgUid);

    Optional<AgentSettingsEntity> findByOrgUidAndIsDefaultTrue(String orgUid);

    List<AgentSettingsEntity> findByOrgUidAndEnabledTrue(String orgUid);

    Boolean existsByNameAndOrgUid(String name, String orgUid);
}
