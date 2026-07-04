package com.bytedesk.ai.mcp_client;

import com.bytedesk.core.base.BaseTools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class McpClientTools extends BaseTools<McpClientRequest, McpClientResponse> {

    public McpClientTools(McpClientRestService restService, ObjectMapper objectMapper) {
        super("mcpClient", McpClientRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query mcpClient by uid. This tool returns structured data for AI tool invocation.")
    public Object mcpClientQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query mcpClient by org with request json. This tool returns structured data for AI tool invocation.")
    public Object mcpClientQueryByOrg(@ToolParam(description = "McpClientRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query mcpClient by user with request json. This tool returns structured data for AI tool invocation.")
    public Object mcpClientQueryByUser(@ToolParam(description = "McpClientRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create mcpClient with request json. This tool returns structured data for AI tool invocation.")
    public Object mcpClientCreate(@ToolParam(description = "McpClientRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update mcpClient with request json. This tool returns structured data for AI tool invocation.")
    public Object mcpClientUpdate(@ToolParam(description = "McpClientRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete mcpClient by uid. This tool returns structured data for AI tool invocation.")
    public Object mcpClientDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
