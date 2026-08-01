package com.bytedesk.ai.tool;

import com.bytedesk.core.base.BaseTools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ToolTools extends BaseTools<ToolRequest, ToolResponse> {

    public ToolTools(ToolRestService restService, ObjectMapper objectMapper) {
        super("tool", ToolRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query tool by uid. This tool returns structured data for AI tool invocation.")
    public Object toolQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query tool by org with request json. This tool returns structured data for AI tool invocation.")
    public Object toolQueryByOrg(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query tool by user with request json. This tool returns structured data for AI tool invocation.")
    public Object toolQueryByUser(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create tool with request json. This tool returns structured data for AI tool invocation.")
    public Object toolCreate(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update tool with request json. This tool returns structured data for AI tool invocation.")
    public Object toolUpdate(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete tool by uid. This tool returns structured data for AI tool invocation.")
    public Object toolDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
