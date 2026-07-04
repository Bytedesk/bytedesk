package com.bytedesk.ai.mcp_server;

import com.bytedesk.core.base.BaseTools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class McpServerTools extends BaseTools<McpServerRequest, McpServerResponse> {

    public McpServerTools(McpServerRestService restService, ObjectMapper objectMapper) {
        super("mcpServer", McpServerRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query mcpServer by uid. This tool returns structured data for AI tool invocation.")
    public Object mcpServerQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query mcpServer by org with request json. This tool returns structured data for AI tool invocation.")
    public Object mcpServerQueryByOrg(@ToolParam(description = "McpServerRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query mcpServer by user with request json. This tool returns structured data for AI tool invocation.")
    public Object mcpServerQueryByUser(@ToolParam(description = "McpServerRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create mcpServer with request json. This tool returns structured data for AI tool invocation.")
    public Object mcpServerCreate(@ToolParam(description = "McpServerRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update mcpServer with request json. This tool returns structured data for AI tool invocation.")
    public Object mcpServerUpdate(@ToolParam(description = "McpServerRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete mcpServer by uid. This tool returns structured data for AI tool invocation.")
    public Object mcpServerDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
