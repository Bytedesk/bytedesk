package com.bytedesk.voc.exception;

public class FeedbackNotFoundException extends VocException {
    public FeedbackNotFoundException(Long feedbackId) {
        super("Feedback not found: " + feedbackId);
    }
} 