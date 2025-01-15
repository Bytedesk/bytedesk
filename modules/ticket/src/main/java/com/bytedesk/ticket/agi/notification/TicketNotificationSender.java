package com.bytedesk.ticket.agi.notification;

public interface TicketNotificationSender {
    void send(TicketNotificationEntity notification) throws Exception;
} 