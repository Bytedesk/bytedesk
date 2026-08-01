package com.bytedesk.ai.tool_guard;

import com.bytedesk.core.base.BaseTools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ToolGuardTools extends BaseTools<ToolGuardRequest, ToolGuardResponse> {

    public ToolGuardTools(ToolGuardRestService restService, ObjectMapper objectMapper) {
        super("tool_guard", ToolGuardRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query tool_guard by uid. This tool returns structured data for AI tool invocation.")
    public Object tool_guardQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query tool_guard by org with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_guardQueryByOrg(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query tool_guard by user with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_guardQueryByUser(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create tool_guard with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_guardCreate(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update tool_guard with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_guardUpdate(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete tool_guard by uid. This tool returns structured data for AI tool invocation.")
    public Object tool_guardDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
