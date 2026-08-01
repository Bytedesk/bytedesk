# MCP

微语可以将已存在的 Spring AI `@Tool` 工具以 Model Context Protocol (MCP) Server 的形式对外开放，方便第三方 Agent、MCP Client 调用当前服务器能力。

## 启用方式

MCP Server 默认关闭，因为它会把系统能力开放给外部客户端。需要接入第三方 Agent 时再显式开启。

```properties
spring.ai.mcp.server.enabled=true
spring.ai.mcp.server.name=bytedesk-mcp-server
spring.ai.mcp.server.type=SYNC
spring.ai.mcp.server.stdio=false
spring.ai.mcp.server.sse-message-endpoint=/mcp/message
```

Spring AI WebMVC MCP 默认使用 SSE 传输：

- SSE endpoint: `/sse`
- Message endpoint: `/mcp/message`

本地 starter 默认端口为 `9003`，因此 MCP Client 可连接：

```text
http://127.0.0.1:9003/sse
```

## 工具开放策略

`BytedeskMcpToolConfiguration` 会扫描并注册现有 `@Tool` Bean。当前保持保守策略：默认扫描 `com.bytedesk` 包下工具，但只开放只读查询类工具。

```properties
bytedesk.ai.mcp.tools.enabled=true
bytedesk.ai.mcp.tools.read-only=true
bytedesk.ai.mcp.tools.include-packages=com.bytedesk
bytedesk.ai.mcp.tools.read-only-include-pattern=.*(Query|Search|Find|Get|List|Count).*
bytedesk.ai.mcp.tools.exclude-pattern=.*(Create|Update|Delete|Remove|Cancel|Change|Optimize|Reset|Score|Set|Send).*
```

如果需要缩小暴露范围，可以把 `bytedesk.ai.mcp.tools.include-packages` 改成更具体的包前缀，例如 `com.bytedesk.ai`、`com.bytedesk.service`。写操作需要先接入认证、组织隔离、审批和审计。

## 验证

编译 enterprise AI 模块：

```bash
env JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home ./starter/mvnw -f pom.xml -pl enterprise/ai -am -DskipTests compile
```

## 安全建议

- 默认保持 MCP 关闭，需要外部 Agent 集成时再开启。
- 初期保持 `read-only=true`，先开放查询能力。
- 不要在未接入 token 认证、组织隔离、审批、审计前开放创建、更新、删除、发送消息等写操作。
- 工具响应中避免返回密码、token、secret、license、内部配置等敏感字段。
