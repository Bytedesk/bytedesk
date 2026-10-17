package com.bytedesk.ticket.ticket_sla;

import org.jspecify.annotations.Nullable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.bytedesk.ticket.ticket.TicketSLAService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketSlaMonitorTask {

    private final TicketSLAService ticketSLAService;
    @Value("${bytedesk.ticket.sla.reconcile-dry-run-enabled:true}")
    private boolean reconcileDryRunEnabled;
    @Value("${bytedesk.ticket.sla.reconcile-batch-size:100}")
    private int reconcileBatchSize;
    private @Nullable String reconcileCursor;

    @Scheduled(fixedDelayString = "${bytedesk.ticket.sla.monitor-fixed-delay-ms:60000}")
    public void markWarningRecords() {
        int breached = ticketSLAService.markBreachedDueRecords();
        if (breached > 0) {
            log.info("ticket SLA breached records updated: {}", breached);
        }
        int updated = ticketSLAService.markWarningRecords();
        if (updated > 0) {
            log.info("ticket SLA warning records updated: {}", updated);
        }
        int autoClosed = ticketSLAService.autoCloseBreachedCustomerVerifyRecords();
        if (autoClosed > 0) {
            log.info("ticket SLA auto-closed customer verify records: {}", autoClosed);
        }
    }

    @Scheduled(fixedDelayString = "${bytedesk.ticket.sla.reconcile-fixed-delay-ms:3600000}")
    public synchronized void previewTerminalSlaReconcile() {
        if (!reconcileDryRunEnabled) {
            return;
        }
        TicketSLAService.TerminalSlaReconcileDryRunBatch batch =
                ticketSLAService.previewTerminalSlaReconcile(reconcileCursor, reconcileBatchSize);
        if (batch.scannedRecords() == 0) {
            reconcileCursor = null;
            return;
        }
        reconcileCursor = batch.nextCursor();
        log.info("ticket SLA terminal reconcile dry-run: scanned={}, active={}, breachedReview={}, "
                        + "missingTerminalTime={}, nextCursor={}",
                batch.scannedRecords(), batch.activeRecords(), batch.breachedRecords(),
                batch.missingTerminalTimeRecords(), reconcileCursor);
    }
}