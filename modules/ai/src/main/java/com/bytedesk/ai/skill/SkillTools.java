package com.bytedesk.ai.skill;

import com.bytedesk.core.base.BaseTools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SkillTools extends BaseTools<SkillRequest, SkillResponse> {

    public SkillTools(SkillRestService restService, ObjectMapper objectMapper) {
        super("skill", SkillRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query skill by uid. This tool returns structured data for AI tool invocation.")
    public Object skillQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query skill by org with request json. This tool returns structured data for AI tool invocation.")
    public Object skillQueryByOrg(@ToolParam(description = "SkillRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query skill by user with request json. This tool returns structured data for AI tool invocation.")
    public Object skillQueryByUser(@ToolParam(description = "SkillRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create skill with request json. This tool returns structured data for AI tool invocation.")
    public Object skillCreate(@ToolParam(description = "SkillRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update skill with request json. This tool returns structured data for AI tool invocation.")
    public Object skillUpdate(@ToolParam(description = "SkillRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete skill by uid. This tool returns structured data for AI tool invocation.")
    public Object skillDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
