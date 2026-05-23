# sms

This package manages SMS records, send-service coordination, and SMS lifecycle APIs.

## Implementation Notes

- Core models include SmsEntity, SmsRequest, SmsResponse, SmsExcel, SmsSendResult, and SmsTypeEnum.
- SmsRepository, SmsSpecification, SmsRestController, and SmsRestService provide persistence, filtering, and management endpoints for SMS records.
- SmsSendService and SmsExternalSender handle SMS delivery orchestration and external sender integration.
- SmsInitializer, SmsPermissions, SmsTools, SmsEntityListener, SmsEventListener, and the event subpackage provide bootstrap data, permission metadata, helper logic, and create, update, delete lifecycle events.
