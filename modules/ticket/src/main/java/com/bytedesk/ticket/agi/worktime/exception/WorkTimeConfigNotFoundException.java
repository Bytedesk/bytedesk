package com.bytedesk.ticket.agi.worktime.exception;

public class WorkTimeConfigNotFoundException extends RuntimeException {
    public WorkTimeConfigNotFoundException(Long configId) {
        super("Work time config not found: " + configId);
    }
} 