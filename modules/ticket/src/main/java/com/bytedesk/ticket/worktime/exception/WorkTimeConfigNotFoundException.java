package com.bytedesk.ticket.worktime.exception;

public class WorkTimeConfigNotFoundException extends RuntimeException {
    public WorkTimeConfigNotFoundException(Long configId) {
        super("Work time config not found: " + configId);
    }
} 