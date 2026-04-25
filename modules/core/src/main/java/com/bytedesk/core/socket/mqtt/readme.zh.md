# mqtt

该包负责 MQTT 运行时配置、会话处理与 MQTT 传输生命周期集成。

## 实现要点

- MqttProperties、MqttConsts、MqttSession、MqttChannelUtils、MqttUtils 提供运行属性、常量、会话状态、通道辅助与工具逻辑。
- MqttRestController 暴露面向传输管理或集成的接口入口。
- event 子包与 MqttEventPublisher 负责 connected、disconnected、subscribe、unsubscribe 事件发布。
- handler、initializer、listener、protocol、server、service 子包拆分了承载协议处理与服务端运行职责。
