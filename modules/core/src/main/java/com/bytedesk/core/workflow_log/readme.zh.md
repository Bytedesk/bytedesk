# workflow_log

该包负责工作流审计日志管理、执行轨迹跟踪与工作流日志生命周期接口。

## 实现要点

- 核心文件包括 WorkflowLogEntity、WorkflowLogRequest、WorkflowLogResponse、WorkflowLogExcel、WorkflowLogTypeEnum，用于执行轨迹与审计记录建模。
- WorkflowLogRepository、WorkflowLogSpecification、WorkflowLogRestController、WorkflowLogRestService 提供持久化、条件过滤和工作流日志管理接口。
- WorkflowLogEntityListener、WorkflowLogEventListener 与 event 子包负责日志生命周期回调以及创建、更新、删除事件。
- WorkflowLogInitializer、WorkflowLogPermissions、WorkflowLogTools 提供初始化数据、权限元数据与共享辅助逻辑。
