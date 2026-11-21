# session

This package handles remote assistance session lifecycle, signaling, and session APIs.

## Implementation Notes

- Session entities, repositories, application services, request and response models, and session APIs are implemented here.
- Every auditable transition is published as a `RemoteSessionAuditEvent`; the enterprise module persists it.
- TURN/STUN credentials are issued by the enterprise module, which owns the ICE endpoints.
