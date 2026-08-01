package com.bytedesk.core.apns_p12;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ApnsP12Tools extends BaseTools<ApnsP12Request, ApnsP12Response> {

    public ApnsP12Tools(ApnsP12RestService restService, ObjectMapper objectMapper) {
        super("apns_p12", ApnsP12Request.class, restService, objectMapper);
    }

    @Tool(description = "Query apns_p12 by uid. This tool returns structured data for AI tool invocation.")
    public Object apns_p12QueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query apns_p12 by org with request json. This tool returns structured data for AI tool invocation.")
    public Object apns_p12QueryByOrg(@ToolParam(description = "ApnsP12Request json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query apns_p12 by user with request json. This tool returns structured data for AI tool invocation.")
    public Object apns_p12QueryByUser(@ToolParam(description = "ApnsP12Request json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create apns_p12 with request json. This tool returns structured data for AI tool invocation.")
    public Object apns_p12Create(@ToolParam(description = "ApnsP12Request json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update apns_p12 with request json. This tool returns structured data for AI tool invocation.")
    public Object apns_p12Update(@ToolParam(description = "ApnsP12Request json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete apns_p12 by uid. This tool returns structured data for AI tool invocation.")
    public Object apns_p12DeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
