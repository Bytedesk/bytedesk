package com.bytedesk.ticket.attachment.exception;

public class AttachmentNotFoundException extends RuntimeException {
    public AttachmentNotFoundException(Long attachmentId) {
        super("Attachment not found: " + attachmentId);
    }
} 