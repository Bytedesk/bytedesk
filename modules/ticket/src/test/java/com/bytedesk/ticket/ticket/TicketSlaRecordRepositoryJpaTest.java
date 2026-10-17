package com.bytedesk.ticket.ticket;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.bytedesk.ticket.ticket.enums.TicketStatusEnum;
import com.bytedesk.ticket.ticket_sla.TicketSlaStatusEnum;
import com.bytedesk.ticket.ticket_sla_record.TicketSlaRecordEntity;
import com.bytedesk.ticket.ticket_sla_record.TicketSlaRecordRepository;

@DataJpaTest
@AutoConfigureTestDatabase
@EntityScan(basePackages = { "com.bytedesk.ticket", "com.bytedesk.service.form",
        "com.bytedesk.core.upload", "com.bytedesk.core.base" })
@ContextConfiguration(classes = TicketVisibilitySettingsScopedJpaTest.TestConfig.class)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.main.banner-mode=off"
})
class TicketSlaRecordRepositoryJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TicketSlaRecordRepository repository;

    @Test
    void terminalReconcileShouldAcceptNullInitialCursorAndAdvanceWithoutRepeatingRecords() {
        TicketEntity ticket = TicketEntity.builder()
                .uid("terminal-ticket")
                .status(TicketStatusEnum.VERIFIED_OK.name())
                .build();
        entityManager.persist(ticket);
        for (String uid : List.of("sla-001", "sla-002")) {
            entityManager.persist(TicketSlaRecordEntity.builder()
                    .uid(uid)
                    .ticketUid(ticket.getUid())
                    .status(TicketSlaStatusEnum.RUNNING.name())
                    .build());
        }
        entityManager.flush();
        entityManager.clear();

        List<String> statuses = List.of(TicketSlaStatusEnum.RUNNING.name());
        List<String> terminalStatuses = List.of(TicketStatusEnum.VERIFIED_OK.name());
        PageRequest page = PageRequest.of(0, 1);

        var first = repository.findTerminalReconcileCandidates(null, statuses, terminalStatuses, page);
        assertThat(first).extracting(TicketSlaRecordEntity::getUid).containsExactly("sla-001");

        var second = repository.findTerminalReconcileCandidates(first.getFirst().getUid(),
                statuses, terminalStatuses, page);
        assertThat(second).extracting(TicketSlaRecordEntity::getUid).containsExactly("sla-002");
        assertThat(repository.findTerminalReconcileCandidates(second.getFirst().getUid(),
                statuses, terminalStatuses, page)).isEmpty();
        assertThat(repository.findTerminalReconcileCandidates(null, statuses, terminalStatuses, page))
                .extracting(TicketSlaRecordEntity::getUid).containsExactly("sla-001");
    }
}
