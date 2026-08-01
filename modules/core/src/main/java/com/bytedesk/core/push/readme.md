# push

This package manages push records, multi-channel push routing, and push execution APIs.

## Implementation Notes

- Core models include PushEntity, PushTokenEntity, PushRequest, PushResponse, and PushStatusEnum.
- PushRepository, PushSpecification, PushRestController, PushRestService, and PushService provide persistence, filtering, and push management endpoints.
- PushFilterService and PushExpireCacheService handle routing preconditions, filtering, and expiring push data.
- PushPermissions and PushEventListener provide permission metadata and event-side integration.
- The service subpackage contains channel-specific send services such as APNs, Huawei, Mi, Web, Email, and generic send-result abstractions, while the strategy subpackage encapsulates auth validation strategies.
