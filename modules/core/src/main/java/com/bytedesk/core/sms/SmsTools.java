package com.bytedesk.core.sms;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class SmsTools extends BaseTools<SmsRequest, SmsResponse> {

    public SmsTools(SmsRestService restService, ObjectMapper objectMapper) {
        super("sms", SmsRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query sms by uid")
    public Object smsQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query sms by org with request json")
    public Object smsQueryByOrg(@ToolParam(description = "SmsRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query sms by user with request json")
    public Object smsQueryByUser(@ToolParam(description = "SmsRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create sms with request json")
    public Object smsCreate(@ToolParam(description = "SmsRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update sms with request json")
    public Object smsUpdate(@ToolParam(description = "SmsRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete sms by uid")
    public Object smsDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
