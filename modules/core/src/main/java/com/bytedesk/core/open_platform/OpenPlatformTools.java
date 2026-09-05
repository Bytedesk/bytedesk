package com.bytedesk.core.open_platform;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OpenPlatformTools extends BaseTools<OpenPlatformRequest, OpenPlatformResponse> {

    public OpenPlatformTools(OpenPlatformRestService restService, ObjectMapper objectMapper) {
        super("open_platform", OpenPlatformRequest.class, restService, objectMapper);
    }

    @Tool(name = "open_platform_query_by_uid", description = "Query open_platform by uid. This tool returns structured data for AI tool invocation.")
    public Object openPlatformQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "open_platform_query_by_org", description = "Query open_platform by org with request json. This tool returns structured data for AI tool invocation.")
    public Object openPlatformQueryByOrg(@ToolParam(description = "OpenPlatformRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "open_platform_query_by_user", description = "Query open_platform by user with request json. This tool returns structured data for AI tool invocation.")
    public Object openPlatformQueryByUser(@ToolParam(description = "OpenPlatformRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "open_platform_create", description = "Create open_platform with request json. This tool returns structured data for AI tool invocation.")
    public Object openPlatformCreate(@ToolParam(description = "OpenPlatformRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "open_platform_update", description = "Update open_platform with request json. This tool returns structured data for AI tool invocation.")
    public Object openPlatformUpdate(@ToolParam(description = "OpenPlatformRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "open_platform_delete_by_uid", description = "Delete open_platform by uid. This tool returns structured data for AI tool invocation.")
    public Object openPlatformDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
