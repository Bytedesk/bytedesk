package com.bytedesk.ai.mcp_server;

import lombok.Data;
import java.util.Map;

/**
 * 简化的工具调用响应类
 * 用于替代 Spring AI MCP 中缺失的 ToolCallResponse
 */
@Data
public class ToolCallResponse {
    
    /**
     * 响应状态
     */
    private boolean success;
    
    /**
     * 响应数据
     */
    private Object data;
    
    /**
     * 错误消息
     */
    private String errorMessage;
    
    /**
     * 响应元数据
     */
    private Map<String, Object> metadata;
    
    /**
     * 请求ID
     */
    private String requestId;
    
    /**
     * 执行时间（毫秒）
     */
    private long executionTime;
    
    public ToolCallResponse() {}
    
    public ToolCallResponse(boolean success, Object data) {
        this.success = success;
        this.data = data;
    }
    
    public ToolCallResponse(boolean success, Object data, String requestId) {
        this.success = success;
        this.data = data;
        this.requestId = requestId;
    }
    
    /**
     * 创建成功响应
     */
    public static ToolCallResponse success(Object data) {
        return new ToolCallResponse(true, data);
    }
    
    /**
     * 创建失败响应
     */
    public static ToolCallResponse error(String errorMessage) {
        ToolCallResponse response = new ToolCallResponse();
        response.setSuccess(false);
        response.setErrorMessage(errorMessage);
        return response;
    }
    
    /**
     * 创建成功响应（带请求ID）
     */
    public static ToolCallResponse success(Object data, String requestId) {
        return new ToolCallResponse(true, data, requestId);
    }
    
    /**
     * 创建失败响应（带请求ID）
     */
    public static ToolCallResponse error(String errorMessage, String requestId) {
        ToolCallResponse response = new ToolCallResponse();
        response.setSuccess(false);
        response.setErrorMessage(errorMessage);
        response.setRequestId(requestId);
        return response;
    }
}
