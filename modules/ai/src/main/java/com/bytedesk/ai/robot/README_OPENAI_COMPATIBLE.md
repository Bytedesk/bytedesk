# OpenAI 兼容的 Chat Completions 接口

## 概述

`RobotChatController` 现在提供了一个完全兼容 OpenAI API 的 `chat/completions` 接口，允许第三方应用程序使用标准的 OpenAI 客户端库来调用 Bytedesk 的 AI 服务。

## 接口端点

```bash
POST /api/ai/chat/v1/chat/completions
```

## 功能特性

- ✅ 完全兼容 OpenAI Chat Completions API 格式
- ✅ 支持流式和非流式响应
- ✅ 支持多种消息角色（system, user, assistant）
- ✅ 自动 token 使用情况统计
- ✅ 错误处理和响应格式标准化
- ✅ 支持所有 OpenAI 标准参数

## 请求格式

### 非流式请求

```bash
curl -X POST http://localhost:9003/api/ai/chat/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "bytedesk-ai",
    "messages": [
      {
        "role": "system",
        "content": "You are a helpful assistant."
      },
      {
        "role": "user",
        "content": "Hello, how are you?"
      }
    ],
    "temperature": 0.7,
    "max_tokens": 150
  }'
```

### 流式请求

```bash
curl -X POST http://localhost:9003/api/ai/chat/v1/chat/completions \
  -H "Content-Type: application/json" \
  -d '{
    "model": "bytedesk-ai",
    "messages": [
      {
        "role": "user",
        "content": "Tell me a story"
      }
    ],
    "stream": true,
    "temperature": 0.7
  }'
```

## 支持的参数

| 参数 | 类型 | 必需 | 描述 |
|------|------|------|------|
| `model` | string | 是 | 模型名称，可以是任意字符串 |
| `messages` | array | 是 | 消息数组，包含 role 和 content |
| `max_tokens` | integer | 否 | 最大生成 token 数 |
| `temperature` | number | 否 | 温度参数 (0-2) |
| `top_p` | number | 否 | 核采样参数 |
| `n` | integer | 否 | 生成的回复数量 |
| `stream` | boolean | 否 | 是否使用流式响应 |
| `stop` | array | 否 | 停止词列表 |
| `presence_penalty` | number | 否 | 存在惩罚 |
| `frequency_penalty` | number | 否 | 频率惩罚 |
| `logit_bias` | object | 否 | logit 偏置 |
| `user` | string | 否 | 用户 ID |

## 消息角色

- `system`: 系统消息，用于设置助手的行为
- `user`: 用户消息
- `assistant`: 助手回复消息

## 响应格式

### 非流式响应

```json
{
  "id": "chatcmpl-123",
  "object": "chat.completion",
  "created": 1677652288,
  "model": "bytedesk-ai",
  "choices": [
    {
      "index": 0,
      "message": {
        "role": "assistant",
        "content": "Hello! I'm doing well, thank you for asking. How can I help you today?"
      },
      "finish_reason": "stop"
    }
  ],
  "usage": {
    "prompt_tokens": 56,
    "completion_tokens": 31,
    "total_tokens": 87
  }
}
```

### 流式响应

```bash
data: {"id":"chatcmpl-123","object":"chat.completion.chunk","created":1677652288,"model":"bytedesk-ai","choices":[{"index":0,"delta":{"content":"Hello"},"finish_reason":null}]}

data: {"id":"chatcmpl-123","object":"chat.completion.chunk","created":1677652288,"model":"bytedesk-ai","choices":[{"index":0,"delta":{"content":" there"},"finish_reason":null}]}

data: [DONE]
```

## 使用 OpenAI 客户端库

### Python

```python
import openai

# 配置客户端指向 Bytedesk 服务
client = openai.OpenAI(
    api_key="your-api-key",  # 可以是任意值
    base_url="http://localhost:9003/api/ai/chat/v1"
)

# 发送请求
response = client.chat.completions.create(
    model="bytedesk-ai",
    messages=[
        {"role": "system", "content": "You are a helpful assistant."},
        {"role": "user", "content": "Hello!"}
    ]
)

print(response.choices[0].message.content)
```

### JavaScript/Node.js

```javascript
import OpenAI from 'openai';

const openai = new OpenAI({
  apiKey: 'your-api-key',  // 可以是任意值
  baseURL: 'http://localhost:9003/api/ai/chat/v1'
});

async function main() {
  const completion = await openai.chat.completions.create({
    messages: [
      { role: 'system', content: 'You are a helpful assistant.' },
      { role: 'user', content: 'Hello!' }
    ],
    model: 'bytedesk-ai',
  });

  console.log(completion.choices[0].message.content);
}

main();
```

### Java

```java
// 使用 OpenAI Java 客户端
OpenAiService service = new OpenAiService("your-api-key", Duration.ofSeconds(30));
service.setBaseUrl("http://localhost:9003/api/ai/chat/v1/");

ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
    .builder()
    .model("bytedesk-ai")
    .messages(Arrays.asList(
        new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are a helpful assistant."),
        new ChatMessage(ChatMessageRole.USER.value(), "Hello!")
    ))
    .build();

ChatCompletionResult result = service.createChatCompletion(chatCompletionRequest);
System.out.println(result.getChoices().get(0).getMessage().getContent());
```

## 错误处理

当发生错误时，接口会返回标准的 OpenAI 错误格式：

```json
{
  "error": {
    "message": "No chat model available",
    "type": "service_unavailable",
    "code": null
  }
}
```

## 注意事项

1. **模型配置**: 确保已正确配置 Primary ChatModel，否则接口将返回 "No chat model available" 错误
2. **Stream 模式**: 流式响应使用 Server-Sent Events (SSE) 格式
3. **Token 统计**: Token 使用情况取决于底层模型提供商的支持
4. **兼容性**: 此接口完全兼容 OpenAI API v1 格式，可以直接替换 OpenAI 端点使用

## 配置要求

确保在 `application.properties` 或 `application.yml` 中正确配置了 ChatModel：

```yaml
spring:
  ai:
    model:
      chat: openai  # 或其他支持的提供商：ollama、zhipuai 等
    openai:
      chat:
        enabled: true
        api-key: your-api-key
        model: gpt-3.5-turbo
```

这样配置后，第三方应用程序就可以使用标准的 OpenAI 客户端库来调用 Bytedesk 的 AI 服务了。
