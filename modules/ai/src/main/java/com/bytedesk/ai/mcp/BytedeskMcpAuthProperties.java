package com.bytedesk.ai.mcp;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "bytedesk.ai.mcp.auth")
public class BytedeskMcpAuthProperties {

    /**
     * Require Authorization: Bearer <token> for MCP SSE/message endpoints.
     */
    private boolean enabled = true;

    /**
     * Shared token used by external MCP clients. Keep empty to fail closed when auth is enabled.
     */
    private String bearerToken;

    private String sseEndpoint = "/sse";

    private String messageEndpoint = "/mcp/message";
}
