package com.bytedesk.ai.tool_approval;

import com.bytedesk.core.base.BaseTools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ToolApprovalTools extends BaseTools<ToolApprovalRequest, ToolApprovalResponse> {

    public ToolApprovalTools(ToolApprovalRestService restService, ObjectMapper objectMapper) {
        super("tool_approval", ToolApprovalRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query tool_approval by uid. This tool returns structured data for AI tool invocation.")
    public Object tool_approvalQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query tool_approval by org with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_approvalQueryByOrg(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query tool_approval by user with request json")
    public Object tool_approvalQueryByUser(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create tool_approval with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_approvalCreate(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update tool_approval with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_approvalUpdate(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete tool_approval by uid. This tool returns structured data for AI tool invocation.")
    public Object tool_approvalDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
