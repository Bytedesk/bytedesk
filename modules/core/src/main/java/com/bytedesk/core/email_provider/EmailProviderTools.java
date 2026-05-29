package com.bytedesk.core.email_provider;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class EmailProviderTools extends BaseTools<EmailProviderRequest, EmailProviderResponse> {

    public EmailProviderTools(EmailProviderRestService restService, ObjectMapper objectMapper) {
        super("email", EmailProviderRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query email by uid")
    public Object emailQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query email by org with request json")
    public Object emailQueryByOrg(@ToolParam(description = "EmailProviderRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query email by user with request json")
    public Object emailQueryByUser(@ToolParam(description = "EmailProviderRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create email with request json")
    public Object emailCreate(@ToolParam(description = "EmailProviderRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update email with request json")
    public Object emailUpdate(@ToolParam(description = "EmailProviderRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete email by uid")
    public Object emailDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
