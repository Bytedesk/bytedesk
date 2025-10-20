# 内嵌 FreeSWITCH ESL 客户端（bytedesk-call.esl）

本目录为 FreeSWITCH ESL（Event Socket Library）客户端的内嵌实现代码。

为什么内嵌？
- 上游 `org.freeswitch.esl.client` 长期未更新，存在兼容性与维护风险；
- 降低外部依赖的不确定性，便于按需修复、优化与扩展；
- 参考了社区实现的设计思路（如 esl-client、thingscloud/freeswitch-esl 等），具体差异以源文件头部注释为准。

主要能力
- Inbound/Outbound 连接管理：`client.inbound`、`outbound`
- ESL 消息编解码与命令发送：`transport.message`、`transport.SendMsg`
- 事件解析与分发：`transport.event.EslEvent`，监听接口 `client.inbound.IEslEventListener`
- 基于 Netty/Apache MINA 的网络与协议处理

快速开始（Inbound）
1) 创建并连接客户端（示例见 `CallConfig`）：
	- 使用 `Client#connect(InetSocketAddress addr, String password, int timeoutSeconds)` 建立连接
	- 连接成功后调用 `setEventSubscriptions(IModEslApi.EventFormat.PLAIN, "all")` 订阅事件
2) 注册事件监听器：
	- 实现 `IEslEventListener#onEslEvent(Context, EslEvent)`
	- 根据 `EslEvent#getEventName()` 分发处理 `CHANNEL_CREATE/ANSWER/HANGUP/DTMF` 等
3) 发送命令：
	- 同步 API：`Client#sendApiCommand(String command, String arg)`
	- 发送到通道：构造 `SendMsg` 并调用 `Client#sendMessage(SendMsg)`

常用 API 速查
- `originate` 发起呼叫：bgapi/api 命令或使用 `SendMsg` 执行 `bridge` 等应用
- `uuid_answer` 应答、`uuid_kill` 挂断、`uuid_transfer` 转接
- `uuid_broadcast` 播放、`uuid_record start|stop` 录音控制
- `uuid_getvar/uuid_setvar` 读写通道变量、`uuid_send_dtmf` 发送 DTMF

事件处理要点
- 订阅 `plain all` 或按需过滤（`addEventFilter`）
- 常见事件：`CHANNEL_CREATE`、`CHANNEL_ANSWER`、`CHANNEL_HANGUP`、`DTMF`
- 事件头通过 `EslEvent#getEventHeaders()` 获取，如 `Unique-ID`、`Caller-Caller-ID-Number`、`Caller-Destination-Number` 等

兼容性
- JDK 17 编译与运行；
- 面向 FreeSWITCH 1.10+，若协议字段变更，请在本包内适配。

许可与版权
- 遵循上游项目开源许可；如与本仓库 LICENSE 存在差异，以各源文件声明为准；
- 原作者与社区项目保留各自版权；如有疑义请联系维护者。

注意
- 资源管理：在应用关闭时请正确关闭连接，避免资源泄露；
- 错误处理：`Throwables.propagate` 等已弃用的用法已替换为标准 `RuntimeException` 包装。
