# email

This package manages email records, provider and protocol metadata, and email delivery lifecycle APIs.

## Implementation Notes

- Core models include EmailProviderEntity, EmailProviderRequest, EmailProviderResponse, EmailProviderExcel, EmailProviderExtra, EmailProviderSendResult, EmailProviderConnectionStatusEnum, EmailProviderProtocolEnum, EmailProviderProviderEnum, and EmailProviderTypeEnum.
- EmailProviderRepository, EmailProviderSpecification, EmailProviderRestController, and EmailProviderRestService provide persistence, filtering, and management endpoints for email records and account settings.
- EmailProviderSendService and EmailProviderListenerConfig cover email sending orchestration and listener/runtime configuration.
- EmailProviderTools and the util subpackage provide shared helper logic for email processing.
- EmailProviderEntityListener, the event subpackage, and EmailProviderPermissions provide lifecycle integration, create, update, delete events, and permission metadata.
