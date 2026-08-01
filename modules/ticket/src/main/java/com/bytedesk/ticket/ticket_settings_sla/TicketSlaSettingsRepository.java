package com.bytedesk.ticket.ticket_settings_sla;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TicketSlaSettingsRepository extends JpaRepository<TicketSlaSettingsEntity, Long>, JpaSpecificationExecutor<TicketSlaSettingsEntity> {

    Optional<TicketSlaSettingsEntity> findByUid(String uid);
}