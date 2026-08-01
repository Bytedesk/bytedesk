# push

该包负责推送记录管理、多通道推送路由与推送执行接口。

## 实现要点

- 核心模型包括 PushEntity、PushTokenEntity、PushRequest、PushResponse、PushStatusEnum。
- PushRepository、PushSpecification、PushRestController、PushRestService、PushService 提供持久化、条件过滤和推送管理接口。
- PushFilterService 与 PushExpireCacheService 负责路由前置过滤、条件处理和过期推送数据管理。
- PushPermissions 与 PushEventListener 提供权限元数据和事件侧集成。
- service 子包包含 APNs、华为、小米、Web、Email 等渠道发送服务及通用发送结果抽象；strategy 子包封装认证校验策略。
