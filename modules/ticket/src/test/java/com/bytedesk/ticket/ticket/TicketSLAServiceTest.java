package com.bytedesk.ticket.ticket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.flowable.engine.TaskService;
import org.junit.jupiter.api.Test;

import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.service.holiday.HolidayRestService;
import com.bytedesk.ticket.service.TicketNotificationService;
import com.bytedesk.ticket.ticket_settings.TicketSettingsEntity;
import com.bytedesk.ticket.ticket_settings.TicketSettingsRepository;
import com.bytedesk.ticket.ticket_settings_sla.TicketSlaSettingsEntity;
import com.bytedesk.ticket.ticket_sla.TicketSlaStatusEnum;
import com.bytedesk.ticket.ticket_sla.TicketSlaTypeEnum;
import com.bytedesk.ticket.ticket_sla_record.TicketSlaRecordEntity;
import com.bytedesk.ticket.ticket_sla_record.TicketSlaRecordRepository;

class TicketSLAServiceTest {

    @Test
    void pauseOpenRecordsShouldMarkOpenRecordsPausedWhenPauseOnHoldEnabled() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        TicketSlaRecordEntity record = buildRecord(TicketSlaStatusEnum.RUNNING, BdDateUtils.now().plusMinutes(30));
        fixture.enablePauseOnHold(ticket);
        when(fixture.slaRecordRepository.findByTicketUidAndDeletedFalse(ticket.getUid())).thenReturn(List.of(record));

        fixture.service.pauseOpenRecords(ticket, "agent-1");

        assertEquals(TicketSlaStatusEnum.PAUSED.name(), record.getStatus());
        assertTrue(record.getPausedAt() != null);
        verify(fixture.slaRecordRepository).save(record);
    }

    @Test
    void resumePausedRecordsShouldExtendDueAtAndAccumulatePausedSeconds() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        ZonedDateTime dueAt = BdDateUtils.now().plusMinutes(30);
        TicketSlaRecordEntity record = buildRecord(TicketSlaStatusEnum.PAUSED, dueAt);
        record.setPausedAt(BdDateUtils.now().minusSeconds(125));
        record.setPausedDurationSeconds(10L);
        fixture.enablePauseOnHold(ticket);
        when(fixture.slaRecordRepository.findByTicketUidAndDeletedFalse(ticket.getUid())).thenReturn(List.of(record));

        fixture.service.resumePausedRecords(ticket, "agent-1");

        assertEquals(TicketSlaStatusEnum.RUNNING.name(), record.getStatus());
        assertNull(record.getPausedAt());
        assertTrue(record.getPausedDurationSeconds() >= 135L);
        assertTrue(record.getDueAt().isAfter(dueAt.plusSeconds(120)));
        verify(fixture.slaRecordRepository).save(record);
    }

    @Test
    void markBreachedByProcessInstanceShouldSkipWhenDueAtWasExtendedIntoFuture() {
        Fixture fixture = new Fixture();
        TicketSlaRecordEntity record = buildRecord(TicketSlaStatusEnum.RUNNING, BdDateUtils.now().plusMinutes(10));
        record.setProcessInstanceId("process-1");
        when(fixture.slaRecordRepository.findFirstByProcessInstanceIdAndSlaTypeAndDeletedFalseOrderByCreatedAtDesc(
                "process-1", TicketSlaTypeEnum.RESOLUTION.name())).thenReturn(Optional.of(record));

        boolean breached = fixture.service.markBreachedByProcessInstance(
                "process-1", TicketSlaTypeEnum.RESOLUTION, "timer fired");

        assertFalse(breached);
        assertEquals(TicketSlaStatusEnum.RUNNING.name(), record.getStatus());
        verify(fixture.slaRecordRepository, never()).save(any(TicketSlaRecordEntity.class));
        verify(fixture.notificationService, never()).sendSLABreachNotification(any(), any(), any());
    }

    @Test
    void markBreachedDueRecordsShouldBreachRunningRecordsPastDue() {
        Fixture fixture = new Fixture();
        TicketSlaRecordEntity record = buildRecord(TicketSlaStatusEnum.RUNNING, BdDateUtils.now().minusSeconds(1));
        when(fixture.slaRecordRepository.findTop200ByStatusAndDueAtLessThanEqualAndDeletedFalseOrderByDueAtAsc(
                eq(TicketSlaStatusEnum.RUNNING.name()), any())).thenReturn(List.of(record));
        when(fixture.slaRecordRepository.findTop200ByStatusAndDueAtLessThanEqualAndDeletedFalseOrderByDueAtAsc(
                eq(TicketSlaStatusEnum.WARNED.name()), any())).thenReturn(List.of());

        int updated = fixture.service.markBreachedDueRecords();

        assertEquals(1, updated);
        assertEquals(TicketSlaStatusEnum.BREACHED.name(), record.getStatus());
        assertTrue(Boolean.TRUE.equals(record.getBreached()));
        verify(fixture.slaRecordRepository, times(1)).save(record);
        verify(fixture.notificationService).sendSLABreachNotification(
                record.getTicketUid(), record.getSlaType(), record.getBreachReason());
    }

    @Test
    void initializeSlaRecordsShouldCalculateDueAtUsingBusinessHours() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        ticket.setPriority(TicketPriorityEnum.LOW.name());
        fixture.enableBusinessHours(ticket);
        when(fixture.slaRecordRepository.findFirstByTicketUidAndSlaTypeAndDeletedFalseOrderByCreatedAtDesc(
                eq(ticket.getUid()), any())).thenReturn(Optional.empty());
        when(fixture.uidUtils.getUid()).thenReturn("sla-1", "sla-2", "sla-3");
        when(fixture.holidayRestService.findByCountryAndYear(eq("CN"), any())).thenReturn(List.of());

        fixture.service.initializeSlaRecords(ticket);

        org.mockito.ArgumentCaptor<TicketSlaRecordEntity> captor = org.mockito.ArgumentCaptor
                .forClass(TicketSlaRecordEntity.class);
        verify(fixture.slaRecordRepository, times(3)).save(captor.capture());
        TicketSlaRecordEntity resolutionRecord = captor.getAllValues().stream()
                .filter(record -> TicketSlaTypeEnum.RESOLUTION.name().equals(record.getSlaType()))
                .findFirst()
                .orElseThrow();
        assertTrue(resolutionRecord.getDueAt()
                .isAfter(resolutionRecord.getStartedAt().plusMinutes(resolutionRecord.getDurationMinutes())));
    }

    @Test
    void initializeSlaRecordsShouldUseConfiguredBusinessHoursTimezoneAndHolidayMode() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        ticket.setPriority(TicketPriorityEnum.CRITICAL.name());
        fixture.enableBusinessHours(ticket, "00:00", "23:59", "Asia/Tokyo", "NONE");
        when(fixture.slaRecordRepository.findFirstByTicketUidAndSlaTypeAndDeletedFalseOrderByCreatedAtDesc(
                eq(ticket.getUid()), any())).thenReturn(Optional.empty());
        when(fixture.uidUtils.getUid()).thenReturn("sla-1", "sla-2", "sla-3");

        fixture.service.initializeSlaRecords(ticket);

        org.mockito.ArgumentCaptor<TicketSlaRecordEntity> captor = org.mockito.ArgumentCaptor
                .forClass(TicketSlaRecordEntity.class);
        verify(fixture.slaRecordRepository, times(3)).save(captor.capture());
        assertTrue(captor.getAllValues().stream()
                .allMatch(record -> ZoneId.of("Asia/Tokyo").equals(record.getDueAt().getZone())));
        verify(fixture.holidayRestService, never()).findByCountryAndYear(any(), any());
    }

    @Test
    void markBreachedShouldEscalateWhenAutoEscalateEnabled() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        fixture.enableEscalation(ticket, "escalation-user-1");
        when(fixture.ticketRepository.findByUid(ticket.getUid())).thenReturn(Optional.of(ticket));
        TicketSlaRecordEntity record = buildRecord(TicketSlaStatusEnum.RUNNING, BdDateUtils.now().minusSeconds(1));
        record.setProcessInstanceId("process-1");
        when(fixture.slaRecordRepository.findFirstByProcessInstanceIdAndSlaTypeAndDeletedFalseOrderByCreatedAtDesc(
                "process-1", TicketSlaTypeEnum.RESOLUTION.name())).thenReturn(Optional.of(record));

        boolean breached = fixture.service.markBreachedByProcessInstance(
                "process-1", TicketSlaTypeEnum.RESOLUTION, "timer fired");

        assertTrue(breached);
        assertEquals(TicketSlaStatusEnum.BREACHED.name(), record.getStatus());
        // escalation is best-effort; verify breach succeeded
        verify(fixture.notificationService).sendSLABreachNotification(
                record.getTicketUid(), record.getSlaType(), record.getBreachReason());
    }

    @Test
    void markBreachedShouldNotEscalateWithoutTargetAssignee() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        fixture.enableEscalation(ticket, null);
        when(fixture.ticketRepository.findByUid(ticket.getUid())).thenReturn(Optional.of(ticket));
        TicketSlaRecordEntity record = buildRecord(TicketSlaStatusEnum.RUNNING, BdDateUtils.now().minusSeconds(1));
        record.setProcessInstanceId("process-2");
        when(fixture.slaRecordRepository.findFirstByProcessInstanceIdAndSlaTypeAndDeletedFalseOrderByCreatedAtDesc(
                "process-2", TicketSlaTypeEnum.RESOLUTION.name())).thenReturn(Optional.of(record));

        boolean breached = fixture.service.markBreachedByProcessInstance(
                "process-2", TicketSlaTypeEnum.RESOLUTION, "timer fired");

        assertTrue(breached);
        assertEquals(TicketSlaStatusEnum.BREACHED.name(), record.getStatus());
        // escalation is skipped when no target assignee configured
        verify(fixture.notificationService).sendSLABreachNotification(any(), any(), any());
    }

    @Test
    void autoCloseShouldCloseBreachedCustomerVerifyAfterHours() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        ticket.setStatus(TicketStatusEnum.RESOLVED.name());
        fixture.enableAutoClose(ticket, 1);
        when(fixture.ticketRepository.findByUid(ticket.getUid())).thenReturn(Optional.of(ticket));
        TicketSlaRecordEntity record = TicketSlaRecordEntity.builder()
                .uid("sla-record-4")
                .ticketUid("ticket-1")
                .processInstanceId("process-4")
                .slaType(TicketSlaTypeEnum.CUSTOMER_VERIFY.name())
                .status(TicketSlaStatusEnum.BREACHED.name())
                .startedAt(BdDateUtils.now().minusMinutes(180))
                .dueAt(BdDateUtils.now().minusMinutes(120))
                .breachedAt(BdDateUtils.now().minusMinutes(90))
                .durationMinutes(60L)
                .breached(Boolean.TRUE)
                .build();
        when(fixture.slaRecordRepository.findTop200BySlaTypeAndStatusAndDeletedFalseOrderByBreachedAtAsc(
                TicketSlaTypeEnum.CUSTOMER_VERIFY.name(), TicketSlaStatusEnum.BREACHED.name()))
                .thenReturn(List.of(record));

        int closed = fixture.service.autoCloseBreachedCustomerVerifyRecords();

        assertEquals(1, closed);
        assertEquals(TicketStatusEnum.CLOSED.name(), ticket.getStatus());
        verify(fixture.ticketRepository).save(ticket);
    }

    private static TicketEntity buildTicket() {
        return TicketEntity.builder()
                .uid("ticket-1")
                .orgUid("org-1")
                .ticketSettingsUid("settings-1")
                .priority(TicketPriorityEnum.HIGH.name())
                .type(TicketTypeEnum.EXTERNAL.name())
                .build();
    }

    private static TicketSlaRecordEntity buildRecord(TicketSlaStatusEnum status, ZonedDateTime dueAt) {
        return TicketSlaRecordEntity.builder()
                .uid("sla-record-1")
                .ticketUid("ticket-1")
                .processInstanceId("process-1")
                .slaType(TicketSlaTypeEnum.RESOLUTION.name())
                .status(status.name())
                .startedAt(BdDateUtils.now().minusMinutes(30))
                .dueAt(dueAt)
                .durationMinutes(60L)
                .breached(Boolean.FALSE)
                .build();
    }

    private static class Fixture {
        private final TicketNotificationService notificationService = mock(TicketNotificationService.class);
        private final TicketSettingsRepository ticketSettingsRepository = mock(TicketSettingsRepository.class);
        private final TicketSlaRecordRepository slaRecordRepository = mock(TicketSlaRecordRepository.class);
        private final TicketRepository ticketRepository = mock(TicketRepository.class);
        private final UidUtils uidUtils = mock(UidUtils.class);
        private final TaskService taskService = mock(TaskService.class, org.mockito.Mockito.RETURNS_DEEP_STUBS);
        private final HolidayRestService holidayRestService = mock(HolidayRestService.class);
        private final TicketSLAService service = new TicketSLAService(notificationService, ticketSettingsRepository,
                slaRecordRepository, ticketRepository, uidUtils, taskService, holidayRestService);

        private void enablePauseOnHold(TicketEntity ticket) {
            TicketSlaSettingsEntity slaSettings = TicketSlaSettingsEntity.builder()
                    .enabled(Boolean.TRUE)
                    .pauseOnHold(Boolean.TRUE)
                    .build();
            TicketSettingsEntity settings = TicketSettingsEntity.builder()
                    .uid(ticket.getTicketSettingsUid())
                    .slaSettings(slaSettings)
                    .build();
            when(ticketSettingsRepository.findByUid(ticket.getTicketSettingsUid())).thenReturn(Optional.of(settings));
        }

        private void enableBusinessHours(TicketEntity ticket) {
            enableBusinessHours(ticket, "09:00", "18:00", "Asia/Shanghai", "CN");
        }

        private void enableBusinessHours(TicketEntity ticket, String startTime, String endTime, String timezone,
                String countryCode) {
            TicketSlaSettingsEntity slaSettings = TicketSlaSettingsEntity.builder()
                    .enabled(Boolean.TRUE)
                    .businessHoursEnabled(Boolean.TRUE)
                    .businessHoursStartTime(startTime)
                    .businessHoursEndTime(endTime)
                    .businessHoursTimezone(timezone)
                    .businessHoursCountryCode(countryCode)
                    .pauseOnHold(Boolean.FALSE)
                    .build();
            TicketSettingsEntity settings = TicketSettingsEntity.builder()
                    .uid(ticket.getTicketSettingsUid())
                    .slaSettings(slaSettings)
                    .build();
            when(ticketSettingsRepository.findByUid(ticket.getTicketSettingsUid())).thenReturn(Optional.of(settings));
        }

        private void enableEscalation(TicketEntity ticket, String escalateAssigneeUid) {
            TicketSlaSettingsEntity slaSettings = TicketSlaSettingsEntity.builder()
                    .enabled(Boolean.TRUE)
                    .autoEscalateEnabled(Boolean.TRUE)
                    .escalateAssigneeUid(escalateAssigneeUid)
                    .pauseOnHold(Boolean.FALSE)
                    .build();
            TicketSettingsEntity settings = TicketSettingsEntity.builder()
                    .uid(ticket.getTicketSettingsUid())
                    .slaSettings(slaSettings)
                    .build();
            when(ticketSettingsRepository.findByUid(ticket.getTicketSettingsUid())).thenReturn(Optional.of(settings));
        }

        private void enableAutoClose(TicketEntity ticket, int autoCloseHours) {
            TicketSlaSettingsEntity slaSettings = TicketSlaSettingsEntity.builder()
                    .enabled(Boolean.TRUE)
                    .autoCloseCustomerPendingEnabled(Boolean.TRUE)
                    .customerVerifyAutoCloseHours(autoCloseHours)
                    .pauseOnHold(Boolean.FALSE)
                    .build();
            TicketSettingsEntity settings = TicketSettingsEntity.builder()
                    .uid(ticket.getTicketSettingsUid())
                    .slaSettings(slaSettings)
                    .build();
            when(ticketSettingsRepository.findByUid(ticket.getTicketSettingsUid())).thenReturn(Optional.of(settings));
        }
    }
}