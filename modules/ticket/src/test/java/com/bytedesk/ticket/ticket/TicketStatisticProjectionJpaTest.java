package com.bytedesk.ticket.ticket;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import com.bytedesk.core.rbac.user.UserProtobuf;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase
@EntityScan(basePackages = { "com.bytedesk.ticket", "com.bytedesk.service.form",
        "com.bytedesk.core.upload", "com.bytedesk.core.base" })
@ContextConfiguration(classes = TicketVisibilitySettingsScopedJpaTest.TestConfig.class)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.main.banner-mode=off"
})
class TicketStatisticProjectionJpaTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TicketRepository repository;

    @Test
    void statisticSlicesReturnRawIdentitySnapshotsAndClosedTimeWithScopedFilters() {
        ZonedDateTime createdAt = ZonedDateTime.of(2026, 10, 1, 9, 0, 0, 0, ZoneOffset.UTC);
        String reporter = UserProtobuf.builder().uid("reporter-member").type("MEMBER").build().toJson();
        String assignee = UserProtobuf.builder().uid("handler-member").type("MEMBER").build().toJson();
        entityManager.persist(TicketEntity.builder().uid("ticket-statistic").orgUid("org-statistic")
                .createdAt(createdAt).status("CLOSED").closedTime(createdAt.plusSeconds(30))
                .reporter(reporter).assignee(assignee).workgroupUid("group-statistic")
                .departmentUid("department-statistic").build());
        entityManager.persist(TicketEntity.builder().uid("other-org").orgUid("other")
                .createdAt(createdAt).build());
        entityManager.persist(TicketEntity.builder().uid("deleted").orgUid("org-statistic")
                .createdAt(createdAt).deleted(true).build());
        entityManager.persist(TicketEntity.builder().uid("outside-window").orgUid("org-statistic")
                .createdAt(createdAt.minusDays(1)).build());
        entityManager.flush();
        entityManager.clear();

        var page = PageRequest.of(0, 10);
        var slice = repository.findByOrgUidAndDeletedFalseAndCreatedAtBetween(
                "org-statistic", createdAt, createdAt.plusDays(1), page);
        assertThat(slice.getContent()).hasSize(1);
        TicketStatisticRow row = slice.getContent().getFirst();
        assertThat(row.getReporter()).isEqualTo(reporter);
        assertThat(row.getAssignee()).isEqualTo(assignee);
        assertThat(row.getClosedTime().toInstant()).isEqualTo(createdAt.plusSeconds(30).toInstant());
        assertThat(row.getResolvedTime()).isNull();
        assertThat(repository.findByOrgUidAndDepartmentUidAndDeletedFalseAndCreatedAtBetween(
                "org-statistic", "department-statistic", createdAt, createdAt.plusDays(1), page).getContent())
                .extracting(TicketStatisticRow::getUid).containsExactly("ticket-statistic");
        assertThat(repository.findByOrgUidAndWorkgroupUidContainingAndDeletedFalseAndCreatedAtBetween(
                "org-statistic", "group-statistic", createdAt, createdAt.plusDays(1), page).getContent())
                .extracting(TicketStatisticRow::getUid).containsExactly("ticket-statistic");
        assertThat(repository.findByOrgUidAndAssigneeContainingAndDeletedFalseAndCreatedAtBetween(
                "org-statistic", "\"uid\":\"handler-member\"", createdAt, createdAt.plusDays(1), page).getContent())
                .extracting(TicketStatisticRow::getUid).containsExactly("ticket-statistic");
    }
}
