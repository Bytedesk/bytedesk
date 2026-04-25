# workflow_edge

This package manages workflow edge definitions, routing conditions, edge services, and transition lifecycle APIs.

## Implementation Notes

- Core files include WorkflowEdgeEntity, WorkflowEdgeRequest, WorkflowEdgeResponse, WorkflowEdgeExcel, and WorkflowEdgeTypeEnum for transition definition and import-export modeling.
- WorkflowEdgeRepository, WorkflowEdgeSpecification, WorkflowEdgeRestController, WorkflowEdgeRestService, and WorkflowEdgeService provide persistence, filtering, REST endpoints, and edge-level orchestration.
- WorkflowEdgeEntityListener, WorkflowEdgeEventListener, and the event subpackage handle lifecycle callbacks and create, update, delete events for workflow transitions.
- WorkflowEdgeInitializer, WorkflowEdgePermissions, WorkflowEdgeTools, and WorkflowEdgeConvert provide bootstrap data, permission metadata, utility logic, and conversion helpers.
