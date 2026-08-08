# Anthropic 提供方

基于 Spring AI 的 Anthropic Claude 模型集成。

## 参考文档

- [Spring AI Anthropic Chat](https://docs.spring.io/spring-ai/reference/api/chat/anthropic-chat.html)
- [anthropic-sdk-java](https://github.com/anthropics/anthropic-sdk-java)

## 配置

在 `application.properties` 中启用：

```properties
spring.ai.anthropic.chat.enabled=true
spring.ai.anthropic.base-url=https://api.anthropic.com
spring.ai.anthropic.api-key=sk-ant-xxx
spring.ai.anthropic.chat.options.model=claude-sonnet-4-5
spring.ai.anthropic.chat.options.temperature=0.7
spring.ai.anthropic.chat.options.max-tokens=4096
```

## 组件

- `SpringAIAnthropicChatConfig` - 聊天模型 / 客户端 Bean 配置
- `SpringAIAnthropicService` - 主服务，支持动态 provider 解析（继承 `BaseSpringAIService`）
- `SpringAIAnthropicChatService` - 条件聊天服务，使用默认 ChatModel
- `SpringAIAnthropicChatController` - REST API 接口（同步 / 流式 / SSE / 结构化输出）

