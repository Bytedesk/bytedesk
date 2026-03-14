package com.bytedesk.meet.conference;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConferenceRuntimeProperties {

    @Value("${bytedesk.meet.stun-server:stun:stun.l.google.com:19302}")
    private String stunServer;

    @Value("${bytedesk.meet.turn-server:}")
    private String turnServer;

    @Value("${bytedesk.meet.turn-username:}")
    private String turnUsername;

    @Value("${bytedesk.meet.turn-credential:}")
    private String turnCredential;

    @Value("${bytedesk.meet.participant-timeout-seconds:45}")
    private long participantTimeoutSeconds;

    public ConferenceIceServerResponse toIceServerResponse() {
        List<ConferenceIceServer> iceServers = new ArrayList<>();
        if (!stunServer.isBlank()) {
            iceServers.add(new ConferenceIceServer(List.of(stunServer.trim()), null, null));
        }
        if (!turnServer.isBlank()) {
            iceServers.add(new ConferenceIceServer(List.of(turnServer.trim()),
                    turnUsername.isBlank() ? null : turnUsername.trim(),
                    turnCredential.isBlank() ? null : turnCredential.trim()));
        }
        return new ConferenceIceServerResponse(iceServers);
    }

    public long participantTimeoutSeconds() {
        return participantTimeoutSeconds;
    }
}