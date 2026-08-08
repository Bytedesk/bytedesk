# Anthropic Provider

Anthropic Claude model integration based on Spring AI.

## References

- [Spring AI Anthropic Chat](https://docs.spring.io/spring-ai/reference/api/chat/anthropic-chat.html)
- [anthropic-sdk-java](https://github.com/anthropics/anthropic-sdk-java)

## Configuration

Enable in `application.properties`:

```properties
spring.ai.anthropic.chat.enabled=true
spring.ai.anthropic.base-url=https://api.anthropic.com
spring.ai.anthropic.api-key=sk-ant-xxx
spring.ai.anthropic.chat.options.model=claude-sonnet-4-5
spring.ai.anthropic.chat.options.temperature=0.7
spring.ai.anthropic.chat.options.max-tokens=4096
```

## Components

- `SpringAIAnthropicChatConfig` - Chat model / client bean configuration
- `SpringAIAnthropicService` - Main service with dynamic provider resolution (extends `BaseSpringAIService`)
- `SpringAIAnthropicChatService` - Conditional chat service using default ChatModel
- `SpringAIAnthropicChatController` - REST API endpoints (sync / stream / SSE / structured output)

