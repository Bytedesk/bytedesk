# server

This package manages server-node records, server status metadata, and server lifecycle APIs.

## Implementation Notes

- Core models include ServerEntity, ServerRequest, ServerResponse, ServerExcel, ServerStatusEnum, and ServerTypeEnum.
- ServerRepository, ServerSpecification, ServerRestController, and ServerRestService provide persistence, filtering, and management endpoints for server-node records.
- ServerInitializer and ServerPermissions provide bootstrap data and permission metadata.
- ServerEntityListener, ServerEventListener, and the event subpackage handle create, update, and delete lifecycle events.
