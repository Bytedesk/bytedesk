# SpringAI RAG Controller 重构说明

## 问题描述
原有的 `SpringAIRagController` 使用了特定的 `OllamaChatModel`，当 Ollama 没有启用时会导致以下错误：
```
Parameter 2 of constructor in com.bytedesk.ai.springai.rag.SpringAIRagController required a bean of type 'org.springframework.ai.ollama.OllamaChatModel' that could not be found.
```

## 解决方案
将特定的 `OllamaChatModel` 替换为通用的 `ChatModel` 接口，使控制器能够适配任何 ChatModel 实现。

## 主要更改

### 1. SpringAIRagController.java
- **字段更改**: 将 `private final OllamaChatModel bytedeskOllamaChatModel` 改为 `private final ChatModel chatModel`
- **导入更改**: 移除 `OllamaChatModel` 导入，添加 `ChatModel` 导入
- **注解更改**: 添加 `@ConditionalOnBean(ChatModel.class)` 确保只有在有 ChatModel 可用时才创建控制器
- **方法更新**: 所有使用 `bytedeskOllamaChatModel` 的地方都替换为 `chatModel`

### 2. 新增配置类

#### ChatModelConfig.java
- 提供备用的 ChatModel 配置
- 只有在没有其他 ChatModel 实现时才创建 FallbackChatModel
- 受 `bytedesk.features.java-ai=true` 配置控制

#### FallbackChatModel.java
- 简单的 ChatModel 实现作为备用
- 当没有其他 ChatModel 可用时返回友好的错误消息
- 提醒用户配置正确的 ChatModel 实现

## 兼容性
此更改向后兼容：
- 当 Ollama 启用时，会优先使用 OllamaChatModel
- 当其他 ChatModel 实现（如 OpenAI）可用时，会使用相应的实现
- 当没有任何 ChatModel 可用时，会使用 FallbackChatModel 避免启动错误

## 配置要求
确保在 `application.properties` 中设置：
```properties
bytedesk.features.java-ai=true
```

## 测试建议
1. 测试 Ollama 启用的情况
2. 测试 Ollama 禁用但有其他 ChatModel 的情况
3. 测试完全没有 ChatModel 的情况（应该使用 FallbackChatModel）
