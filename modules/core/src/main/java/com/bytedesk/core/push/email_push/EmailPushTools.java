package com.bytedesk.core.push.email_push;

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

    @Tool(description = "Query email_push by uid")
    public Object email_pushQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query email_push by org with request json")
    public Object email_pushQueryByOrg(@ToolParam(description = "EmailPushRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query email_push by user with request json")
    public Object email_pushQueryByUser(@ToolParam(description = "EmailPushRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create email_push with request json")
    public Object email_pushCreate(@ToolParam(description = "EmailPushRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update email_push with request json")
    public Object email_pushUpdate(@ToolParam(description = "EmailPushRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete email_push by uid")
    public Object email_pushDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
