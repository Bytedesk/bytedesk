package com.bytedesk.service.queue.exception;

public class QueueMemberAlreadyExistsException extends RuntimeException {

    public QueueMemberAlreadyExistsException(String message) {
        super(message);
    }
}
