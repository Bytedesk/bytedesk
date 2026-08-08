package com.bytedesk.ai.tool_call;

import com.bytedesk.core.base.BaseTools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ToolCallTools extends BaseTools<ToolCallRequest, ToolCallResponse> {

    public ToolCallTools(ToolCallRestService restService, ObjectMapper objectMapper) {
        super("tool_call", ToolCallRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query tool_call by uid. This tool returns structured data for AI tool invocation.")
    public Object tool_callQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query tool_call by org with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_callQueryByOrg(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query tool_call by user with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_callQueryByUser(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create tool_call with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_callCreate(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update tool_call with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_callUpdate(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete tool_call by uid. This tool returns structured data for AI tool invocation.")
    public Object tool_callDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
