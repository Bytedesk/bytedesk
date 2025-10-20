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

## 对外 REST API（bytedesk-call.esl）

在同目录提供了基于 Spring 的对外接口封装：
- `EslService`：封装常见 ESL 命令（api/bgapi、reloadxml/acl、status/show、sofia、originate、uuid_*、conference 等）
- `EslController`：暴露 REST 接口，前缀 `/freeswitch/api/v1/esl`
 - `xmlcurl/XmlCurlController` + `XmlCurlService`：提供最小可用的 mod_xml_curl HTTP 服务端（演示版）

启用条件：
- ESL：`bytedesk.call.freeswitch.enabled=true`，`CallConfig` 将建立 Inbound 连接并订阅事件
- XML-Curl：`bytedesk.call.freeswitch.xmlcurl.enabled=true` 开启 HTTP 服务端

常用端点：
- GET `/freeswitch/api/v1/esl/status`：等价 `status`
- POST `/freeswitch/api/v1/esl/api`：通用 API，Body: `{ "command":"reloadxml", "args":"" }`
- POST `/freeswitch/api/v1/esl/bgapi`：通用 BGAPI，返回 BACKGROUND_JOB 事件数据
- POST `/freeswitch/api/v1/esl/reloadxml`、`/reloadacl`
- GET `/freeswitch/api/v1/esl/show/{what}`：`what` 取值如 `channels/calls/registrations`
- GET `/freeswitch/api/v1/esl/sofia/status`
- POST `/freeswitch/api/v1/esl/sofia/profile/{profile}/{action}`：`action` 如 `rescan/restart/start/stop`
- POST `/freeswitch/api/v1/esl/xml_flush_cache`：清空 XML 缓存（常与 xml_curl 配合）
- 呼叫控制：
	- POST `/freeswitch/api/v1/esl/originate` Body: `{ "args":"{ignore_early_media=true}sofia/gateway/gw/1001 &park" }`
	- POST `/freeswitch/api/v1/esl/uuid/answer/{uuid}`
	- POST `/freeswitch/api/v1/esl/uuid/kill/{uuid}?cause=USER_BUSY`
	- POST `/freeswitch/api/v1/esl/uuid/transfer` Body: `{ "uuid":"...", "dest":"1000", "dialplan":"XML", "context":"default", "leg":"-bleg" }`
	- POST `/freeswitch/api/v1/esl/uuid/bridge` Body: `{ "uuidA":"...", "uuidB":"..." }`
	- POST `/freeswitch/api/v1/esl/uuid/broadcast` Body: `{ "uuid":"...", "file":"/path/file.wav", "legs":"both" }`
	- POST `/freeswitch/api/v1/esl/uuid/record` Body: `{ "uuid":"...", "action":"start|stop", "path":"/path/rec.wav" }`
	- POST `/freeswitch/api/v1/esl/uuid/setvar` Body: `{ "uuid":"...", "var":"X", "value":"Y" }`
	- GET `/freeswitch/api/v1/esl/uuid/getvar?uuid=...&var=...`
	- POST `/freeswitch/api/v1/esl/uuid/dtmf` Body: `{ "uuid":"...", "dtmf":"1234#" }`
- 会议控制：
	- POST `/freeswitch/api/v1/esl/conference` Body: `{ "room":"9000", "subCommand":"list" }`
	- 例如踢人：`{ "room":"9000", "subCommand":"kick", "args":"<uuid>" }`

返回结果说明：
- 通用 API 返回 `{ ok, replyText, contentType, body }`，便于日志与界面展示
- BGAPI 返回 `{ eventName, headers, bodyLines }`

注意事项：
- ESL 无法直接编辑磁盘文件；动态配置需由应用/运维写入 XML 后，再调用 `reloadxml`
- SIP Profile 变更通常需 `sofia profile <name> rescan`，必要时 `restart`（可能影响在呼通话）
- 部分变更仅对新通话生效；生产环境操作需审慎

## mod_xml_curl 最小可用方案（演示）

服务端（本项目）：
- 开启 `bytedesk.call.freeswitch.xmlcurl.enabled=true` 后，提供 HTTP 端点：
	- GET `/freeswitch/xmlcurl?type=directory&user=1000&domain=default`
	- GET `/freeswitch/xmlcurl?type=dialplan&context=default&dest=1000`
	- 返回 `application/xml`，示例实现见 `XmlCurlService`，生产应改为从数据库/配置中心读取

FreeSWITCH 侧配置要点（参考）：
- 启用 `mod_xml_curl`，并在 `xml_curl.conf.xml` 设置对应 URL（建议内网/HTTPS + 鉴权）
- 可启用缓存提升性能；变更后通过 `fsctl xml_flush_cache` 或上述 API 端点刷新缓存
- 保留基础配置文件作为兜底，动态内容由 xml_curl 提供

安全建议：
- 将 xml_curl 服务端限于内网，开启身份校验/白名单/签名校验
- 对请求与返回做审计与速率限制，防止滥用或失效导致拨号失败

## 接入 mod_xml_cdr（CDR 后台沉淀）

本模块提供了接收 XML CDR 的接口：
- POST `/freeswitch/cdr/xml`（Content-Type: application/xml 或 text/xml）
- 控制器：`com.bytedesk.call.cdr.XmlCdrController`
- 解析器：`com.bytedesk.call.cdr.XmlCdrParser`（支持 epoch 优先、常见字段映射到 `CallCdrEntity`）

FreeSWITCH 配置要点（参考）：
- 启用 `mod_xml_cdr`
- 配置写文件或 HTTP POST 到上述接口（建议内网/HTTPS + 鉴权）
- 建议开启重试/幂等（以 UUID 为幂等键），保障投递可靠

注意：
- 生产环境务必加鉴权、IP 白名单、速率限制与审计；确保存储脱敏与合规
- 若先落地文件再由 Agent 推送，可保持 FS 简洁并提高可观测性
