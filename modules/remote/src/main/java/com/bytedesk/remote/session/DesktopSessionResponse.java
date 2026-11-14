package com.bytedesk.remote.session;

import java.util.List;

public record DesktopSessionResponse(
        String uid, String threadUid, String roomUid, String viewerName,
        DesktopSessionStatusEnum status, boolean controlRequested, boolean controlGranted,
        long serverTime, long expiresAt, long leaseUntil, long inputCount, List<Signal> signals) {
    public record Signal(String id, String type, String payload) {}
    public record Invitation(DesktopSessionResponse session, String invitationToken) {}
    public record Joined(DesktopSessionResponse session, String hostToken) {}
}
