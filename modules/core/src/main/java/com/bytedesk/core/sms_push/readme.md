# sms_push

This package manages SMS_PUSH records, send-service coordination, and SMS_PUSH lifecycle APIs.

## Implementation Notes

- Core models include SmsPushEntity, SmsPushRequest, SmsPushResponse, SmsPushExcel, SmsPushSendResult, and SmsPushTypeEnum.
- SmsPushRepository, SmsPushSpecification, SmsPushRestController, and SmsPushRestService provide persistence, filtering, and management endpoints for SMS_PUSH records.
- SmsPushSendService and SmsPushExternalSender handle SMS_PUSH delivery orchestration and external sender integration.
- SmsPushInitializer, SmsPushPermissions, SmsPushTools, SmsPushEntityListener, SmsPushEventListener, and the event subpackage provide bootstrap data, permission metadata, helper logic, and create, update, delete lifecycle events.
