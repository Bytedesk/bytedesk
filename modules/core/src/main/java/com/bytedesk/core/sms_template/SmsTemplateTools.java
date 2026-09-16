package com.bytedesk.core.sms_template;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SmsTemplateTools extends BaseTools<SmsTemplateRequest, SmsTemplateResponse> {

    public SmsTemplateTools(SmsTemplateRestService restService, ObjectMapper objectMapper) {
        super("sms_template", SmsTemplateRequest.class, restService, objectMapper);
    }

    @Tool(name = "sms_template_query_by_uid", description = "Query sms_template by uid. This tool returns structured data for AI tool invocation.")
    public Object smsTemplateQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "sms_template_query_by_org", description = "Query sms_template by org with request json. This tool returns structured data for AI tool invocation.")
    public Object smsTemplateQueryByOrg(@ToolParam(description = "SmsTemplateRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "sms_template_query_by_user", description = "Query sms_template by user with request json. This tool returns structured data for AI tool invocation.")
    public Object smsTemplateQueryByUser(@ToolParam(description = "SmsTemplateRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "sms_template_create", description = "Create sms_template with request json. This tool returns structured data for AI tool invocation.")
    public Object smsTemplateCreate(@ToolParam(description = "SmsTemplateRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "sms_template_update", description = "Update sms_template with request json. This tool returns structured data for AI tool invocation.")
    public Object smsTemplateUpdate(@ToolParam(description = "SmsTemplateRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "sms_template_delete_by_uid", description = "Delete sms_template by uid. This tool returns structured data for AI tool invocation.")
    public Object smsTemplateDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
