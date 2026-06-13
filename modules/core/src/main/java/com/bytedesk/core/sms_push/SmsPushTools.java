package com.bytedesk.core.sms_push;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SmsPushTools extends BaseTools<SmsPushRequest, SmsPushResponse> {

    public SmsPushTools(SmsPushRestService restService, ObjectMapper objectMapper) {
        super("sms_push", SmsPushRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query sms_push by uid. This tool returns structured data for AI tool invocation.")
    public Object sms_pushQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query sms_push by org with request json. This tool returns structured data for AI tool invocation.")
    public Object sms_pushQueryByOrg(@ToolParam(description = "SmsPushRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query sms_push by user with request json. This tool returns structured data for AI tool invocation.")
    public Object sms_pushQueryByUser(@ToolParam(description = "SmsPushRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create sms_push with request json. This tool returns structured data for AI tool invocation.")
    public Object sms_pushCreate(@ToolParam(description = "SmsPushRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update sms_push with request json. This tool returns structured data for AI tool invocation.")
    public Object sms_pushUpdate(@ToolParam(description = "SmsPushRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete sms_push by uid. This tool returns structured data for AI tool invocation.")
    public Object sms_pushDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
