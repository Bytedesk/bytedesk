package com.bytedesk.service.visitor_custom_field_settings;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitorCustomFieldSettingsRepository extends JpaRepository<VisitorCustomFieldSettingsEntity, Long> {

    Optional<VisitorCustomFieldSettingsEntity> findByOrgUidAndDeleted(String orgUid, boolean deleted);
}
