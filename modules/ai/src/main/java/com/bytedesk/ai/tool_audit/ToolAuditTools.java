package com.bytedesk.ai.tool_audit;

import com.bytedesk.core.base.BaseTools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ToolAuditTools extends BaseTools<ToolAuditRequest, ToolAuditResponse> {

    public ToolAuditTools(ToolAuditRestService restService, ObjectMapper objectMapper) {
        super("tool_audit", ToolAuditRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query tool_audit by uid. This tool returns structured data for AI tool invocation.")
    public Object tool_auditQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query tool_audit by org with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_auditQueryByOrg(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query tool_audit by user with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_auditQueryByUser(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create tool_audit with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_auditCreate(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update tool_audit with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_auditUpdate(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete tool_audit by uid. This tool returns structured data for AI tool invocation.")
    public Object tool_auditDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
