# Dashscope 实现方式对比

本项目提供了两种 Dashscope 实现方式，可以根据需要选择使用：

## 1. OpenAiChatModel 方式（兼容模式）

**位置：** `com.bytedesk.ai.springai.providers.dashscope`

**特点：**
- 使用 `OpenAiChatModel` 和 `OpenAiChatOptions`
- 通过兼容模式 API 端点：`https://dashscope.aliyuncs.com/compatible-mode`
- 模拟 OpenAI 的 API 格式
- 需要手动处理 token 统计

**配置类：** `SpringAIDashscopeChatConfig`
**服务类：** `SpringAIDashscopeService`
**控制器：** `SpringAIDashscopeController`

**API 端点：**
- `/springai/dashscope/chat/sync`
- `/springai/dashscope/chat/stream`
- `/springai/dashscope/chat/sse`

## 2. DashScopeChatModel 方式（官方推荐）

**位置：** `com.bytedesk.ai.alibaba.dashscope`

**特点：**
- 使用官方的 `DashScopeChatModel` 和 `DashScopeChatOptions`
- 通过 `spring-ai-alibaba-starter-dashscope` 自动配置
- 原生支持 Dashscope API
- 更准确的 token 统计
- 支持更多 Dashscope 特有功能（如联网搜索、自定义请求头等）

**配置类：** `AlibabaDashscopeChatConfig`
**服务类：** `AlibabaDashscopeService`
**控制器：** `AlibabaDashscopeController`

**API 端点：**
- `/alibaba/dashscope/chat/sync`
- `/alibaba/dashscope/chat/official`
- `/alibaba/dashscope/tokens`
- `/alibaba/dashscope/chat/custom`
- `/alibaba/dashscope/health`

## 配置对比

### OpenAiChatModel 方式配置
```properties
spring.ai.dashscope.base-url=https://dashscope.aliyuncs.com/compatible-mode
spring.ai.dashscope.api-key=your-api-key
spring.ai.dashscope.chat.enabled=true
spring.ai.dashscope.chat.options.model=deepseek-r1
spring.ai.dashscope.chat.options.temperature=0.7
```

### DashScopeChatModel 方式配置
```properties
spring.ai.dashscope.api-key=your-api-key
spring.ai.dashscope.chat.enabled=true
spring.ai.dashscope.chat.options.model=deepseek-r1
spring.ai.dashscope.chat.options.temperature=0.7
```

## Token 统计对比

### OpenAiChatModel 方式
```java
// 需要手动提取和估算 token
TokenUsage tokenUsage = extractDashscopeTokenUsage(response);
```

### DashScopeChatModel 方式
```java
// 官方支持，更准确的 token 统计
res.put("output_token", chatResponse.getMetadata().getUsage().getCompletionTokens());
res.put("input_token", chatResponse.getMetadata().getUsage().getPromptTokens());
res.put("total_token", chatResponse.getMetadata().getUsage().getTotalTokens());
```

## 推荐使用

**建议使用 DashScopeChatModel 方式（官方推荐）**，原因如下：

1. **官方支持**：使用阿里云官方的 SDK，更稳定可靠
2. **功能完整**：支持 Dashscope 的所有特性
3. **Token 统计准确**：官方提供准确的 token 使用情况
4. **维护成本低**：减少自定义代码，降低维护成本
5. **性能更好**：原生优化，性能更佳

## 迁移指南

如果要从 OpenAiChatModel 方式迁移到 DashScopeChatModel 方式：

1. **更新依赖**：确保已引入 `spring-ai-alibaba-starter-dashscope`
2. **修改配置**：移除 `base-url` 配置，使用官方自动配置
3. **更新代码**：将服务注入改为使用 `alibabaDashscopeChatModel`
4. **测试验证**：确保功能正常，token 统计准确

## 示例代码

### 使用官方 DashScopeChatModel
```java
@Autowired
@Qualifier("alibabaDashscopeChatModel")
private ChatModel alibabaDashscopeChatModel;

// 调用示例
ChatResponse response = alibabaDashscopeChatModel.call(
    new Prompt("Hello", DashScopeChatOptions.builder()
        .withModel(DashScopeApi.ChatModel.QWEN_PLUS.getValue())
        .withTemperature(0.7)
        .build())
);

// 获取 token 信息
long promptTokens = response.getMetadata().getUsage().getPromptTokens();
long completionTokens = response.getMetadata().getUsage().getCompletionTokens();
long totalTokens = response.getMetadata().getUsage().getTotalTokens();
```

### 使用兼容模式 OpenAiChatModel
```java
@Autowired
private OpenAiChatModel bytedeskDashscopeChatModel;

// 调用示例
String response = bytedeskDashscopeChatModel.call("Hello");

// 需要手动估算 token
TokenUsage tokenUsage = estimateDashscopeTokenUsageFromText(response, "Hello");
``` 