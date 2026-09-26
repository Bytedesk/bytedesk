# push

该包负责推送记录管理、多通道推送路由与推送执行接口。

## 实现要点

- 核心模型包括 PushEntity、PushRequest、PushResponse、PushStatusEnum。
- PushRepository、PushSpecification、PushRestController、PushRestService、PushService 提供持久化、条件过滤和推送管理接口。
- PushFilterService 与 PushExpireCacheService 负责路由前置过滤、条件处理和过期推送数据管理。
- PushPermissions 与 PushEventListener 提供权限元数据和事件侧集成。
- service 子包包含 APNs、华为、小米、Web、Email 等渠道发送服务及通用发送结果抽象；strategy 子包封装认证校验策略。
- 设备推送绑定（push_android_device）、APNs 推送与记录（push_apns、push_apns_p12、push_apns_token）、Android 推送记录（push_android）、推送凭据下发（push_config）与推送设置（push_settings）已迁至 `enterprise/core`（com.bytedesk.enterprise.core.push_*）。
