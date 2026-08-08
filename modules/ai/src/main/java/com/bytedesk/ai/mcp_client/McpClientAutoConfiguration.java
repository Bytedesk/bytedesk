package com.bytedesk.ai.mcp_client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(McpClientProperties.class)
@ConditionalOnProperty(prefix = "bytedesk.ai.mcp.client", name = "enabled", havingValue = "true")
public class McpClientAutoConfiguration {

}