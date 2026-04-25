# role

This package manages role definitions, role rule metadata, and role lifecycle APIs.

## Implementation Notes

- Core files include RoleEntity, RoleRequest, RoleResponse, RoleResponseSimple, and RoleExcel for role data and import-export modeling.
- RoleRepository, RoleSpecification, RoleRestController, and RoleRestService provide persistence, filtering, and management endpoints for role records.
- RoleAuthorityRules and RoleConsts centralize built-in role rules, constants, and authorization-related helper metadata.
- RoleEntityListener, RoleEventListener, and the event subpackage handle role lifecycle callbacks and create, update events.
- RoleInitializer and RolePermissions provide bootstrap role data and permission metadata.
