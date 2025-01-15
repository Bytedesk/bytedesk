package com.bytedesk.ticket.comment.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(Long commentId) {
        super("Comment not found: " + commentId);
    }
} 