# workflow

该包负责工作流定义管理、执行上下文维护、Schema 编译与运行时编排接口。

## 实现要点

- 核心模型包括 WorkflowEntity、WorkflowRequest、WorkflowResponse、WorkflowSchema、WorkflowExecutionContext、WorkflowNodeExecutionResult、WorkflowTypeEnum 和 WorkflowStatusEnum。
- WorkflowRepository、WorkflowSpecification、WorkflowRestController、WorkflowRestService、WorkflowService 负责持久化、条件过滤、REST 接口和工作流执行编排。
- WorkflowEntityListener、WorkflowEventListener 以及 event 子包提供生命周期回调与工作流创建、更新、删除事件。
- WorkflowInitializer、WorkflowInitData、WorkflowPermissions、WorkflowConst、WorkflowUtils 负责初始化数据、权限元数据、共享常量和通用辅助逻辑。
- compiler、node、edge 子包拆分了承载编译、节点处理与连线处理等专门能力。
