# MCP

Bytedesk can expose selected Spring AI tools as a Model Context Protocol (MCP) server for third-party agents and MCP clients.

## Enable

The MCP server is disabled by default because it exposes application capabilities to external clients.

```properties
spring.ai.mcp.server.enabled=true
spring.ai.mcp.server.name=bytedesk-mcp-server
spring.ai.mcp.server.type=SYNC
spring.ai.mcp.server.stdio=false
spring.ai.mcp.server.sse-message-endpoint=/mcp/message
```

Spring AI WebMVC MCP uses SSE transport by default:

- SSE endpoint: `/sse`
- Message endpoint: `/mcp/message`

## Tool Exposure

Bytedesk now uses the platform tool registry as the single MCP exposure control plane.
Code-defined Spring AI `@Tool` methods and `ToolCallback` beans are first synchronized into `ToolEntity`, and then the MCP bridge decides whether they are exposed externally.

```properties
bytedesk.ai.mcp.tools.enabled=true
```

Registry governance rules:

- `enabled=false`: the tool is not available to the MCP bridge.
- `mcpExposureMode=NONE`: the tool stays internal only.
- `mcpExposureMode=READONLY`: the tool is exposed only when its runtime name follows the read-oriented naming convention such as `Query`, `Search`, `Find`, `Get`, `List`, or `Count`.
- `mcpExposureMode=DUAL`: the tool may be exposed to MCP even if it is not read-only.
- `allowedMethods`: optional comma-separated or line-separated runtime tool names/method names for additional narrowing.

Use the admin ToolTable to review and edit platform tool governance after running the platform sync action. Keep write operations behind permission, approval, and audit controls.

## Verification

Compile the enterprise AI module:

```bash
env JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home ./starter/mvnw -f pom.xml -pl enterprise/ai -am -DskipTests compile
```

When the starter application is running with MCP enabled, configure an MCP client to connect to:

```text
http://127.0.0.1:9003/sse
```

## Security Notes

- Keep MCP disabled unless an external agent integration is required.
- Prefer `mcpExposureMode=READONLY` for initial external exposure.
- Do not expose write tools without token-based authentication, organization scoping, approval, and audit logging.
- Avoid returning secrets, tokens, licenses, passwords, or internal configuration fields from tool responses.
