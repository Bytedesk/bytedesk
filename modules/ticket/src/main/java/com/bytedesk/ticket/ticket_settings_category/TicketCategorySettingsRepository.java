package com.bytedesk.ticket.ticket_settings_category;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketCategorySettingsRepository
        extends JpaRepository<TicketCategorySettingsEntity, Long> {

    Optional<TicketCategorySettingsEntity> findByUid(String uid);
}