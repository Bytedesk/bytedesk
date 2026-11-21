package com.bytedesk.remote.session.event;

/**
 * Audit-worthy session transition. The community module only publishes it, so the
 * audit trail itself remains an enterprise capability.
 */
public record RemoteSessionAuditEvent(String sessionUid, String orgUid, String actorUid,
        String eventType, long inputCount) {
}
