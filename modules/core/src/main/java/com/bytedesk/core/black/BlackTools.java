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

    @Tool(description = "Query black by uid")
    public Object blackQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query black by org with request json")
    public Object blackQueryByOrg(@ToolParam(description = "BlackRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query black by user with request json")
    public Object blackQueryByUser(@ToolParam(description = "BlackRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create black with request json")
    public Object blackCreate(@ToolParam(description = "BlackRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update black with request json")
    public Object blackUpdate(@ToolParam(description = "BlackRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete black by uid")
    public Object blackDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
