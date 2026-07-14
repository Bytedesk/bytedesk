# MRCP4J 实例代码使用指南

本目录包含 MRCP4J 的实际业务代码实例,用于处理语音识别(ASR)和语音合成(TTS)业务。

## 📁 文件说明

### 1. SimpleExample.java

最简单的使用示例,适合初学者快速了解 MRCP4J 的基本用法。

**功能:**

- 初始化 MRCP 客户端连接
- 执行简单的语音识别
- 执行简单的语音合成

**运行方式:**

```bash
java com.bytedesk.call.mrcp4j.example.SimpleExample
```

### 2. BankingIvrExample.java

完整的银行客服 IVR 流程实现,展示如何整合语音识别和合成实现业务流程。

**功能:**

- 播放欢迎语
- 识别用户意图(查询余额/转账/人工服务)
- 根据意图执行相应业务逻辑
- 播放业务响应

**运行方式:**

```bash
java com.bytedesk.call.mrcp4j.example.BankingIvrExample
```

## 🚀 快速开始

### 前置条件

1. **启动 MRCP 服务器**

   ```bash
   # 确保 MRCP 服务器运行在 localhost:1544
   ```

2. **添加依赖**
   确保项目包含 MRCP4J 相关依赖

### 运行示例

#### 示例 1: 简单示例

```java
public static void main(String[] args) {
    SimpleExample example = new SimpleExample();
    
    try {
        // 1. 初始化连接
        example.init("localhost", 1544);
        
        // 2. 执行语音识别
        example.recognize();
        Thread.sleep(10000); // 等待识别完成
        
        // 3. 执行语音合成
        example.synthesize();
        Thread.sleep(5000); // 等待合成完成
        
    } catch (Exception e) {
    } finally {
        example.close();
    }
}
```

#### 示例 2: 银行 IVR

```java
public static void main(String[] args) {
    BankingIvrExample example = new BankingIvrExample();
    
    try {
        // 初始化
        example.init("localhost", 1544);
        
        // 执行完整的 IVR 流程
        example.executeIvrFlow();
        
    } catch (Exception e) {
    } finally {
        example.close();
    }
}
```

## 📝 代码说明

### 初始化连接

```java
// 创建工厂
MrcpFactory factory = new MrcpFactory();

// 创建提供者
MrcpProvider provider = factory.createProvider();

// 创建通道
InetSocketAddress serverAddress = new InetSocketAddress("localhost", 1544);
MrcpChannel channel = provider.createChannel(
    "my-channel",
    serverAddress,
    MrcpResourceType.SPEECHRECOG  // 或 SPEECHSYNTH
);

// 添加事件监听器
channel.addEventListener(new MrcpEventListener() {
    @Override
    public void eventReceived(MrcpEvent event) {
        // 处理事件
        System.out.println("收到事件: " + event.getEventName());
    }
});
```

### 语音识别

```java
// 1. 创建请求
MrcpRequest request = MrcpRequestFactory.createVendorSpecificRequest("RECOGNIZE");
request.setRequestID(1);

// 2. 设置语法
String grammar = "<?xml version=\"1.0\"?>..." // SRGS 语法
request.setContent("application/srgs+xml", grammar, null);

// 3. 设置参数
request.addHeader(MrcpHeaderName.CONFIDENCE_THRESHOLD.constructHeader("0.7"));
request.addHeader(MrcpHeaderName.NO_INPUT_TIMEOUT.constructHeader("5000"));

// 4. 发送请求
MrcpResponse response = channel.sendRequest(request);
```

### 语音合成

```java
// 1. 创建请求
MrcpRequest request = MrcpRequestFactory.createVendorSpecificRequest("SPEAK");
request.setRequestID(2);

// 2. 设置 SSML
String ssml = "<?xml version=\"1.0\"?>..." // SSML 内容
request.setContent("application/ssml+xml", ssml, null);

// 3. 设置语音参数
request.addHeader(MrcpHeaderName.VOICE_NAME.constructHeader("xiaoyun"));

// 4. 发送请求
MrcpResponse response = channel.sendRequest(request);
```

### 事件处理

```java
// 处理识别事件
private void handleAsrEvent(MrcpEvent event) {
    String eventName = event.getEventName();
    
    if ("START-OF-INPUT".equals(eventName)) {
        // 检测到语音输入
    } else if ("RECOGNITION-COMPLETE".equals(eventName)) {
        // 识别完成
        String result = event.getContent();
    } else if ("RECOGNITION-FAILED".equals(eventName)) {
        // 识别失败
    }
}

// 处理合成事件
private void handleTtsEvent(MrcpEvent event) {
    String eventName = event.getEventName();
    
    if ("SPEAK-COMPLETE".equals(eventName)) {
        // 播放完成
    } else if ("SPEAK-FAILED".equals(eventName)) {
        // 播放失败
    }
}
```

## 业务场景示例

### 场景 1: 简单查询

- 用户说:"查询余额"
- 系统识别意图
- 查询数据库
- 播报结果

### 场景 2: 多轮对话

- 播放欢迎语
- 识别用户意图
- 根据意图进入不同分支
- 可能需要多次交互
- 完成业务或转人工

### 场景 3: 数字收集

- 播放提示:"请输入卡号"
- 使用 DTMF 或语音识别
- 收集 16 位数字
- 验证并处理

## ⚙️ 配置说明

### SRGS 语法示例

```xml
<?xml version="1.0"?>
<grammar xmlns="http://www.w3.org/2001/06/grammar" 
         version="1.0" xml:lang="zh-CN" mode="voice">
  <rule id="command" scope="public">
    <one-of>
      <item>查询余额</item>
      <item>转账</item>
      <item>人工服务</item>
    </one-of>
  </rule>
</grammar>
```

### SSML 示例

```xml
<?xml version="1.0"?>
<speak version="1.0" 
       xmlns="http://www.w3.org/2001/10/synthesis" 
       xml:lang="zh-CN">
  <prosody rate="medium" pitch="medium">
    欢迎使用语音服务系统
  </prosody>
</speak>
```

## 🐛 常见问题

### Q1: 连接失败怎么办?

**A:**

1. 检查 MRCP 服务器是否启动
2. 确认服务器地址和端口正确
3. 检查防火墙设置

### Q2: 识别没有结果?

**A:**

1. 确保有音频输入
2. 检查语法定义是否正确
3. 调整置信度阈值
4. 增加超时时间

### Q3: 合成没有声音?

**A:**

1. 检查 SSML 格式
2. 确认语音名称支持
3. 检查音频输出设备
4. 查看服务器日志

### Q4: 事件没有触发?

**A:**

1. 确认已添加事件监听器
2. 检查事件名称大小写
3. 查看服务器端配置

## 📚 参考资料

- [RFC 6787 - MRCPv2 协议](https://tools.ietf.org/html/rfc6787)
- [SRGS 语法规范](https://www.w3.org/TR/speech-grammar/)
- [SSML 规范](https://www.w3.org/TR/speech-synthesis/)
- [MRCP4J 主 README](../README.md)

## 💡 最佳实践

1. **资源管理**
   - 使用完毕后及时关闭通道
   - 避免创建过多连接
   - 合理设置超时时间

2. **错误处理**
   - 捕获所有异常
   - 提供友好错误提示
   - 记录详细日志

3. **性能优化**
   - 复用 MRCP 通道
   - 异步处理请求
   - 合理使用线程池

4. **安全性**
   - 验证用户输入
   - 限制请求频率
   - 保护敏感信息
