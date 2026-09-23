package com.bytedesk.core.push_apns;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PushApnsTools extends BaseTools<PushApnsRequest, PushApnsResponse> {

    public PushApnsTools(PushApnsRestService restService, ObjectMapper objectMapper) {
        super("apns_push", PushApnsRequest.class, restService, objectMapper);
    }

    @Tool(name = "apns_push_query_by_uid", description = "Query apns_push by uid. This tool returns structured data for AI tool invocation.")
    public Object apns_pushQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "apns_push_query_by_org", description = "Query apns_push by org with request json. This tool returns structured data for AI tool invocation.")
    public Object apns_pushQueryByOrg(@ToolParam(description = "PushApnsRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "apns_push_query_by_user", description = "Query apns_push by user with request json. This tool returns structured data for AI tool invocation.")
    public Object apns_pushQueryByUser(@ToolParam(description = "PushApnsRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "apns_push_create", description = "Create apns_push with request json. This tool returns structured data for AI tool invocation.")
    public Object apns_pushCreate(@ToolParam(description = "PushApnsRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "apns_push_update", description = "Update apns_push with request json. This tool returns structured data for AI tool invocation.")
    public Object apns_pushUpdate(@ToolParam(description = "PushApnsRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "apns_push_delete_by_uid", description = "Delete apns_push by uid. This tool returns structured data for AI tool invocation.")
    public Object apns_pushDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
