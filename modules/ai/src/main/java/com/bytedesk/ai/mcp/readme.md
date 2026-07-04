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

Bytedesk registers existing Spring AI `@Tool` beans through `BytedeskMcpToolConfiguration`.
The first release is conservative and exposes only read-oriented tools from Bytedesk packages.

```properties
bytedesk.ai.mcp.tools.enabled=true
bytedesk.ai.mcp.tools.read-only=true
bytedesk.ai.mcp.tools.include-packages=com.bytedesk
bytedesk.ai.mcp.tools.read-only-include-pattern=.*(Query|Search|Find|Get|List|Count).*
bytedesk.ai.mcp.tools.exclude-pattern=.*(Create|Update|Delete|Remove|Cancel|Change|Optimize|Reset|Score|Set|Send).*
```

To narrow the exposure scope, replace `bytedesk.ai.mcp.tools.include-packages` with more specific package prefixes, such as `com.bytedesk.ai` or `com.bytedesk.service`. Keep write operations behind permission, approval, and audit controls.

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
- Keep `read-only=true` for initial deployments.
- Do not expose write tools without token-based authentication, organization scoping, approval, and audit logging.
- Avoid returning secrets, tokens, licenses, passwords, or internal configuration fields from tool responses.
