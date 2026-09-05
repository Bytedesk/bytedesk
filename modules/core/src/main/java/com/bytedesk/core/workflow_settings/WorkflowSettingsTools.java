package com.bytedesk.core.workflow_settings;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WorkflowSettingsTools extends BaseTools<WorkflowSettingsRequest, WorkflowSettingsResponse> {

    public WorkflowSettingsTools(WorkflowSettingsRestService restService, ObjectMapper objectMapper) {
        super("workflow_settings", WorkflowSettingsRequest.class, restService, objectMapper);
    }

    @Tool(name = "workflow_settings_query_by_uid", description = "Query workflow_settings by uid. This tool returns structured data for AI tool invocation.")
    public Object workflow_settingsQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "workflow_settings_query_by_org", description = "Query workflow_settings by org with request json")
    public Object workflow_settingsQueryByOrg(@ToolParam(description = "WorkflowSettingsRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "workflow_settings_query_by_user", description = "Query workflow_settings by user with request json")
    public Object workflow_settingsQueryByUser(@ToolParam(description = "WorkflowSettingsRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "workflow_settings_create", description = "Create workflow_settings with request json. This tool returns structured data for AI tool invocation.")
    public Object workflow_settingsCreate(@ToolParam(description = "WorkflowSettingsRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "workflow_settings_update", description = "Update workflow_settings with request json. This tool returns structured data for AI tool invocation.")
    public Object workflow_settingsUpdate(@ToolParam(description = "WorkflowSettingsRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "workflow_settings_delete_by_uid", description = "Delete workflow_settings by uid. This tool returns structured data for AI tool invocation.")
    public Object workflow_settingsDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
