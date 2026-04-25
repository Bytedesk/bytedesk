# server

该包负责服务节点记录管理、服务状态元数据与服务生命周期接口。

## 实现要点

- 核心模型包括 ServerEntity、ServerRequest、ServerResponse、ServerExcel、ServerStatusEnum、ServerTypeEnum。
- ServerRepository、ServerSpecification、ServerRestController、ServerRestService 提供持久化、条件过滤和服务节点记录管理接口。
- ServerInitializer 与 ServerPermissions 提供初始化数据和权限元数据。
- ServerEntityListener、ServerEventListener 与 event 子包负责创建、更新、删除生命周期事件。
