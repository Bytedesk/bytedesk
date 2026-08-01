# workflow

This package manages workflow definitions, execution context, schema compilation, and runtime orchestration APIs.

## Implementation Notes

- Core workflow models include WorkflowEntity, WorkflowRequest, WorkflowResponse, WorkflowSchema, WorkflowExecutionContext, WorkflowNodeExecutionResult, WorkflowTypeEnum, and WorkflowStatusEnum.
- WorkflowRepository, WorkflowSpecification, WorkflowRestController, WorkflowRestService, and WorkflowService handle persistence, filtering, REST endpoints, and workflow execution orchestration.
- WorkflowEntityListener, WorkflowEventListener, and the event subpackage provide lifecycle callbacks and workflow create, update, and delete domain events.
- WorkflowInitializer, WorkflowInitData, WorkflowPermissions, WorkflowConst, and WorkflowUtils cover bootstrap data, permission metadata, shared constants, and helper logic.
- Subpackages split specialized capabilities into compiler, node, and edge processing.
