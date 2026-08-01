# socket

该包负责协调连接在线状态、MQTT 传输与 STOMP over WebSocket 的实时消息运行时。

## 实现要点

- connection 子包负责连接注册、在线状态同步、心跳刷新、指标采集和在线 TTL 解析。
- mqtt 子包包含协议常量与属性、会话与通道工具、REST 入口，以及 server、service、handler、initializer、listener 等运行支持。
- stomp 子包包含 STOMP 配置、控制器、处理器、拦截器、监听器以及 WebSocket 消息事件集成。
