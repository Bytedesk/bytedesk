# ByteDesk meet Module

会议模块 - 基于 Spring Boot 的企业级会议服务端实现

## 概述

本模块实现了完整的会议服务端功能，包括:

- 📹 音视频会议管理
- 📅 会议日程预约
- 👥 参会者管理
- 💬 实时信令通信 (WebSocket)
- 🔐 会议安全控制
- 📊 会议数据持久化

## 技术栈

- **Spring Boot 3.x** - 应用框架
- **Spring Data JPA** - 数据访问层
- **WebSocket (STOMP)** - 实时通信
- **MySQL/PostgreSQL** - 数据库
- **Lombok** - 简化代码
- **OpenAPI/Swagger** - API 文档

## 项目结构

```bash
modules/meet/
├── src/main/java/com/bytedesk/meet/
│   ├── entity/                    # 实体类
│   │   ├── meetEntity.java         # 会议实体
│   │   ├── ParticipantEntity.java        # 参会者实体
│   │   ├── ScheduleEntity.java           # 日程实体
│   │   └── RoomEntity.java               # 会议室实体
│   ├── repository/                # 数据访问层
│   │   ├── meetRepository.java
│   │   ├── ParticipantRepository.java
│   │   ├── ScheduleRepository.java
│   │   └── RoomRepository.java
│   ├── service/                   # 业务逻辑层
│   │   └── meetService.java
│   ├── controller/                # REST API控制器
│   │   └── meetRestController.java
│   ├── dto/                       # 数据传输对象
│   │   ├── request/               # 请求DTO
│   │   │   ├── meetCreateRequest.java
│   │   │   └── meetJoinRequest.java
│   │   └── response/              # 响应DTO
│   │       ├── meetResponse.java
│   │       └── ParticipantResponse.java
│   ├── config/                    # 配置类
│   │   └── meetWebSocketConfig.java
│   └── websocket/                 # WebSocket处理
│       └── (待实现)
└── pom.xml                        # Maven配置
```

## 数据库设计

### 会议表 (bytedesk_meet)

| 字段 | 类型 | 说明 |
|------|------|------|
| uuid | VARCHAR(64) | 会议唯一标识 |
| topic | VARCHAR(256) | 会议主题 |
| host_uid | VARCHAR(64) | 主持人用户ID |
| room_id | VARCHAR(64) | 会议室ID |
| password | VARCHAR(64) | 会议密码 |
| type | VARCHAR(32) | 会议类型 |
| status | VARCHAR(32) | 会议状态 |
| start_time | DATETIME | 开始时间 |
| end_time | DATETIME | 结束时间 |
| max_participants | INT | 最大参与者数 |
| current_participants | INT | 当前参与者数 |

### 参会者表 (bytedesk_meet_participant)

| 字段 | 类型 | 说明 |
|------|------|------|
| uuid | VARCHAR(64) | 参会者唯一标识 |
| meet_uid | VARCHAR(64) | 会议ID |
| user_uid | VARCHAR(64) | 用户ID |
| nickname | VARCHAR(128) | 昵称 |
| role | VARCHAR(32) | 角色 |
| status | VARCHAR(32) | 状态 |
| audio_enabled | BOOLEAN | 是否开启音频 |
| video_enabled | BOOLEAN | 是否开启视频 |
| join_time | DATETIME | 加入时间 |

### 日程表 (bytedesk_meet_schedule)

| 字段 | 类型 | 说明 |
|------|------|------|
| uuid | VARCHAR(64) | 日程唯一标识 |
| meet_uid | VARCHAR(64) | 会议ID |
| title | VARCHAR(256) | 标题 |
| start_time | DATETIME | 开始时间 |
| end_time | DATETIME | 结束时间 |
| repeat_type | VARCHAR(32) | 重复类型 |
| reminder_type | VARCHAR(32) | 提醒类型 |

## API 接口文档

### 基础路径

```bash
/api/v1/meet
```

### 1. 创建快速会议

```http
POST /api/v1/meet/create
Content-Type: application/json
X-User-Uid: user-uid

{
  "topic": "项目周会",
  "description": "讨论项目进度",
  "password": "123456",
  "duration": 60,
  "maxParticipants": 100,
  "muteOnEntry": false,
  "videoOnEntry": true
}
```

**响应**:

```json
{
  "uid": "conf-uuid",
  "topic": "项目周会",
  "roomId": "room-id",
  "status": "NOT_STARTED",
  "hostUid": "user-uid",
  "maxParticipants": 100,
  "currentParticipants": 0,
  "hasPassword": true,
  "wsUrl": "wss://ws.bytedesk.com/meet/conf-uuid"
}
```

### 2. 加入会议

```http
POST /api/v1/meet/join
Content-Type: application/json
X-User-Uid: user-uid

{
  "meetingId": "conf-uuid",
  "password": "123456",
  "nickname": "张三",
  "enableAudio": true,
  "enableVideo": true
}
```

### 3. 离开会议

```http
POST /api/v1/meet/{meetingId}/leave
X-User-Uid: user-uid
```

### 4. 结束会议

```http
POST /api/v1/meet/{meetingId}/end
X-User-Uid: host-uid
```

### 5. 获取会议信息

```http
GET /api/v1/meet/{meetingId}
```

### 6. 获取会议列表

```http
GET /api/v1/meet/list?page=1&pageSize=20
X-User-Uid: user-uid
```

## WebSocket 信令

### 连接端点

```
ws://localhost:9003/ws/meet
```

### 订阅主题

- `/topic/meet/{meetingId}/participant-joined` - 参会者加入
- `/topic/meet/{meetingId}/participant-left` - 参会者离开
- `/topic/meet/{meetingId}/ended` - 会议结束
- `/user/queue/meet` - 个人消息队列

### 发送消息

```javascript
// 加入房间
stompClient.send('/app/meet/join', {}, JSON.stringify({
  meetingId: 'conf-uuid',
  userUid: 'user-uuid'
}));

// WebRTC信令
stompClient.send('/app/meet/webrtc', {}, JSON.stringify({
  type: 'offer',
  meetingId: 'conf-uuid',
  sdp: '...'
}));
```

## 实体类说明

### meetEntity

会议实体，包含会议的所有信息:

- **基本信息**: 主题、描述、主持人
- **时间管理**: 开始时间、结束时间、时长
- **状态管理**: 状态(未开始/进行中/已结束/已取消)
- **安全控制**: 密码、锁定状态
- **容量管理**: 最大参与数、当前参与数
- **功能开关**: 静音、视频、等候室、录制等

### ParticipantEntity

参会者实体，管理会议参与者:

- **角色**: HOST(主持人), CO_HOST(联合主持), PRESENTER(演讲者), ATTENDEE(参会者)
- **状态**: ONLINE(在线), AWAY(离开), OFFLINE(离线), IN_LOBBY(等候室)
- **媒体控制**: 音频、视频、屏幕共享状态
- **时间记录**: 加入时间、离开时间

### ScheduleEntity

会议日程实体，管理预约会议:

- **重复规则**: 不重复、每天、每周、每月、自定义
- **提醒设置**: 5分钟、15分钟、30分钟、1小时、2小时、1天
- **时间管理**: 开始时间、结束时间、重复结束日期

## 数据库索引

### meetEntity

- `idx_meet_uid` - UUID索引
- `idx_meet_host_uid` - 主持人索引
- `idx_meet_room_id` - 会议室ID索引
- `idx_meet_status` - 状态索引
- `idx_meet_start_time` - 开始时间索引

### ParticipantEntity

- `idx_participant_uid` - UUID索引
- `idx_participant_meet_uid` - 会议ID索引
- `idx_participant_user_uid` - 用户ID索引
- `idx_participant_role` - 角色索引

## 业务流程

### 创建会议流程

```
客户端请求 -> meetRestController
  -> meetService.createQuickMeeting()
    -> meetRepository.save()
    -> 返回会议信息（含WebSocket URL）
```

### 加入会议流程

```
客户端请求 -> meetRestController
  -> meetService.joinMeeting()
    -> 验证密码
    -> 检查容量
    -> 创建Participant记录
    -> 更新会议状态
    -> 发送WebSocket通知
    -> 返回会议信息
```

### 结束会议流程

```
主持人请求 -> meetRestController
  -> meetService.endMeeting()
    -> 验证权限
    -> 更新会议状态为ENDED
    -> 更新所有参与者状态为OFFLINE
    -> 发送WebSocket通知
```

## 使用示例

### 1. 创建会议

```java
meetCreateRequest request = new meetCreateRequest();
request.setTopic("周会");
request.setDuration(60);
request.setMaxParticipants(50);

meetResponse response = meetService.createQuickMeeting(request, "user-uid");
System.out.println("Meeting created: " + response.getUid());
```

### 2. 查询会议

```java
meetResponse response = meetService.getMeetingInfo("conf-uuid");
System.out.println("Meeting status: " + response.getStatus());
System.out.println("Participants: " + response.getCurrentParticipants());
```

### 3. 获取参与者列表

```java
List<ParticipantEntity> participants =
    participantRepository.findBymeetUidOrderByJoinTimeAsc("conf-uuid");
```

## 配置说明

### application.properties

```properties
# WebSocket配置
websocket.endpoint=/ws/meet
websocket.allowed-origins=*

# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/bytedesk
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# 文件上传配置
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB
```

## 已实现功能

### ✅ 核心功能

- [x] 会议实体设计
- [x] 参会者实体设计
- [x] 日程实体设计
- [x] Repository 层实现
- [x] Service 业务逻辑
- [x] REST API 接口
- [x] WebSocket 配置
- [x] DTO 对象

### 🚧 待实现功能

- [ ] WebRTC 信令处理
- [ ] 屏幕共享管理
- [ ] 会议录制服务
- [ ] 文件传输
- [ ] 会议统计
- [ ] 实时字幕
- [ ] 候选会议者管理
- [ ] 聊天消息存储

## 开发指南

### 1. 添加新的API端点

```java
@PostMapping("/new-endpoint")
@Operation(summary = "新端点", description = "描述")
public ResponseEntity<?> newEndpoint() {
    // 实现逻辑
    return ResponseEntity.ok().build();
}
```

### 2. 添加WebSocket处理

```java
@MessageMapping("/meet/webrtc")
public void handleWebRTCMessage(WebrtcMessage message) {
    // 处理WebRTC信令
    messagingTemplate.convertAndSend(
        "/topic/meet/" + message.getMeetingId() + "/webrtc",
        message
    );
}
```

### 3. 扩展实体

1. 在 Entity 类中添加字段
2. 添加对应的索引
3. 在 Repository 中添加查询方法
4. 在 Service 中实现业务逻辑
5. 在 DTO 中添加字段

## 测试

### 单元测试

```bash
mvn test -Dtest=meetServiceTest
```

### 集成测试

```bash
mvn verify
```

### API 测试

使用 Swagger UI: `http://localhost:9003/swagger-ui.html`

## 性能优化

### 数据库优化

- 使用索引加速查询
- 批量操作减少数据库访问
- 使用缓存减少重复查询

### WebSocket优化

- 心跳保持连接
- 消息队列处理高并发
- 连接池管理

## 安全性

### 认证授权

- JWT Token 认证
- 基于角色的访问控制
- 会议密码保护

### 数据安全

- HTTPS 加密传输
- WSS 加密WebSocket
- 数据脱敏处理

## 监控与日志

### 日志级别

- ERROR: 错误日志
- WARN: 警告日志
- INFO: 关键操作日志
- DEBUG: 调试日志

### 监控指标

- 会议创建数量
- 在线会议数
- 参与者数量
- 系统资源使用

## 后续计划

### Phase 1 (当前)

- ✅ 基础实体和Repository
- ✅ 核心业务逻辑
- ✅ REST API
- ✅ WebSocket配置

### Phase 2 (下一阶段)

- [ ] WebRTC 信令处理
- [ ] 屏幕共享功能
- [ ] 会议录制
- [ ] 文件传输

### Phase 3 (未来)

- [ ] AI 实时字幕
- [ ] 会议统计分析
- [ ] 虚拟背景
- [ ] 白板协作

## 问题反馈

如有问题或建议，请联系:

- Email: <support@bytedesk.com>
- GitHub Issues: <https://github.com/Bytedesk/bytedesk/issues>

## 许可证

Business Source License 1.1 - 仅可用于内部使用，禁止转售或作为SaaS服务提供。

---

**当前版本**: 1.0.0
**最后更新**: 2025-01-16
**维护者**: ByteDesk Team
