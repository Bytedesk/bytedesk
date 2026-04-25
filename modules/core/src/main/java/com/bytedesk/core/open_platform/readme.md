# open_platform

This package manages open-platform application records, platform type metadata, and integration lifecycle APIs.

## Implementation Notes

- Core models include OpenPlatformEntity, OpenPlatformRequest, OpenPlatformResponse, OpenPlatformExcel, and OpenPlatformTypeEnum.
- OpenPlatformRepository, OpenPlatformSpecification, OpenPlatformRestController, and OpenPlatformRestService provide persistence, filtering, and management endpoints for platform records.
- OpenPlatformInitData, OpenPlatformInitializer, OpenPlatformPermissions, and OpenPlatformTools provide bootstrap data, permission metadata, and shared helper logic.
- OpenPlatformEntityListener, OpenPlatformEventListener, and the event subpackage handle create, update, and delete lifecycle events.
