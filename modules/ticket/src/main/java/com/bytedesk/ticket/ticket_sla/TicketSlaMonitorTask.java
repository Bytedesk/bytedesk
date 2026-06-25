package com.bytedesk.ticket.ticket_sla;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bytedesk.ticket.ticket.TicketSLAService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketSlaMonitorTask {

    private final TicketSLAService ticketSLAService;

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
}