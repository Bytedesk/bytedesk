package com.bytedesk.ticket.bug;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BugTools extends BaseTools<BugRequest, BugResponse> {

    public BugTools(BugRestService restService, ObjectMapper objectMapper) {
        super("bug", BugRequest.class, restService, objectMapper);
    }

    @Tool(name = "bug_query_by_uid", description = "Query bug by uid. This tool returns structured data for AI tool invocation.")
    public Object bugQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "bug_query_by_org", description = "Query bug by org with request json. This tool returns structured data for AI tool invocation.")
    public Object bugQueryByOrg(@ToolParam(description = "BugRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "bug_query_by_user", description = "Query bug by user with request json. This tool returns structured data for AI tool invocation.")
    public Object bugQueryByUser(@ToolParam(description = "BugRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "bug_create", description = "Create bug with request json. This tool returns structured data for AI tool invocation.")
    public Object bugCreate(@ToolParam(description = "BugRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "bug_update", description = "Update bug with request json. This tool returns structured data for AI tool invocation.")
    public Object bugUpdate(@ToolParam(description = "BugRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "bug_delete_by_uid", description = "Delete bug by uid. This tool returns structured data for AI tool invocation.")
    public Object bugDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
