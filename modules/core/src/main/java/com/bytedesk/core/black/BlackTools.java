package com.bytedesk.core.black;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BlackTools extends BaseTools<BlackRequest, BlackResponse> {

    public BlackTools(BlackRestService restService, ObjectMapper objectMapper) {
        super("black", BlackRequest.class, restService, objectMapper);
    }

    @Tool(name = "black_query_by_uid", description = "Query black by uid. This tool returns structured data for AI tool invocation.")
    public Object blackQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "black_query_by_org", description = "Query black by org with request json. This tool returns structured data for AI tool invocation.")
    public Object blackQueryByOrg(@ToolParam(description = "BlackRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "black_query_by_user", description = "Query black by user with request json. This tool returns structured data for AI tool invocation.")
    public Object blackQueryByUser(@ToolParam(description = "BlackRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "black_create", description = "Create black with request json. This tool returns structured data for AI tool invocation.")
    public Object blackCreate(@ToolParam(description = "BlackRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "black_update", description = "Update black with request json. This tool returns structured data for AI tool invocation.")
    public Object blackUpdate(@ToolParam(description = "BlackRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "black_delete_by_uid", description = "Delete black by uid. This tool returns structured data for AI tool invocation.")
    public Object blackDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
