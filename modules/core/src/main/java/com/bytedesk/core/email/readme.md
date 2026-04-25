# email

This package manages email records, provider and protocol metadata, and email delivery lifecycle APIs.

## Implementation Notes

- Core models include EmailEntity, EmailRequest, EmailResponse, EmailExcel, EmailExtra, EmailSendResult, EmailConnectionStatusEnum, EmailProtocolEnum, EmailProviderEnum, and EmailTypeEnum.
- EmailRepository, EmailSpecification, EmailRestController, and EmailRestService provide persistence, filtering, and management endpoints for email records and account settings.
- EmailSendService and EmailListenerConfig cover email sending orchestration and listener/runtime configuration.
- EmailTools and the util subpackage provide shared helper logic for email processing.
- EmailEntityListener, the event subpackage, and EmailPermissions provide lifecycle integration, create, update, delete events, and permission metadata.
