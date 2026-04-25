# organization

This package manages organization records, verification and identity metadata, and organization lifecycle APIs.

## Implementation Notes

- Core models include OrganizationEntity, OrganizationRequest, OrganizationResponse, OrganizationResponseSimple, OrganizationIdentityTypeEnum, OrganizationVerifyStatusEnum, and OrganizationVerifyTypeEnum.
- OrganizationRepository, OrganizationSpecification, OrganizationRestController, and OrganizationRestService provide persistence, filtering, and management endpoints for organization records.
- OrganizationDefaults, OrganizationInitializer, and OrganizationPermissions provide default organization data, bootstrap logic, and permission metadata.
- OrganizationEntityListener, OrganizationEventListener, and the event subpackage handle organization lifecycle callbacks and create events.
