package com.bytedesk.meet.conference;

import java.util.List;

public record ConferenceIceServer(
        List<String> urls,
        String username,
        String credential) {
}