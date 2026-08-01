package com.bytedesk.ai.tool_rule;

import com.bytedesk.core.base.BaseTools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ToolRuleTools extends BaseTools<ToolRuleRequest, ToolRuleResponse> {

    public ToolRuleTools(ToolRuleRestService restService, ObjectMapper objectMapper) {
        super("tool_rule", ToolRuleRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query tool_rule by uid. This tool returns structured data for AI tool invocation.")
    public Object tool_ruleQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query tool_rule by org with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_ruleQueryByOrg(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query tool_rule by user with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_ruleQueryByUser(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create tool_rule with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_ruleCreate(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update tool_rule with request json. This tool returns structured data for AI tool invocation.")
    public Object tool_ruleUpdate(@ToolParam(description = "ToolRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete tool_rule by uid. This tool returns structured data for AI tool invocation.")
    public Object tool_ruleDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
