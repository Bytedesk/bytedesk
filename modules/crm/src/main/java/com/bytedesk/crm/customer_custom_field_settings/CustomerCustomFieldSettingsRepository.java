package com.bytedesk.crm.customer_custom_field_settings;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerCustomFieldSettingsRepository extends JpaRepository<CustomerCustomFieldSettingsEntity, Long> {

    Optional<CustomerCustomFieldSettingsEntity> findByOrgUidAndDeleted(String orgUid, boolean deleted);
}
