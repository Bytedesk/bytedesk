package com.bytedesk.core.exception;

public class AgentCapacityExceededException extends RuntimeException {

    public AgentCapacityExceededException(String message) {
        super(message);
    }
}