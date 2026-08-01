# authority

This package manages authority items, fine-grained permission metadata, and authority lifecycle APIs.

## Implementation Notes

- Core files include AuthorityEntity, AuthorityRequest, and AuthorityResponse for authority record modeling.
- AuthorityRepository, AuthoritySpecification, AuthorityRestController, and AuthorityRestService provide persistence, filtering, and management endpoints for authority items.
- AuthorityEntityListener, AuthorityEventListener, and the event subpackage handle authority lifecycle callbacks and create, update, delete events.
- AuthorityInitializer and AuthorityPermissions provide built-in authority bootstrap data and permission metadata.
