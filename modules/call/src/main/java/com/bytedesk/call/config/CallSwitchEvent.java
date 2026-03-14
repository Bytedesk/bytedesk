package com.bytedesk.call.config;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import lombok.Getter;

@Getter
public class CallSwitchEvent {

    private final String eventName;
    private final String eventSubclass;
    private final Map<String, String> headers;
    private final List<String> bodyLines;
    private final Instant receivedAt;

    public CallSwitchEvent(String eventName, String eventSubclass, Map<String, String> headers, List<String> bodyLines) {
        this.eventName = eventName;
        this.eventSubclass = eventSubclass;
        this.headers = headers;
        this.bodyLines = bodyLines;
        this.receivedAt = Instant.now();
    }
}
