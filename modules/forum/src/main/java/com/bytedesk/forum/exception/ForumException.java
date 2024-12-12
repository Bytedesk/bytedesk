package com.bytedesk.forum.exception;

public class ForumException extends RuntimeException {
    
    public ForumException(String message) {
        super(message);
    }
    
    public ForumException(String message, Throwable cause) {
        super(message, cause);
    }
} 