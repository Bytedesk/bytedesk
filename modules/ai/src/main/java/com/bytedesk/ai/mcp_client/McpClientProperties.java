package com.bytedesk.ai.mcp_client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "bytedesk.ai.mcp.client")
public class McpClientProperties {

    /**
     * Whether external MCP client capabilities are enabled.
     */
    private boolean enabled = false;

    /**
     * Maximum number of concurrently managed external MCP connections.
     */
    private int maxConnections = 10;

    /**
     * Default connection timeout in milliseconds.
     */
    private int connectionTimeoutMs = 30000;

    /**
     * Default request timeout in milliseconds.
     */
    private int requestTimeoutMs = 60000;

    /**
     * Health check interval in milliseconds.
     */
    private int healthCheckIntervalMs = 60000;
}