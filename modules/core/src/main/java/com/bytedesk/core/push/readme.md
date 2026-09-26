# push

This package manages push records, multi-channel push routing, and push execution APIs.

## Implementation Notes

- Core models include PushEntity, PushRequest, PushResponse, and PushStatusEnum.
- PushRepository, PushSpecification, PushRestController, PushRestService, and PushService provide persistence, filtering, and push management endpoints.
- PushFilterService and PushExpireCacheService handle routing preconditions, filtering, and expiring push data.
- PushPermissions and PushEventListener provide permission metadata and event-side integration.
- The service subpackage contains channel-specific send services such as Web, Email, and generic send-result abstractions, while the strategy subpackage encapsulates auth validation strategies. It also hosts the `AgentMobilePushService` interface: OSS modules depend on this thin interface only, whose enterprise implementation (`PushApnsService`) now lives in `enterprise/core` push packages; community builds without enterprise modules simply skip mobile push.
- Device push binding (push_android_device), APNs push/records (push_apns, push_apns_p12, push_apns_token), Android push records (push_android), push credentials config (push_config) and push settings (push_settings) have been migrated to `enterprise/core` (com.bytedesk.enterprise.core.push_*).
