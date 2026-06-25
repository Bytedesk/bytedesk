package com.bytedesk.ticket.ticket_sla_rule;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TicketSlaRuleRepository extends JpaRepository<TicketSlaRuleEntity, Long>, JpaSpecificationExecutor<TicketSlaRuleEntity> {

    Optional<TicketSlaRuleEntity> findByUid(String uid);
}