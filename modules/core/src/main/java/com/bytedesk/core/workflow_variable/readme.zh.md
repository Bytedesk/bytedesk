# workflow_variable

该包负责工作流变量管理、变量作用域与类型建模，以及流程步骤间的取值写值。

## 实现要点

- 核心文件包括 WorkflowVariableEntity、WorkflowVariableRequest、WorkflowVariableResponse、WorkflowVariableScopeEnum、WorkflowVariableTypeEnum，用于变量数据与元数据建模。
- WorkflowVariableRepository 与 WorkflowVariableService 提供持久化及工作流变量运行时读写能力。
- WorkflowVariableController 对外暴露变量管理与查询接口，供工作流相关调用方访问。
