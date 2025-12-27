# MRCP4J - Java MRCP Protocol Implementation

MRCP4J 是一个完整的 Media Resource Control Protocol Version 2 (MRCPv2) Java 实现库,用于构建语音识别(ASR)和语音合成(TTS)应用。

[Github Mrcp4j](https://github.com/JVoiceXML/mrcp4j)

## 📋 目录

- [特性](#特性)
- [架构概览](#架构概览)
- [快速开始](#快速开始)
- [详细示例](#详细示例)
- [API 文档](#api-文档)
- [配置说明](#配置说明)
- [常见问题](#常见问题)

## ✨ 特性

- ✅ 完整支持 MRCPv2 协议 (RFC 6787)
- ✅ 支持多种资源类型:语音识别、语音合成、录音、说话人验证
- ✅ 基于 Apache MINA 的高性能网络通信
- ✅ 支持异步和同步调用模式
- ✅ 线程安全的客户端和服务器实现
- ✅ 灵活的事件监听机制
- ✅ 完善的错误处理和日志记录

## 🏗️ 架构概览

```
mrcp4j/
├── client/              # 客户端实现
│   ├── MrcpFactory      # 工厂类,创建客户端实例
│   ├── MrcpChannel      # 通道接口,管理连接
│   ├── MrcpProvider     # 协议提供者
│   └── MrcpSocket       # 网络通信层
├── server/              # 服务器实现
│   ├── MrcpServerSocket # 服务器主类
│   ├── MrcpSession      # 会话管理
│   ├── delegator/       # 请求委托器
│   └── provider/        # 服务提供者接口
├── message/             # 消息定义
│   ├── MrcpMessage      # 消息基类
│   ├── MrcpRequest      # 请求消息
│   ├── MrcpResponse     # 响应消息
│   └── header/          # 消息头定义
└── util/                # 工具类
```

## 🚀 快速开始

### 1. 创建 MRCP 客户端

```java
import com.bytedesk.call.mrcp4j.client.*;
import com.bytedesk.call.mrcp4j.message.request.*;

// 创建工厂实例
MrcpFactory factory = MrcpFactory.newInstance();

// 创建语音识别通道
MrcpChannel channel = factory.createChannel(
    "mrcp-server.example.com",  // 服务器地址
    1544,                         // 端口
    MrcpResourceType.SPEECHRECOG  // 资源类型
);

// 添加事件监听器
channel.addEventListener(new MrcpEventListener() {
    @Override
    public void eventReceived(MrcpEvent event) {
        System.out.println("收到事件: " + event.getEventName());
    }
});
```

### 2. 发送语音识别请求

```java
// 创建 RECOGNIZE 请求
MrcpRequest request = MrcpRequestFactory.createRecognizeRequest();

// 设置语法内容 (SRGS 格式)
String grammar = 
    "<?xml version=\"1.0\"?>\n" +
    "<grammar xmlns=\"http://www.w3.org/2001/06/grammar\" " +
    "version=\"1.0\" xml:lang=\"zh-CN\" mode=\"voice\">\n" +
    "  <rule id=\"command\" scope=\"public\">\n" +
    "    <one-of>\n" +
    "      <item>查询余额</item>\n" +
    "      <item>转账</item>\n" +
    "      <item>人工服务</item>\n" +
    "    </one-of>\n" +
    "  </rule>\n" +
    "</grammar>";

request.setContent("application/srgs+xml", grammar);

// 设置请求参数
request.setHeader(MrcpHeaderName.CONFIDENCE_THRESHOLD, "0.7");
request.setHeader(MrcpHeaderName.NO_INPUT_TIMEOUT, "5000");

// 发送请求并获取响应
try {
    MrcpResponse response = channel.sendRequest(request);
    if (response.getStatusCode() == 200) {
        System.out.println("识别成功!");
    }
} catch (MrcpInvocationException e) {
    System.err.println("请求失败: " + e.getMessage());
}
```

### 3. 发送语音合成请求

```java
// 创建语音合成通道
MrcpChannel ttsChannel = factory.createChannel(
    "mrcp-server.example.com",
    1544,
    MrcpResourceType.SPEECHSYNTH
);

// 创建 SPEAK 请求
MrcpRequest speakRequest = MrcpRequestFactory.createSpeakRequest();

// 设置 SSML 内容
String ssml = 
    "<?xml version=\"1.0\"?>\n" +
    "<speak version=\"1.0\" xmlns=\"http://www.w3.org/2001/10/synthesis\" " +
    "xml:lang=\"zh-CN\">\n" +
    "  <prosody rate=\"medium\" pitch=\"medium\">\n" +
    "    欢迎使用语音服务系统\n" +
    "  </prosody>\n" +
    "</speak>";

speakRequest.setContent("application/ssml+xml", ssml);

// 发送请求
MrcpResponse speakResponse = ttsChannel.sendRequest(speakRequest);
```

## 📚 详细示例

### 服务器端实现

```java
import com.bytedesk.call.mrcp4j.server.*;
import com.bytedesk.call.mrcp4j.server.provider.*;

// 创建服务器
MrcpServerSocket server = new MrcpServerSocket(1544);

// 注册语音识别处理器
server.registerHandler(
    MrcpResourceType.SPEECHRECOG,
    new RecogOnlyRequestHandler() {
        @Override
        public MrcpResponse handleRecognize(MrcpRequest request, MrcpSession session) {
            // 实现语音识别逻辑
            String content = request.getContent();
            
            // 模拟识别结果
            MrcpResponse response = new MrcpResponse();
            response.setStatusCode(200);
            response.setRequestState(MrcpRequestState.COMPLETE);
            
            return response;
        }
    }
);

// 启动服务器
server.start();
System.out.println("MRCP 服务器已启动在端口 1544");
```

### 异步事件处理

```java
channel.addEventListener(new MrcpEventListener() {
    @Override
    public void eventReceived(MrcpEvent event) {
        switch (event.getEventName()) {
            case MrcpEventName.START_OF_INPUT:
                System.out.println("检测到语音输入开始");
                break;
                
            case MrcpEventName.RECOGNITION_COMPLETE:
                String result = event.getContent();
                System.out.println("识别结果: " + result);
                break;
                
            case MrcpEventName.SPEAK_COMPLETE:
                System.out.println("语音合成播放完成");
                break;
                
            default:
                System.out.println("未知事件: " + event.getEventName());
        }
    }
});
```

## 📖 API 文档

### 核心接口

| 类/接口 | 说明 |
|--------|------|
| `MrcpFactory` | 客户端工厂类,用于创建通道 |
| `MrcpChannel` | MRCP 通道,管理与服务器的连接 |
| `MrcpServerSocket` | MRCP 服务器实现 |
| `MrcpRequest` | MRCP 请求消息 |
| `MrcpResponse` | MRCP 响应消息 |
| `MrcpEvent` | MRCP 事件消息 |

### 资源类型

| 类型 | 常量 | 说明 |
|-----|------|------|
| 语音识别 | `MrcpResourceType.SPEECHRECOG` | ASR 服务 |
| 语音合成 | `MrcpResourceType.SPEECHSYNTH` | TTS 服务 |
| 录音 | `MrcpResourceType.RECORDER` | 录音服务 |
| 说话人验证 | `MrcpResourceType.SPEAKVERIFY` | 声纹验证 |

### 方法名称

| 方法 | 适用资源 | 说明 |
|-----|---------|------|
| `RECOGNIZE` | SPEECHRECOG | 启动语音识别 |
| `SPEAK` | SPEECHSYNTH | 启动语音合成 |
| `STOP` | 所有 | 停止当前操作 |
| `START-INPUT-TIMERS` | SPEECHRECOG | 启动输入定时器 |
| `RECORD` | RECORDER | 开始录音 |

## ⚙️ 配置说明

### 客户端配置

```java
// 设置连接超时
channel.setConnectTimeout(5000);

// 设置请求超时
channel.setRequestTimeout(30000);

// 启用自动重连
channel.setAutoReconnect(true);
```

### 服务器配置

```java
// 创建服务器并配置
MrcpServerSocket server = new MrcpServerSocket(1544);

// 设置最大会话数
server.setMaxSessions(100);

// 设置读取超时
server.setReadTimeout(60000);

// 启用详细日志
server.setVerboseLogging(true);
```

## ❓ 常见问题

### Q: 如何处理网络断线重连?

A: 启用自动重连功能:
```java
channel.setAutoReconnect(true);
channel.setReconnectInterval(5000); // 5秒后重试
```

### Q: 如何设置识别置信度阈值?

A: 在请求中设置 Confidence-Threshold 头:
```java
request.setHeader(MrcpHeaderName.CONFIDENCE_THRESHOLD, "0.75");
```

### Q: 如何获取详细的错误信息?

A: 捕获异常并检查响应状态:
```java
try {
    MrcpResponse response = channel.sendRequest(request);
    if (response.getStatusCode() != 200) {
        CompletionCause cause = response.getCompletionCause();
        System.err.println("错误原因: " + cause);
    }
} catch (MrcpInvocationException e) {
    System.err.println("调用失败: " + e.getMessage());
}
```

### Q: 支持哪些音频格式?

A: 支持以下格式:
- PCM (Linear 16-bit)
- G.711 (μ-law/A-law)
- G.729
- OPUS

通过 `Content-Type` 头指定格式:
```java
request.setHeader(MrcpHeaderName.CONTENT_TYPE, "audio/x-wav");
```

## 📝 协议参考

- [RFC 6787 - MRCPv2](https://tools.ietf.org/html/rfc6787)
- [RFC 4463 - MRCPv1](https://tools.ietf.org/html/rfc4463)
- [SRGS Grammar Specification](https://www.w3.org/TR/speech-grammar/)
- [SSML Specification](https://www.w3.org/TR/speech-synthesis/)
