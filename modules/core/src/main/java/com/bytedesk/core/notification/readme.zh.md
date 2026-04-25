# notification

该包负责通知记录管理、投递状态跟踪与通知分发接口。

## 实现要点

- 核心模型包括 NotificationEntity、NotificationRequest、NotificationResponse、NotificationDispatchResponse、NotificationStatusEnum、NotificationTypeEnum、NotificationExcel。
- NotificationRepository、NotificationSpecification、NotificationRestController、NotificationRestService、NotificationService 提供持久化、条件过滤、分发编排与通知管理接口。
- NotificationProtobuf 提供通知载荷在传输或集成场景中的 protobuf 转换支持。
- NotificationEntityListener、NotificationEventListener 与 event 子包负责通知生命周期回调以及创建、更新、删除事件。
- NotificationInitializer 与 NotificationPermissions 提供通知管理所需的初始化数据和权限元数据。
