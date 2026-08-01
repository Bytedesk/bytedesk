# workflow_edge

该包负责工作流连线定义管理、路由条件建模、连线服务与流转生命周期接口。

## 实现要点

- 核心文件包括 WorkflowEdgeEntity、WorkflowEdgeRequest、WorkflowEdgeResponse、WorkflowEdgeExcel、WorkflowEdgeTypeEnum，用于流转定义与导入导出建模。
- WorkflowEdgeRepository、WorkflowEdgeSpecification、WorkflowEdgeRestController、WorkflowEdgeRestService、WorkflowEdgeService 提供持久化、条件过滤、REST 接口和连线级编排能力。
- WorkflowEdgeEntityListener、WorkflowEdgeEventListener 与 event 子包负责生命周期回调以及工作流连线的创建、更新、删除事件。
- WorkflowEdgeInitializer、WorkflowEdgePermissions、WorkflowEdgeTools、WorkflowEdgeConvert 提供初始化数据、权限元数据、工具逻辑与转换辅助能力。
