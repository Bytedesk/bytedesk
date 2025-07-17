# ChatModel 信息查询 API 使用说明

## 概述

本系统提供了完整的ChatModel信息查询功能，包括查看所有可用的ChatModel、Primary ChatModel、以及测试不同提供商的功能。

## 配置说明

当前系统支持以下ChatModel提供商：
- `zhipuai` - 智谱AI云端对话模型
- `ollama` - 本地Ollama对话模型  
- `dashscope` - 阿里云Dashscope对话模型
- `deepseek` - DeepSeek云端对话模型
- `baidu` - 百度文心一言对话模型
- `tencent` - 腾讯混元对话模型
- `volcengine` - 火山引擎对话模型
- `openai` - OpenAI对话模型
- `openrouter` - OpenRouter对话模型
- `siliconflow` - SiliconFlow对话模型
- `gitee` - Gitee对话模型

## API 接口

### 1. 获取所有ChatModel信息

**接口地址：** `GET /spring/ai/api/v1/chat-models/info`

**功能：** 获取所有可用的ChatModel信息，包括Primary ChatModel

**示例：**
```bash
curl -X GET "http://127.0.0.1:9003/spring/ai/api/v1/chat-models/info"
```

**返回示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "chatModels": [
      {
        "provider": "zhipuai",
        "className": "ZhiPuAiChatModel",
        "fullClassName": "org.springframework.ai.zhipuai.ZhiPuAiChatModel",
        "isPrimary": true,
        "enabled": true,
        "description": "智谱AI云端对话模型",
        "modelType": "Cloud Chat Model",
        "status": "Active",
        "testResponse": "你好！我是智谱AI助手，很高兴为您服务。有什么我可以帮助您的吗？..."
      }
    ],
    "primaryModel": {
      "provider": "zhipuai",
      "className": "ZhiPuAiChatModel",
      "fullClassName": "org.springframework.ai.zhipuai.ZhiPuAiChatModel",
      "isPrimary": true,
      "status": "Active",
      "testResponse": "你好！我是智谱AI助手，很高兴为您服务。有什么我可以帮助您的吗？..."
    },
    "totalCount": 1,
    "primaryProvider": "zhipuai",
    "timestamp": 1721234567890
  }
}
```

### 2. 获取Primary ChatModel信息

**接口地址：** `GET /spring/ai/api/v1/chat-models/primary`

**功能：** 获取当前配置的Primary ChatModel信息

**示例：**
```bash
curl -X GET "http://127.0.0.1:9003/spring/ai/api/v1/chat-models/primary"
```

### 3. 获取RAG使用的ChatModel信息

**接口地址：** `GET /spring/ai/api/v1/chat-models/rag`

**功能：** 获取RAG系统使用的ChatModel信息

**示例：**
```bash
curl -X GET "http://127.0.0.1:9003/spring/ai/api/v1/chat-models/rag"
```

### 4. 测试指定的ChatModel

**接口地址：** `GET /spring/ai/api/v1/chat-models/test/{provider}`

**功能：** 测试指定提供商的ChatModel

**参数：**
- `provider`: 提供商名称（如：zhipuai, ollama, dashscope等）

**示例：**
```bash
# 测试智谱AI
curl -X GET "http://127.0.0.1:9003/spring/ai/api/v1/chat-models/test/zhipuai"

# 测试Ollama
curl -X GET "http://127.0.0.1:9003/spring/ai/api/v1/chat-models/test/ollama"
```

**返回示例：**
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "status": "Success",
    "provider": "zhipuai",
    "testMessage": "Hello, this is a test message. Please respond with 'OK' if you can see this.",
    "response": "你好！我看到了你的测试消息。我可以正常响应，所以回复'OK'。",
    "responseLength": 25,
    "modelClass": "ZhiPuAiChatModel",
    "timestamp": 1721234567890
  }
}
```

### 5. 测试所有可用的ChatModel

**接口地址：** `GET /spring/ai/api/v1/chat-models/test-all`

**功能：** 测试所有可用的ChatModel

**示例：**
```bash
curl -X GET "http://127.0.0.1:9003/spring/ai/api/v1/chat-models/test-all"
```

## 配置要求

### 1. 启用调试模式

所有API都需要在调试模式下才能访问，确保在配置文件中设置：

```properties
bytedesk.debug=true
```

### 2. 配置Primary ChatModel

在 `application.properties` 中设置Primary ChatModel：

```properties
# 设置Primary ChatModel
spring.ai.model.chat=zhipuai

# 启用对应的提供商
spring.ai.zhipuai.chat.enabled=true
spring.ai.zhipuai.api-key=your-api-key
spring.ai.zhipuai.chat.options.model=glm-4-flash
```

### 3. 启用Java AI功能

确保启用Java AI功能：

```properties
bytedesk.features.java-ai=true
```

## 使用场景

### 1. 系统监控
- 监控当前使用的ChatModel状态
- 检查Primary ChatModel是否正常工作
- 查看所有可用的ChatModel提供商

### 2. 故障排查
- 测试特定提供商的ChatModel是否可用
- 查看ChatModel的详细错误信息
- 验证配置是否正确

### 3. 性能测试
- 测试不同提供商的响应时间
- 比较不同ChatModel的响应质量
- 评估系统整体性能

### 4. 配置验证
- 验证Primary ChatModel配置
- 检查RAG系统使用的ChatModel
- 确认所有提供商配置正确

## 注意事项

1. **安全性**：所有API都受调试模式控制，生产环境中请确保 `bytedesk.debug=false`
2. **性能**：测试API会实际调用ChatModel，可能产生费用和消耗资源
3. **错误处理**：API会返回详细的错误信息，便于问题排查
4. **依赖关系**：确保相关的ChatModel配置正确且服务可用

## 相关文件

- `ChatModelPrimaryConfig.java` - Primary ChatModel配置
- `ChatModelInfoService.java` - ChatModel信息服务
- `ChatModelInfoController.java` - ChatModel信息控制器
- `EmbeddingModelPrimaryConfig.java` - Primary EmbeddingModel配置（参考）
- `EmbeddingModelInfoService.java` - EmbeddingModel信息服务（参考）
- `EmbeddingModelInfoController.java` - EmbeddingModel信息控制器（参考） 