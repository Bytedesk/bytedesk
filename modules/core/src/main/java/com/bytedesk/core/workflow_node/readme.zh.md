# workflow_node

该包负责工作流节点定义管理、节点类型建模、节点服务与节点生命周期接口。

## 实现要点

- 核心文件包括 WorkflowNodeEntity、WorkflowNodeRequest、WorkflowNodeResponse、WorkflowNodeExcel、WorkflowNodeTypeEnum，用于节点定义与导入导出建模。
- WorkflowNodeRepository、WorkflowNodeSpecification、WorkflowNodeRestController、WorkflowNodeRestService、WorkflowNodeService 提供持久化、条件过滤、REST 接口和节点级编排能力。
- WorkflowNodeEntityListener、WorkflowNodeEventListener 与 event 子包负责节点生命周期回调以及创建、更新、删除事件。
- WorkflowNodeInitializer、WorkflowNodePermissions、WorkflowNodeTools、WorkflowNodeConvert、WorkflowNodeUsageExample 提供初始化数据、权限元数据、转换辅助、工具逻辑和示例用法。
