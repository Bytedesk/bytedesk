package com.bytedesk.core.push_settings;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PushSettingsTools extends BaseTools<PushSettingsRequest, PushSettingsResponse> {

    public PushSettingsTools(PushSettingsRestService restService, ObjectMapper objectMapper) {
        super("push_settings", PushSettingsRequest.class, restService, objectMapper);
    }

    @Tool(name = "push_settings_query_by_uid", description = "Query push_settings by uid. This tool returns structured data for AI tool invocation.")
    public Object push_settingsQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "push_settings_query_by_org", description = "Query push_settings by org with request json. This tool returns structured data for AI tool invocation.")
    public Object push_settingsQueryByOrg(@ToolParam(description = "PushSettingsRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "push_settings_query_by_user", description = "Query push_settings by user with request json. This tool returns structured data for AI tool invocation.")
    public Object push_settingsQueryByUser(@ToolParam(description = "PushSettingsRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "push_settings_create", description = "Create push_settings with request json. This tool returns structured data for AI tool invocation.")
    public Object push_settingsCreate(@ToolParam(description = "PushSettingsRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "push_settings_update", description = "Update push_settings with request json. This tool returns structured data for AI tool invocation.")
    public Object push_settingsUpdate(@ToolParam(description = "PushSettingsRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "push_settings_delete_by_uid", description = "Delete push_settings by uid. This tool returns structured data for AI tool invocation.")
    public Object push_settingsDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
