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

    @Tool(description = "Query bug by uid")
    public Object bugQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query bug by org with request json")
    public Object bugQueryByOrg(@ToolParam(description = "BugRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query bug by user with request json")
    public Object bugQueryByUser(@ToolParam(description = "BugRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create bug with request json")
    public Object bugCreate(@ToolParam(description = "BugRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update bug with request json")
    public Object bugUpdate(@ToolParam(description = "BugRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete bug by uid")
    public Object bugDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
