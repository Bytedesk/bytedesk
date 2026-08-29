# connection

该包负责连接记录管理、在线状态同步与连接生命周期接口。

## 实现要点

- 核心模型包括 ConnectionEntity、ConnectionRequest、ConnectionResponse、ConnectionExcel、PresenceResponse、ConnectionStatusEnum、ConnectionProtocalEnum。
- ConnectionRepository、ConnectionSpecification、ConnectionRestController、ConnectionRestService 提供持久化、条件过滤和连接记录管理接口。
- ConnectionHeartbeatFlushTask、ConnectionMetrics、PresenceTtlResolver 负责心跳刷新、指标采集和在线状态过期处理。
- ConnectionInitializer、ConnectionPermissions、ConnectionTools、ConnectionEntityListener、ConnectionEventListener 与 event 子包提供初始化数据、权限元数据、辅助逻辑以及创建、更新、删除生命周期事件。
