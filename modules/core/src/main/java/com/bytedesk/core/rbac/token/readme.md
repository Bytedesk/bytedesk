# token

This package manages token records, token scopes and types, and token lifecycle APIs.

## Implementation Notes

- Core files include TokenEntity, TokenRequest, TokenResponse, TokenScopeEnum, and TokenTypeEnum for token record and metadata modeling.
- TokenRepository, TokenSpecification, TokenRestController, and TokenRestService provide persistence, filtering, and management endpoints for token records.
- TokenEntityListener, TokenEventListener, and the event subpackage handle token lifecycle callbacks and create, update events.
- TokenInitializer and TokenPermissions provide bootstrap data and permission metadata for token management features.
