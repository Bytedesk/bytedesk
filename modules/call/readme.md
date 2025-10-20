<!--
 * @Author: jackning 270580156@qq.com
 * @Date: 2024-09-11 10:22:00
 * @LastEditors: jackning 270580156@qq.com
 * @LastEditTime: 2025-06-07 19:49:53
 * @Description: bytedesk.com https://github.com/Bytedesk/bytedesk
 *   Please be aware of the BSL license restrictions before installing Bytedesk IM – 
 *  selling, reselling, or hosting Bytedesk IM as a service is a breach of the terms and automatically terminates your rights under the license.
 *  Business Source License 1.1: https://github.com/Bytedesk/bytedesk/blob/main/LICENSE 
 *  contact: 270580156@qq.com 
 *  联系：270580156@qq.com
 * Copyright (c) 2024 by bytedesk.com, All Rights Reserved. 
-->
# FreeSwitch Integration Module for Bytedesk

基于 Spring Boot 的 FreeSwitch 集成模块，支持语音通话、WebRTC、呼叫中心功能和事件监听。

## ⚠️ ESL连接问题修复 (2025-06-08)

如果遇到 `text/rude-rejection` 或 `Access Denied` 错误，请查看详细修复指南：

📋 **[FreeSwitch ESL 连接问题修复指南](FREESWITCH_ESL_FIX_GUIDE.md)**

### 快速修复

1. **检查连接状态** - 访问健康检查端点：

   ```bash
   curl http://localhost:9003/actuator/health/freeSwitch
   ```

2. **测试连接** - 使用管理API：

   ```bash
   curl -X POST http://localhost:9003/api/v1/freeswitch/test-connection
   ```

3. **查看配置** - 获取当前配置：

   ```bash
   curl http://localhost:9003/api/v1/freeswitch/config
   ```

### 新增功能

- ✅ **智能错误诊断**: 自动识别连接问题并提供解决建议
- ✅ **连接重试机制**: 指数退避重试策略
- ✅ **健康状态监控**: Spring Boot Actuator集成
- ✅ **管理REST API**: 连接测试和状态查询接口
- ✅ **启动时连接测试**: 应用启动时自动检测FreeSwitch连接

## 功能特性

- ✅ **FreeSwitch ESL 集成**: 通过 Event Socket Library 连接和控制 FreeSwitch
- ✅ **呼叫控制**: 呼叫发起、挂断、转接、保持等基本功能
- ✅ **WebRTC 支持**: 基于 Verto 协议的 WebRTC 音视频通话
- ✅ **实时事件监听**: 监听 FreeSwitch 事件并转发到 Spring Boot 应用
- ✅ **呼叫中心功能**: 队列管理、坐席管理、通话录音
- ✅ **REST API**: 完整的 HTTP 接口用于呼叫控制和状态查询
- ✅ **WebSocket 信令**: 支持 WebRTC 信令交换和实时消息推送
- ✅ **mod_xml_curl**: 通过 HTTP 动态返回 Directory 和 Dialplan XML（可选开启）

## 项目结构

```bash
plugins/freeswitch/
├── pom.xml                                    # Maven 配置文件
├── readme.md                                  # 项目文档
└── src/main/java/com/bytedesk/freeswitch/
    ├── freeswitch/
    │   ├── FreeSwitchService.java            # FreeSwitch 核心服务
    │   ├── FreeSwitchController.java         # REST API 控制器
    │   └── WebSocketConfig.java              # WebSocket 配置
    ├── webrtc/
    │   └── WebRTCSignalingController.java    # WebRTC 信令控制器
    ├── callcenter/
    │   └── CallService.java                  # 呼叫服务
    └── resources/
        └── static/
            └── webrtc-demo.html              # WebRTC 测试页面
```

## 环境要求

- Java 17+
- Spring Boot 3.x
- FreeSwitch 1.10+
- Maven 3.6+

## 快速开始

### 1. 安装 FreeSwitch

```bash
# macOS
brew install freeswitch

# Ubuntu/Debian
apt-get update
apt-get install freeswitch

# CentOS/RHEL
yum install freeswitch
```

### 2. 配置 FreeSwitch

将项目提供的配置文件复制到 FreeSwitch 配置目录：

```bash
# 复制配置文件
cp deploy/private/freeswitch/conf/vars_bytedesk.xml /usr/local/freeswitch/conf/
cp deploy/private/freeswitch/conf/directory/bytedesk.xml /usr/local/freeswitch/conf/directory/
cp deploy/private/freeswitch/conf/autoload_configs/event_socket.conf.xml /usr/local/freeswitch/conf/autoload_configs/
cp deploy/private/freeswitch/conf/autoload_configs/verto_bytedesk.conf.xml /usr/local/freeswitch/conf/autoload_configs/
cp deploy/freeswitch/conf/autoload_configs/xml_curl.conf.xml /usr/local/freeswitch/conf/autoload_configs/
```

### 3. 启动 FreeSwitch

```bash
# 启动 FreeSwitch
freeswitch -nonat

# 或者作为后台服务启动
freeswitch -nc -nonat
```

### 4. 配置 Spring Boot

在 `application-local.properties` 中配置 FreeSwitch 连接参数：

```properties
# FreeSwitch ESL 配置
freeswitch.esl.host=127.0.0.1
freeswitch.esl.port=8021
freeswitch.esl.password=bytedesk123
freeswitch.esl.timeout=10000

# WebRTC 配置
freeswitch.webrtc.ws.url=ws://127.0.0.1:15066
freeswitch.webrtc.wss.url=wss://127.0.0.1:17443

# 呼叫中心配置
freeswitch.callcenter.queue.default=bytedesk_queue
freeswitch.callcenter.recording.enabled=true

# 可选：启用 xml_curl
bytedesk.call.freeswitch.xmlcurl.enabled=true
# 建议设置访问 token
bytedesk.call.freeswitch.xmlcurl.token=change_me
# 允许来源 IP（可选）
bytedesk.call.freeswitch.xmlcurl.ip-whitelist=127.0.0.1,::1
```

### 5. 启动应用

```bash
cd plugins/freeswitch
mvn spring-boot:run
```

## API 使用示例
## xml_curl 使用指南

应用提供固定端点：/freeswitch/xmlcurl

1) Directory 示例（GET）

可用于 FreeSWITCH 注册鉴权与用户变量：

GET /freeswitch/xmlcurl?type=directory&domain=default&user=1000

2) Dialplan 示例（GET）

GET /freeswitch/xmlcurl?type=dialplan&context=default&dest=1000

3) FreeSWITCH POST（application/x-www-form-urlencoded）

应用兼容多种别名：type/section、dest/destination_number/Caller-Destination-Number、user/User-Name、domain/Realm。

4) curl 本地验证

当开启了 token 时：

curl -H "X-XMLCURL-TOKEN: change_me" "http://127.0.0.1:9003/freeswitch/xmlcurl?type=directory&domain=default&user=1000"

curl -H "X-XMLCURL-TOKEN: change_me" "http://127.0.0.1:9003/freeswitch/xmlcurl?type=dialplan&context=default&dest=1000"

5) FreeSWITCH xml_curl.conf.xml 示例

<configuration name="xml_curl.conf" description="cURL XML Gateway">
  <bindings>
    <binding name="bytedesk">
      <param name="gateway-url" value="http://127.0.0.1:9003/freeswitch/xmlcurl" bindings="dialplan|directory"/>
      <param name="timeout" value="10"/>
      <!-- 若使用 token，可加 basic/headers 或者把 token 拼到 url 上；建议通过反向代理统一加头 -->
    </binding>
  </bindings>
  </configuration>

6) 常见问题

- 返回 401：缺少或错误的 X-XMLCURL-TOKEN，请检查配置 bytedesk.call.freeswitch.xmlcurl.token 或在请求中附带 header/参数 token。
- 返回 403：请求来源 IP 不在白名单，调整 bytedesk.call.freeswitch.xmlcurl.ip-whitelist。
- FreeSWITCH 仍未调用 HTTP：确认模块已加载并启用 xml_curl.conf.xml，检查 bindings 与 gateway-url 是否可访问。

### 呼叫控制 API

#### 发起呼叫

```bash
curl -X POST http://localhost:9003/api/freeswitch/originate \
  -H "Content-Type: application/json" \
  -d '{
    "from": "1000",
    "to": "1001",
    "timeout": 60
  }'
```

#### 挂断呼叫

```bash
curl -X POST http://localhost:9003/api/freeswitch/hangup \
  -H "Content-Type: application/json" \
  -d '{
    "uuid": "call-uuid-here"
  }'
```

#### 转接呼叫

```bash
curl -X POST http://localhost:9003/api/freeswitch/transfer \
  -H "Content-Type: application/json" \
  -d '{
    "uuid": "call-uuid-here",
    "destination": "1002"
  }'
```

#### 播放文件

```bash
curl -X POST http://localhost:9003/api/freeswitch/playback \
  -H "Content-Type: application/json" \
  -d '{
    "uuid": "call-uuid-here",
    "file": "/usr/local/freeswitch/sounds/en/us/callie/ivr/8000/ivr-welcome.wav"
  }'
```

### 呼叫状态查询

#### 获取活跃通话

```bash
curl http://localhost:9003/api/freeswitch/calls/active
```

#### 获取通道状态

```bash
curl http://localhost:9003/api/freeswitch/channels/status
```

## WebRTC 集成

### 1. 访问测试页面

启动应用后，访问：<http://localhost:9003/webrtc-demo.html>

### 2. WebSocket 连接

```javascript
// 连接到 WebSocket
const socket = new SockJS('/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({}, function (frame) {
    console.log('Connected: ' + frame);
    
    // 订阅信令消息
    stompClient.subscribe('/topic/webrtc/signaling', function (message) {
        const data = JSON.parse(message.body);
        handleSignalingMessage(data);
    });
});

// 发送 WebRTC Offer
function sendOffer(offer) {
    stompClient.send("/app/webrtc/offer", {}, JSON.stringify({
        type: 'offer',
        sdp: offer.sdp,
        from: 'user1',
        to: 'user2'
    }));
}
```

### 3. 呼叫流程

1. **发起呼叫**: 通过 WebSocket 发送 offer
2. **应答呼叫**: 接收方发送 answer
3. **ICE 候选交换**: 交换网络连接信息
4. **建立连接**: WebRTC 点对点连接建立
5. **通话控制**: 静音、挂断等操作

## 事件监听

应用会自动监听 FreeSwitch 事件并通过 WebSocket 推送：

```javascript
// 订阅呼叫事件
stompClient.subscribe('/topic/call/events', function (message) {
    const event = JSON.parse(message.body);
    console.log('Call event:', event);
    
    switch(event.type) {
        case 'CHANNEL_CREATE':
            console.log('New call created:', event.uuid);
            break;
        case 'CHANNEL_ANSWER':
            console.log('Call answered:', event.uuid);
            break;
        case 'CHANNEL_HANGUP':
            console.log('Call ended:', event.uuid);
            break;
    }
});
```

## 呼叫中心功能

### 队列管理

```bash
# 创建队列
curl -X POST http://localhost:9003/api/callcenter/queue \
  -H "Content-Type: application/json" \
  -d '{
    "name": "support_queue",
    "strategy": "longest-idle-agent",
    "timeout": 300
  }'

# 加入队列
curl -X POST http://localhost:9003/api/callcenter/queue/support_queue/join \
  -H "Content-Type: application/json" \
  -d '{
    "caller": "1000"
  }'
```

### 坐席管理

```bash
# 坐席登录
curl -X POST http://localhost:9003/api/callcenter/agent/login \
  -H "Content-Type: application/json" \
  -d '{
    "agentId": "agent001",
    "queue": "support_queue",
    "extension": "1001"
  }'

# 坐席状态设置
curl -X POST http://localhost:9003/api/callcenter/agent/agent001/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "Available"
  }'
```

## 配置说明

### FreeSwitch 配置文件

- **vars_bytedesk.xml**: 全局变量配置
- **bytedesk.xml**: 用户目录配置
- **event_socket.conf.xml**: ESL 服务配置
- **verto_bytedesk.conf.xml**: WebRTC Verto 配置

### 关键配置参数

```xml
<!-- ESL 密码 -->
<param name="password" value="bytedesk123"/>

<!-- WebRTC 端口 -->
<param name="ws-binding" value=":15066"/>
<param name="wss-binding" value=":17443"/>

<!-- SIP 端口 -->
<param name="sip-port" value="15060"/>
<param name="rtp-port" value="16000-16129"/>
```

## 故障排除

### 常见问题

1. **无法连接 FreeSwitch ESL**
   - 检查 FreeSwitch 是否启动
   - 验证 ESL 密码和端口配置
   - 确认防火墙设置

2. **WebRTC 连接失败**
   - 检查 Verto 配置是否正确
   - 验证 WebSocket 端口是否开放
   - 确认 STUN/TURN 服务器配置

3. **音频质量问题**
   - 检查编解码器配置
   - 验证网络带宽和延迟
   - 调整 RTP 端口范围

### 日志查看

```bash
# FreeSwitch 日志
tail -f /usr/local/freeswitch/log/freeswitch.log

# Spring Boot 日志
tail -f logs/bytedeskim.log
```

## 开发指南

### 添加新的呼叫功能

1. 在 `FreeSwitchService` 中添加新方法
2. 在 `FreeSwitchController` 中暴露 REST API
3. 更新事件监听逻辑（如需要）
4. 添加相应的测试用例

### 自定义事件处理

```java
@Component
public class CustomEventHandler {
    
    @EventListener
    public void handleChannelCreate(ChannelCreateEvent event) {
        // 处理通道创建事件
        log.info("Channel created: {}", event.getUuid());
    }
    
    @EventListener  
    public void handleChannelAnswer(ChannelAnswerEvent event) {
        // 处理通话接听事件
        log.info("Channel answered: {}", event.getUuid());
    }
}
```

## 许可证

本项目采用 Business Source License 1.1 许可证。详情请参阅 [LICENSE](../../LICENSE) 文件。

## 支持

- 邮箱: <270580156@qq.com>
- 官网: <https://bytedesk.com>
- GitHub: <https://github.com/Bytedesk/bytedesk>

## 贡献

欢迎提交 Issue 和 Pull Request。详情请参阅 [CONTRIBUTING.md](../../CONTRIBUTING.md)。
