package com.bytedesk.voc.exception;

public class ReplyNotFoundException extends VocException {
    public ReplyNotFoundException(Long replyId) {
        super("Reply not found: " + replyId);
    }
} 