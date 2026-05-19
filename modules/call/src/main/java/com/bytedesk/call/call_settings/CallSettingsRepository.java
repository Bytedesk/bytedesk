package com.bytedesk.call.call_settings;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CallSettingsRepository extends JpaRepository<CallSettingsEntity, Long>, JpaSpecificationExecutor<CallSettingsEntity> {

    Optional<CallSettingsEntity> findByUid(String uid);

    Optional<CallSettingsEntity> findByAgentUidAndDeletedFalse(String agentUid);

    List<CallSettingsEntity> findAllByTargetInAndEnabledTrueAndDeletedFalse(Collection<String> targets);
}