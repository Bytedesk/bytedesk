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

    @Tool(description = "Query organization_settings by uid")
    public Object organization_settingsQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query organization_settings by org with request json")
    public Object organization_settingsQueryByOrg(@ToolParam(description = "OrganizationSettingsRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query organization_settings by user with request json")
    public Object organization_settingsQueryByUser(@ToolParam(description = "OrganizationSettingsRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create organization_settings with request json")
    public Object organization_settingsCreate(@ToolParam(description = "OrganizationSettingsRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update organization_settings with request json")
    public Object organization_settingsUpdate(@ToolParam(description = "OrganizationSettingsRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete organization_settings by uid")
    public Object organization_settingsDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
