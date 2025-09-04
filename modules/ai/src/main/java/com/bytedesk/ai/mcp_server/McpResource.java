package com.bytedesk.ai.mcp_server;

import lombok.Data;
import java.util.Map;

/**
 * 简化的 MCP 资源类
 * 用于替代 Spring AI MCP 中缺失的 McpResource
 */
@Data
public class McpResource {
    
    /**
     * 资源URI
     */
    private String uri;
    
    /**
     * 资源名称
     */
    private String name;
    
    /**
     * 资源描述
     */
    private String description;
    
    /**
     * 资源内容类型
     */
    private String mimeType;
    
    /**
     * 资源元数据
     */
    private Map<String, Object> metadata;
    
    /**
     * 资源大小（字节）
     */
    private Long size;
    
    public McpResource() {}
    
    public McpResource(String uri, String name) {
        this.uri = uri;
        this.name = name;
    }
    
    public McpResource(String uri, String name, String description) {
        this.uri = uri;
        this.name = name;
        this.description = description;
    }
}
