package com.bytedesk.meet.conference;

public class ConferenceAccessDeniedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConferenceAccessDeniedException(String message) {
        super(message);
    }
}