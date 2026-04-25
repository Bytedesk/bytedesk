# workflow_node

This package manages workflow node definitions, node types, node services, and node lifecycle APIs.

## Implementation Notes

- Core files include WorkflowNodeEntity, WorkflowNodeRequest, WorkflowNodeResponse, WorkflowNodeExcel, and WorkflowNodeTypeEnum for node definition and import-export modeling.
- WorkflowNodeRepository, WorkflowNodeSpecification, WorkflowNodeRestController, WorkflowNodeRestService, and WorkflowNodeService provide persistence, filtering, REST endpoints, and node-level orchestration.
- WorkflowNodeEntityListener, WorkflowNodeEventListener, and the event subpackage handle node lifecycle callbacks and create, update, delete events.
- WorkflowNodeInitializer, WorkflowNodePermissions, WorkflowNodeTools, WorkflowNodeConvert, and WorkflowNodeUsageExample provide bootstrap data, permission metadata, conversion helpers, utility logic, and sample usage.
