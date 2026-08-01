# connection

This package manages connection records, presence synchronization, and connection lifecycle APIs.

## Implementation Notes

- Core models include ConnectionEntity, ConnectionRequest, ConnectionResponse, ConnectionExcel, PresenceResponse, ConnectionStatusEnum, and ConnectionProtocalEnum.
- ConnectionRepository, ConnectionSpecification, ConnectionRestController, and ConnectionRestService provide persistence, filtering, and management endpoints for connection records.
- ConnectionHeartbeatFlushTask, ConnectionMetrics, and PresenceTtlResolver support heartbeat flushing, metrics collection, and presence expiration handling.
- ConnectionInitializer, ConnectionPermissions, ConnectionTools, ConnectionEntityListener, ConnectionEventListener, and the event subpackage provide bootstrap data, permission metadata, helper logic, and create, update, delete lifecycle events.
