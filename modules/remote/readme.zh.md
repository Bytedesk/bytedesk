<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-01-29 16:24:13
 * @LastEditors: jack ning github@bytedesk.com
 * @LastEditTime: 2025-02-06 17:44:26
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# 远程协助

MVP 使用 P2P WebRTC 传屏、可靠有序 DataChannel 传键鼠输入，客服 Web 主控、访客 Electron 被控。后端提供绑定客服 Thread 的单次邀请、会话生命周期、权限/心跳、临时 SDP/ICE、TURN 凭证和关键事件审计；复用 Room/Participant 元数据，不依赖 Janus 媒体房间。

- 使用、配置与验证：[客户端说明](../../frontend/apps/remote/README.md)
- 接口、边界及后续阶段：[规划与实施记录](../../docs/plans/2026-11-12-remote-desktop-control-plan.md)
- 真实操作系统输入、完整后端联调及跨网络/跨平台验收尚未完成；旧 STOMP 转发骨架不属于活动控制链路。

## 包组织

参考 `modules/core` 按功能聚合，而不是按 `entity/repository/service` 技术分层。同一功能的实体、仓库、服务、控制器、请求/响应 DTO 和枚举放在同一个包：

- [`device`](src/main/java/com/bytedesk/remote/device/)：设备实体、仓库、注册请求及设备管理骨架。
- [`session`](src/main/java/com/bytedesk/remote/session/)：会话生命周期、参与方接口、信令/ICE、请求/响应和会话枚举。
- [`audit`](src/main/java/com/bytedesk/remote/audit/)：审计实体、仓库及审计服务骨架。
- `config`、`controller`、`protocol`、`websocket`：保留模块健康配置、前端页面路由与遗留传输协议；WebSocket 配置与控制器放在一起。

测试使用对应功能包，跨功能的数据库映射测试位于模块根测试包。本次只调整组织及引用，不修改 HTTP 路径、载荷、实体/表名或 Liquibase 迁移；注释中的骨架仍不启用。
