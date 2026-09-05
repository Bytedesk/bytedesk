package com.bytedesk.core.organization_settings;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class OrganizationSettingsTools extends BaseTools<OrganizationSettingsRequest, OrganizationSettingsResponse> {

    public OrganizationSettingsTools(OrganizationSettingsRestService restService, ObjectMapper objectMapper) {
        super("organization_settings", OrganizationSettingsRequest.class, restService, objectMapper);
    }

    @Tool(name = "organization_settings_query_by_uid", description = "Query organization_settings by uid. This tool returns structured data for AI tool invocation.")
    public Object organization_settingsQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "organization_settings_query_by_org", description = "Query organization_settings by org with request json")
    public Object organization_settingsQueryByOrg(@ToolParam(description = "OrganizationSettingsRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "organization_settings_query_by_user", description = "Query organization_settings by user with request json")
    public Object organization_settingsQueryByUser(@ToolParam(description = "OrganizationSettingsRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "organization_settings_create", description = "Create organization_settings with request json")
    public Object organization_settingsCreate(@ToolParam(description = "OrganizationSettingsRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "organization_settings_update", description = "Update organization_settings with request json")
    public Object organization_settingsUpdate(@ToolParam(description = "OrganizationSettingsRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "organization_settings_delete_by_uid", description = "Delete organization_settings by uid. This tool returns structured data for AI tool invocation.")
    public Object organization_settingsDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
