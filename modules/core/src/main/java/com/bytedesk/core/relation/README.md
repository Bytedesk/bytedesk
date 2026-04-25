# relation

This package models user and business relations, status changes, interaction statistics, and relation APIs.

## Implementation Notes

- Core domain files include RelationEntity, RelationRequest, RelationResponse, RelationExcel, RelationTypeEnum, and RelationStatusEnum.
- RelationRepository, RelationSpecification, RelationRestController, and RelationRestService provide persistence, filtering, and API orchestration.
- RelationEntityListener, RelationEventListener, and the event subpackage handle lifecycle callbacks and domain events.
- RelationUtils and RelationTools centralize relation naming, validation, scoring, and helper logic.
- RelationInitializer and RelationPermissions bootstrap default data and permission metadata.
