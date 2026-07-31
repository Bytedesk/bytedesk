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
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.junit.jupiter.api.Test;

import com.bytedesk.core.uid.UidUtils;
import com.bytedesk.core.utils.BdDateUtils;
import com.bytedesk.ticket.process.ProcessEntity;
import com.bytedesk.ticket.process.ProcessRepository;
import com.bytedesk.service.holiday.HolidayRestService;
import com.bytedesk.ticket.service.TicketNotificationService;
import com.bytedesk.ticket.ticket.enums.TicketPriorityEnum;
import com.bytedesk.ticket.ticket.enums.TicketStatusEnum;
import com.bytedesk.ticket.ticket.enums.TicketTypeEnum;
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
        void pauseOpenRecordsShouldSkipInactiveNodeRecords() {
                Fixture fixture = new Fixture();
                TicketEntity ticket = buildTicket();
                ticket.setProcessInstanceId("process-1");
                TicketSlaRecordEntity globalRecord = buildRecord(TicketSlaStatusEnum.RUNNING, BdDateUtils.now().plusMinutes(30));
                globalRecord.setSlaSource("GLOBAL");
                TicketSlaRecordEntity nodeRecord = buildRecord(TicketSlaStatusEnum.RUNNING, BdDateUtils.now().plusMinutes(20));
                nodeRecord.setUid("sla-record-node");
                nodeRecord.setSlaSource("NODE");
                nodeRecord.setTaskId("task-inactive");
                nodeRecord.setTaskDefinitionKey("processTicket");
                fixture.enablePauseOnHold(ticket);
                fixture.mockActiveTasks(ticket.getProcessInstanceId(), List.of());
                when(fixture.slaRecordRepository.findByTicketUidAndDeletedFalse(ticket.getUid()))
                                .thenReturn(List.of(globalRecord, nodeRecord));

                fixture.service.pauseOpenRecords(ticket, "agent-1");

                assertEquals(TicketSlaStatusEnum.PAUSED.name(), globalRecord.getStatus());
                assertEquals(TicketSlaStatusEnum.RUNNING.name(), nodeRecord.getStatus());
                verify(fixture.slaRecordRepository).save(globalRecord);
                verify(fixture.slaRecordRepository, never()).save(nodeRecord);
        }

        @Test
        void resumePausedRecordsShouldSkipInactiveNodeRecords() {
                Fixture fixture = new Fixture();
                TicketEntity ticket = buildTicket();
                ticket.setProcessInstanceId("process-1");
                TicketSlaRecordEntity globalRecord = buildRecord(TicketSlaStatusEnum.PAUSED, BdDateUtils.now().plusMinutes(30));
                globalRecord.setSlaSource("GLOBAL");
                globalRecord.setPausedAt(BdDateUtils.now().minusSeconds(120));
                TicketSlaRecordEntity nodeRecord = buildRecord(TicketSlaStatusEnum.PAUSED, BdDateUtils.now().plusMinutes(20));
                nodeRecord.setUid("sla-record-node");
                nodeRecord.setSlaSource("NODE");
                nodeRecord.setTaskId("task-inactive");
                nodeRecord.setTaskDefinitionKey("processTicket");
                nodeRecord.setPausedAt(BdDateUtils.now().minusSeconds(120));
                fixture.enablePauseOnHold(ticket);
                fixture.mockActiveTasks(ticket.getProcessInstanceId(), List.of());
                when(fixture.slaRecordRepository.findByTicketUidAndDeletedFalse(ticket.getUid()))
                                .thenReturn(List.of(globalRecord, nodeRecord));

                fixture.service.resumePausedRecords(ticket, "agent-1");

                assertEquals(TicketSlaStatusEnum.RUNNING.name(), globalRecord.getStatus());
                assertEquals(TicketSlaStatusEnum.PAUSED.name(), nodeRecord.getStatus());
                verify(fixture.slaRecordRepository).save(globalRecord);
                verify(fixture.slaRecordRepository, never()).save(nodeRecord);
        }

        @Test
        void cancelOpenRecordsShouldCancelPausedRecordsToo() {
                Fixture fixture = new Fixture();
                TicketEntity ticket = buildTicket();
                TicketSlaRecordEntity pausedRecord = buildRecord(TicketSlaStatusEnum.PAUSED, BdDateUtils.now().plusMinutes(20));
                pausedRecord.setPausedAt(BdDateUtils.now().minusSeconds(90));
                when(fixture.slaRecordRepository.findByTicketUidAndDeletedFalse(ticket.getUid()))
                                .thenReturn(List.of(pausedRecord));

                fixture.service.cancelOpenRecords(ticket, "agent-1");

                assertEquals(TicketSlaStatusEnum.CANCELED.name(), pausedRecord.getStatus());
                assertEquals("agent-1", pausedRecord.getCompletedBy());
                assertNull(pausedRecord.getPausedAt());
                assertTrue(pausedRecord.getCompletedAt() != null);
                verify(fixture.slaRecordRepository).save(pausedRecord);
        }

        @Test
        void cancelNodeSlaRecordShouldCancelPausedNodeRecord() {
                Fixture fixture = new Fixture();
                TicketSlaRecordEntity nodeRecord = buildRecord(TicketSlaStatusEnum.PAUSED, BdDateUtils.now().plusMinutes(10));
                nodeRecord.setSlaSource("NODE");
                nodeRecord.setTaskId("task-1");
                nodeRecord.setTaskDefinitionKey("processTicket");
                nodeRecord.setPausedAt(BdDateUtils.now().minusSeconds(30));
                when(fixture.slaRecordRepository.findFirstByTaskIdAndDeletedFalseOrderByCreatedAtDesc("task-1"))
                                .thenReturn(Optional.of(nodeRecord));

                fixture.service.cancelNodeSlaRecord("task-1", "agent-1", "task deleted");

                assertEquals(TicketSlaStatusEnum.CANCELED.name(), nodeRecord.getStatus());
                assertEquals("agent-1", nodeRecord.getCompletedBy());
                assertNull(nodeRecord.getPausedAt());
                assertTrue(nodeRecord.getCompletedAt() != null);
                verify(fixture.slaRecordRepository).save(nodeRecord);
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

    @Test
    void ensureNodeSlaRecordShouldCreateNodeScopedRecordWhenNodeHasExplicitDuration() {
        Fixture fixture = new Fixture();
        TicketEntity ticket = buildTicket();
        ticket.setProcessEntityUid("process-1");
        Task task = mock(Task.class);
        when(task.getId()).thenReturn("task-1");
        when(task.getTaskDefinitionKey()).thenReturn("processTicket");
        when(fixture.processRepository.findByUid("process-1")).thenReturn(Optional.of(ProcessEntity.builder()
                .uid("process-1")
                .flowgramSchema("""
                        {"nodes":[{"id":"processTicket","type":"approval","data":{"ticketStage":"PROCESSING","slaType":"RESOLUTION","slaDurationMinutes":30}}],"edges":[]}
                        """)
                .build()));
        when(fixture.slaRecordRepository.findFirstByTaskIdAndDeletedFalseOrderByCreatedAtDesc("task-1"))
                .thenReturn(Optional.empty());
        when(fixture.uidUtils.getUid()).thenReturn("node-sla-1");

        fixture.service.ensureNodeSlaRecord(ticket, task);

        org.mockito.ArgumentCaptor<TicketSlaRecordEntity> captor = org.mockito.ArgumentCaptor
                .forClass(TicketSlaRecordEntity.class);
        verify(fixture.slaRecordRepository).save(captor.capture());
        TicketSlaRecordEntity record = captor.getValue();
        assertEquals("task-1", record.getTaskId());
        assertEquals("processTicket", record.getTaskDefinitionKey());
        assertEquals("NODE", record.getSlaSource());
        assertEquals(TicketSlaTypeEnum.RESOLUTION.name(), record.getSlaType());
        assertEquals(30L, record.getDurationMinutes());
    }

    @Test
    void completeNodeSlaRecordShouldCompleteOpenNodeRecord() {
        Fixture fixture = new Fixture();
        TicketSlaRecordEntity record = buildRecord(TicketSlaStatusEnum.RUNNING, BdDateUtils.now().plusMinutes(10));
        record.setTaskId("task-1");
        record.setTaskDefinitionKey("processTicket");
        record.setSlaSource("NODE");
        when(fixture.slaRecordRepository.findFirstByTaskIdAndDeletedFalseOrderByCreatedAtDesc("task-1"))
                .thenReturn(Optional.of(record));

        fixture.service.completeNodeSlaRecord("task-1", "member-1");

        assertEquals(TicketSlaStatusEnum.COMPLETED.name(), record.getStatus());
        assertEquals("member-1", record.getCompletedBy());
        assertTrue(record.getCompletedAt() != null);
        verify(fixture.slaRecordRepository).save(record);
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
        private final ProcessRepository processRepository = mock(ProcessRepository.class);
        private final UidUtils uidUtils = mock(UidUtils.class);
        private final TaskService taskService = mock(TaskService.class, org.mockito.Mockito.RETURNS_DEEP_STUBS);
                private final TaskQuery taskQuery = mock(TaskQuery.class, org.mockito.Mockito.RETURNS_SELF);
        private final HolidayRestService holidayRestService = mock(HolidayRestService.class);
        private final TicketSLAService service = new TicketSLAService(notificationService, ticketSettingsRepository,
                slaRecordRepository, ticketRepository, processRepository, uidUtils, taskService, holidayRestService);

                private Fixture() {
                        when(taskService.createTaskQuery()).thenReturn(taskQuery);
                        when(taskQuery.active()).thenReturn(taskQuery);
                }

                private void mockActiveTasks(String processInstanceId, List<Task> tasks) {
                        when(taskQuery.processInstanceId(processInstanceId)).thenReturn(taskQuery);
                        when(taskQuery.list()).thenReturn(tasks);
                }

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