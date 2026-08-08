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
spring.ai.mcp.server.protocol=STREAMABLE
```

本地 starter 默认端口为 `9003`，在 Mcp-Inspector 中选择 Streamable HTTP，因此 MCP Client 可连接：

```text
http://127.0.0.1:9003/mcp
```

## 工具开放策略

当前不再通过包扫描白名单直接决定 MCP 暴露，而是以平台注册表作为唯一治理入口。代码里的 Spring AI `@Tool` 方法和 `ToolCallback` Bean 会先同步到 `ToolEntity`，再由 MCP bridge 按工具治理字段决定是否真正对外暴露。

```properties
bytedesk.ai.mcp.tools.enabled=true
```

治理规则如下：

- `enabled=false`：工具不会进入 MCP bridge。
- `mcpExposureMode=NONE`：工具仅对内，不对外暴露。
- `mcpExposureMode=READONLY`：只有运行时工具名符合 `Query`、`Search`、`Find`、`Get`、`List`、`Count` 等查询类约定时才对外暴露。
- `mcpExposureMode=DUAL`：允许该工具对外 MCP 暴露，不受只读命名约束。
- `allowedMethods`：可进一步限制允许暴露的方法名或运行时工具名，支持逗号或换行分隔多个值。

建议通过后台 ToolTable 管理平台注册工具，并在代码变更后执行“刷新平台注册表”，保持代码定义和治理配置一致。写操作仍需要认证、组织隔离、审批和审计。

## 验证

编译 enterprise AI 模块：

```bash
env JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home ./starter/mvnw -f pom.xml -pl enterprise/ai -am -DskipTests compile
```

## 安全建议

- 默认保持 MCP 关闭，需要外部 Agent 集成时再开启。
- 初期优先保持 `mcpExposureMode=READONLY`，先开放查询能力。
- 不要在未接入 token 认证、组织隔离、审批、审计前开放创建、更新、删除、发送消息等写操作。
- 工具响应中避免返回密码、token、secret、license、内部配置等敏感字段。
