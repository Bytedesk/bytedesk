package com.bytedesk.service.worktime_settings;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface WorktimeSettingRepository extends JpaRepository<WorktimeSettingEntity, Long>, JpaSpecificationExecutor<WorktimeSettingEntity> {

    Optional<WorktimeSettingEntity> findByUid(String uid);

    Boolean existsByUid(String uid);
}