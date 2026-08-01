# stomp

该包负责 STOMP over WebSocket 配置、消息拦截与 STOMP 传输生命周期集成。

## 实现要点

- StompConfig 与 StompController 定义 STOMP 运行装配和控制器入口。
- event 子包与 StompEventPublisher 负责 connected、disconnected、subscribe、unsubscribe 事件发布。
- handler、interceptor、listener 子包拆分了承载消息处理、通道拦截和运行时监听职责。
