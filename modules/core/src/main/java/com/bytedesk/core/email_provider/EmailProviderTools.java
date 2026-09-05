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

    @Tool(name = "email_query_by_uid", description = "Query email by uid. This tool returns structured data for AI tool invocation.")
    public Object emailQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "email_query_by_org", description = "Query email by org with request json. This tool returns structured data for AI tool invocation.")
    public Object emailQueryByOrg(@ToolParam(description = "EmailProviderRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "email_query_by_user", description = "Query email by user with request json. This tool returns structured data for AI tool invocation.")
    public Object emailQueryByUser(@ToolParam(description = "EmailProviderRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "email_create", description = "Create email with request json. This tool returns structured data for AI tool invocation.")
    public Object emailCreate(@ToolParam(description = "EmailProviderRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "email_update", description = "Update email with request json. This tool returns structured data for AI tool invocation.")
    public Object emailUpdate(@ToolParam(description = "EmailProviderRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "email_delete_by_uid", description = "Delete email by uid. This tool returns structured data for AI tool invocation.")
    public Object emailDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
