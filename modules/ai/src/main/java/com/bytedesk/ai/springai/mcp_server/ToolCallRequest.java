package com.bytedesk.ai.springai.mcp_server;

import lombok.Data;
import java.util.Map;

/**
 * 简化的工具调用请求类
 * 用于替代 Spring AI MCP 中缺失的 ToolCallRequest
 */
@Data
public class ToolCallRequest {
    
    /**
     * 服务器UID
     */
    private String serverUid;
    
    /**
     * 工具名称
     */
    private String toolName;
    
    /**
     * 工具参数
     */
    private Map<String, Object> arguments;
    
    /**
     * 请求ID
     */
    private String requestId;
    
    /**
     * 调用上下文
     */
    private String context;
    
    public ToolCallRequest() {}
    
    public ToolCallRequest(String toolName, Map<String, Object> arguments) {
        this.toolName = toolName;
        this.arguments = arguments;
    }
    
    public ToolCallRequest(String toolName, Map<String, Object> arguments, String requestId) {
        this.toolName = toolName;
        this.arguments = arguments;
        this.requestId = requestId;
    }
    
    public ToolCallRequest(String serverUid, String toolName, Map<String, Object> arguments) {
        this.serverUid = serverUid;
        this.toolName = toolName;
        this.arguments = arguments;
    }
}
