package com.bytedesk.core.push.apns_token;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ApnsTokenTools extends BaseTools<ApnsTokenRequest, ApnsTokenResponse> {

    public ApnsTokenTools(ApnsTokenRestService restService, ObjectMapper objectMapper) {
        super("apns_token", ApnsTokenRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query apns_token by uid. This tool returns structured data for AI tool invocation.")
    public Object apns_tokenQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query apns_token by org with request json. This tool returns structured data for AI tool invocation.")
    public Object apns_tokenQueryByOrg(@ToolParam(description = "ApnsTokenRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query apns_token by user with request json. This tool returns structured data for AI tool invocation.")
    public Object apns_tokenQueryByUser(@ToolParam(description = "ApnsTokenRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create apns_token with request json. This tool returns structured data for AI tool invocation.")
    public Object apns_tokenCreate(@ToolParam(description = "ApnsTokenRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update apns_token with request json. This tool returns structured data for AI tool invocation.")
    public Object apns_tokenUpdate(@ToolParam(description = "ApnsTokenRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete apns_token by uid. This tool returns structured data for AI tool invocation.")
    public Object apns_tokenDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
