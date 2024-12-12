package com.bytedesk.ticket.notification;

public interface TicketNotificationSender {
    void send(TicketNotificationEntity notification) throws Exception;
} 