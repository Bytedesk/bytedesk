package com.bytedesk.core.sms_provider;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SmsProviderTools extends BaseTools<SmsProviderRequest, SmsProviderResponse> {

    public SmsProviderTools(SmsProviderRestService restService, ObjectMapper objectMapper) {
        super("sms_provider", SmsProviderRequest.class, restService, objectMapper);
    }

    @Tool(name = "sms_provider_query_by_uid", description = "Query sms_provider by uid. This tool returns structured data for AI tool invocation.")
    public Object smsProviderQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "sms_provider_query_by_org", description = "Query sms_provider by org with request json. This tool returns structured data for AI tool invocation.")
    public Object smsProviderQueryByOrg(@ToolParam(description = "SmsProviderRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "sms_provider_query_by_user", description = "Query sms_provider by user with request json. This tool returns structured data for AI tool invocation.")
    public Object smsProviderQueryByUser(@ToolParam(description = "SmsProviderRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "sms_provider_create", description = "Create sms_provider with request json. This tool returns structured data for AI tool invocation.")
    public Object smsProviderCreate(@ToolParam(description = "SmsProviderRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "sms_provider_update", description = "Update sms_provider with request json. This tool returns structured data for AI tool invocation.")
    public Object smsProviderUpdate(@ToolParam(description = "SmsProviderRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "sms_provider_delete_by_uid", description = "Delete sms_provider by uid. This tool returns structured data for AI tool invocation.")
    public Object smsProviderDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
