package com.bytedesk.ticket.agi.assignment.exception;

public class DuplicateRuleNameException extends RuntimeException {
    public DuplicateRuleNameException(String name) {
        super("Assignment rule with name '" + name + "' already exists");
    }
} 