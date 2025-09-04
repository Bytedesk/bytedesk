package com.bytedesk.ai.mcp_server;

import lombok.Data;
import java.util.Map;

/**
 * 简化的 MCP 工具类
 * 用于替代 Spring AI MCP 中缺失的 McpTool
 */
@Data
public class McpTool {
    
    /**
     * 工具名称
     */
    private String name;
    
    /**
     * 工具描述
     */
    private String description;
    
    /**
     * 工具输入参数模式
     */
    private Map<String, Object> inputSchema;
    
    /**
     * 工具版本
     */
    private String version;
    
    /**
     * 工具是否可用
     */
    private boolean enabled = true;
    
    public McpTool() {}
    
    public McpTool(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public McpTool(String name, String description, Map<String, Object> inputSchema) {
        this.name = name;
        this.description = description;
        this.inputSchema = inputSchema;
    }
}
