package com.bytedesk.remote.session;

import java.util.List;

public record RemoteSessionResponse(
        String uid, String threadUid, String roomUid, String viewerName,
        RemoteSessionStatusEnum status, boolean controlRequested, boolean controlGranted,
        long serverTime, long expiresAt, long leaseUntil, long inputCount, List<Signal> signals) {
    public record Signal(String id, String type, String payload) {}
    public record Invitation(RemoteSessionResponse session, String invitationToken) {}
    public record Joined(RemoteSessionResponse session, String hostToken) {}
}
