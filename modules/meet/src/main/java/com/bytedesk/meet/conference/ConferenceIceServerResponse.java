package com.bytedesk.meet.conference;

import java.util.List;

public record ConferenceIceServerResponse(
        List<ConferenceIceServer> iceServers) {
}