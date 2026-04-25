# topic_subscription

This package manages topic subscription records, subscription type metadata, and pub-sub lifecycle APIs.

## Implementation Notes

- Core models include TopicSubscriptionEntity, TopicSubscriptionRequest, TopicSubscriptionResponse, TopicSubscriptionExcel, and TopicSubscriptionTypeEnum.
- TopicSubscriptionRepository, TopicSubscriptionSpecification, TopicSubscriptionRestController, and TopicSubscriptionRestService provide persistence, filtering, and management endpoints for subscription records.
- TopicSubscriptionCacheService, TopicSubscriptionInitializer, TopicSubscriptionPermissions, and TopicSubscriptionTools provide cache support, bootstrap data, permission metadata, and shared helper logic.
- TopicSubscriptionEntityListener, TopicSubscriptionEventListener, and the event subpackage handle create, update, and delete lifecycle events.
