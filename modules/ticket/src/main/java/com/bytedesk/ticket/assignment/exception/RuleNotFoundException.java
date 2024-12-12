package com.bytedesk.ticket.assignment.exception;

public class RuleNotFoundException extends RuntimeException {
    public RuleNotFoundException(Long ruleId) {
        super("Assignment rule not found: " + ruleId);
    }
} 