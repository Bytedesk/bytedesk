package com.bytedesk.call.call_settings;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CallSettingsRepository extends JpaRepository<CallSettingsEntity, Long> {

    List<CallSettingsEntity> findAllByTargetInAndEnabledTrueAndDeletedFalse(Collection<String> targets);
}