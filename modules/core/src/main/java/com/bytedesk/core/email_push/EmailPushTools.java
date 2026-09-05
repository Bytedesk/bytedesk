package com.bytedesk.core.email_push;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class EmailPushTools extends BaseTools<EmailPushRequest, EmailPushResponse> {

    public EmailPushTools(EmailPushRestService restService, ObjectMapper objectMapper) {
        super("email_push", EmailPushRequest.class, restService, objectMapper);
    }

    @Tool(name = "email_push_query_by_uid", description = "Query email_push by uid. This tool returns structured data for AI tool invocation.")
    public Object email_pushQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "email_push_query_by_org", description = "Query email_push by org with request json. This tool returns structured data for AI tool invocation.")
    public Object email_pushQueryByOrg(@ToolParam(description = "EmailPushRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "email_push_query_by_user", description = "Query email_push by user with request json. This tool returns structured data for AI tool invocation.")
    public Object email_pushQueryByUser(@ToolParam(description = "EmailPushRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "email_push_create", description = "Create email_push with request json. This tool returns structured data for AI tool invocation.")
    public Object email_pushCreate(@ToolParam(description = "EmailPushRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "email_push_update", description = "Update email_push with request json. This tool returns structured data for AI tool invocation.")
    public Object email_pushUpdate(@ToolParam(description = "EmailPushRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "email_push_delete_by_uid", description = "Delete email_push by uid. This tool returns structured data for AI tool invocation.")
    public Object email_pushDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
