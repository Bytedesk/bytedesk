# workflow_settings

This package manages workflow settings definitions, default configuration data, and settings lifecycle APIs.

## Implementation Notes

- Core files include WorkflowSettingsEntity, WorkflowSettingsRequest, WorkflowSettingsResponse, WorkflowSettingsExcel, WorkflowSettingsTypeEnum, and WorkflowIframeTab for settings modeling and UI-related configuration values.
- WorkflowSettingsRepository, WorkflowSettingsSpecification, WorkflowSettingsRestController, and WorkflowSettingsRestService provide persistence, filtering, and management endpoints for workflow settings.
- WorkflowSettingsEntityListener, WorkflowSettingsEventListener, and the event subpackage handle settings lifecycle callbacks and create, update, delete events.
- WorkflowSettingsInitData, WorkflowSettingsInitializer, WorkflowSettingsPermissions, and WorkflowSettingsTools provide default configuration bootstrap, permission metadata, and helper logic.
