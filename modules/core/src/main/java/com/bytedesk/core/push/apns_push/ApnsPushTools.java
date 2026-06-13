package com.bytedesk.core.push.apns_push;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ApnsPushTools extends BaseTools<ApnsPushRequest, ApnsPushResponse> {

    public ApnsPushTools(ApnsPushRestService restService, ObjectMapper objectMapper) {
        super("apns_push", ApnsPushRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query apns_push by uid. This tool returns structured data for AI tool invocation.")
    public Object apns_pushQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query apns_push by org with request json. This tool returns structured data for AI tool invocation.")
    public Object apns_pushQueryByOrg(@ToolParam(description = "ApnsPushRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query apns_push by user with request json. This tool returns structured data for AI tool invocation.")
    public Object apns_pushQueryByUser(@ToolParam(description = "ApnsPushRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create apns_push with request json. This tool returns structured data for AI tool invocation.")
    public Object apns_pushCreate(@ToolParam(description = "ApnsPushRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update apns_push with request json. This tool returns structured data for AI tool invocation.")
    public Object apns_pushUpdate(@ToolParam(description = "ApnsPushRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete apns_push by uid. This tool returns structured data for AI tool invocation.")
    public Object apns_pushDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
