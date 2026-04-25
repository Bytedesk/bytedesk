# workflow_log

This package manages workflow audit logs, execution traces, and workflow log lifecycle APIs.

## Implementation Notes

- Core files include WorkflowLogEntity, WorkflowLogRequest, WorkflowLogResponse, WorkflowLogExcel, and WorkflowLogTypeEnum for execution trace and audit record modeling.
- WorkflowLogRepository, WorkflowLogSpecification, WorkflowLogRestController, and WorkflowLogRestService provide persistence, filtering, and management endpoints for workflow logs.
- WorkflowLogEntityListener, WorkflowLogEventListener, and the event subpackage handle log lifecycle callbacks and create, update, delete events.
- WorkflowLogInitializer, WorkflowLogPermissions, and WorkflowLogTools provide bootstrap data, permission metadata, and shared helper logic.
