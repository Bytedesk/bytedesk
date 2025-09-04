package com.bytedesk.ai.mcp_server;

import lombok.Data;
import java.util.Map;

/**
 * 简化的 MCP 提示类
 * 用于替代 Spring AI MCP 中缺失的 McpPrompt
 */
@Data
public class McpPrompt {
    
    /**
     * 提示名称
     */
    private String name;
    
    /**
     * 提示描述
     */
    private String description;
    
    /**
     * 提示模板
     */
    private String template;
    
    /**
     * 提示参数
     */
    private Map<String, Object> arguments;
    
    /**
     * 提示标签
     */
    private String[] tags;
    
    public McpPrompt() {}
    
    public McpPrompt(String name, String description) {
        this.name = name;
        this.description = description;
    }
    
    public McpPrompt(String name, String description, String template) {
        this.name = name;
        this.description = description;
        this.template = template;
    }
}
