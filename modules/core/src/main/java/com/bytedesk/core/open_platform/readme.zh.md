# open_platform

该包负责开放平台应用记录管理、平台类型元数据与集成生命周期接口。

## 实现要点

- 核心模型包括 OpenPlatformEntity、OpenPlatformRequest、OpenPlatformResponse、OpenPlatformExcel、OpenPlatformTypeEnum。
- OpenPlatformRepository、OpenPlatformSpecification、OpenPlatformRestController、OpenPlatformRestService 提供持久化、条件过滤和平台记录管理接口。
- OpenPlatformInitData、OpenPlatformInitializer、OpenPlatformPermissions、OpenPlatformTools 提供初始化数据、权限元数据与共享辅助逻辑。
- OpenPlatformEntityListener、OpenPlatformEventListener 与 event 子包负责创建、更新、删除生命周期事件。
