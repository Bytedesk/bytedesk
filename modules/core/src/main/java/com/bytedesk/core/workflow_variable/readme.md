# workflow_variable

This package manages workflow variables, variable scopes and types, and value access across workflow steps.

## Implementation Notes

- Core files include WorkflowVariableEntity, WorkflowVariableRequest, WorkflowVariableResponse, WorkflowVariableScopeEnum, and WorkflowVariableTypeEnum for variable data and metadata modeling.
- WorkflowVariableRepository and WorkflowVariableService provide persistence and runtime read-write operations for workflow variables.
- WorkflowVariableController exposes management and lookup endpoints for variable access from workflow-related callers.
