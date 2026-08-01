package com.bytedesk.core.open_platform;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OpenPlatformTools extends BaseTools<OpenPlatformRequest, OpenPlatformResponse> {

    public OpenPlatformTools(OpenPlatformRestService restService, ObjectMapper objectMapper) {
        super("openPlatform", OpenPlatformRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query openPlatform by uid. This tool returns structured data for AI tool invocation.")
    public Object openPlatformQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query openPlatform by org with request json. This tool returns structured data for AI tool invocation.")
    public Object openPlatformQueryByOrg(@ToolParam(description = "OpenPlatformRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query openPlatform by user with request json. This tool returns structured data for AI tool invocation.")
    public Object openPlatformQueryByUser(@ToolParam(description = "OpenPlatformRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create openPlatform with request json. This tool returns structured data for AI tool invocation.")
    public Object openPlatformCreate(@ToolParam(description = "OpenPlatformRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update openPlatform with request json. This tool returns structured data for AI tool invocation.")
    public Object openPlatformUpdate(@ToolParam(description = "OpenPlatformRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete openPlatform by uid. This tool returns structured data for AI tool invocation.")
    public Object openPlatformDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
