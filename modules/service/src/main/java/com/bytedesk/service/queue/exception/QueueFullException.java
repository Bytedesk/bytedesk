package com.bytedesk.service.queue.exception;

public class QueueFullException extends RuntimeException {
    
    public QueueFullException(String message) {
        super(message);
    }
} 