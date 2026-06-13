package com.bytedesk.core.group;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class GroupTools extends BaseTools<GroupRequest, GroupResponse> {

    public GroupTools(GroupRestService restService, ObjectMapper objectMapper) {
        super("group", GroupRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query group by uid. This tool returns structured data for AI tool invocation.")
    public Object groupQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query group by org with request json. This tool returns structured data for AI tool invocation.")
    public Object groupQueryByOrg(@ToolParam(description = "GroupRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query group by user with request json. This tool returns structured data for AI tool invocation.")
    public Object groupQueryByUser(@ToolParam(description = "GroupRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create group with request json. This tool returns structured data for AI tool invocation.")
    public Object groupCreate(@ToolParam(description = "GroupRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update group with request json. This tool returns structured data for AI tool invocation.")
    public Object groupUpdate(@ToolParam(description = "GroupRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete group by uid. This tool returns structured data for AI tool invocation.")
    public Object groupDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
