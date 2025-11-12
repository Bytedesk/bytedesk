package com.bytedesk.ticket.ticket_settings.binding;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TicketSettingsBindingRepository extends JpaRepository<TicketSettingsBindingEntity, Long>, JpaSpecificationExecutor<TicketSettingsBindingEntity> {

    Optional<TicketSettingsBindingEntity> findByOrgUidAndWorkgroupUidAndDeletedFalse(String orgUid, String workgroupUid);

    List<TicketSettingsBindingEntity> findByTicketSettingsUidAndDeletedFalse(String ticketSettingsUid);

    boolean existsByOrgUidAndWorkgroupUidAndDeletedFalse(String orgUid, String workgroupUid);
}
