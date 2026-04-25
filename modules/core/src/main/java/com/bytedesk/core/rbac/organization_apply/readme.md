# organization_apply

This package manages organization application records, approval status metadata, and application lifecycle APIs.

## Implementation Notes

- Core models include OrganizationApplyEntity, OrganizationApplyRequest, OrganizationApplyResponse, OrganizationApplyExcel, OrganizationApplyStatusEnum, and OrganizationApplyTypeEnum.
- OrganizationApplyRepository, OrganizationApplySpecification, OrganizationApplyRestController, and OrganizationApplyRestService provide persistence, filtering, and approval management endpoints.
- OrganizationApplyEntityListener, OrganizationApplyEventListener, and the event subpackage handle application lifecycle callbacks and create, update, delete events.
- OrganizationApplyInitializer, OrganizationApplyPermissions, and OrganizationApplyTools provide bootstrap data, permission metadata, and helper logic for application processing.
