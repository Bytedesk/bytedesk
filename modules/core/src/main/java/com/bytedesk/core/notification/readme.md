# notification

This package manages notification records, delivery-state tracking, and notification dispatch APIs.

## Implementation Notes

- Core models include NotificationEntity, NotificationRequest, NotificationResponse, NotificationDispatchResponse, NotificationStatusEnum, NotificationTypeEnum, and NotificationExcel.
- NotificationRepository, NotificationSpecification, NotificationRestController, NotificationRestService, and NotificationService provide persistence, filtering, dispatch orchestration, and management endpoints.
- NotificationProtobuf supports notification payload conversion for transport or integration scenarios.
- NotificationEntityListener, NotificationEventListener, and the event subpackage handle notification lifecycle callbacks and create, update, delete events.
- NotificationInitializer and NotificationPermissions provide bootstrap data and permission metadata for notification management features.
