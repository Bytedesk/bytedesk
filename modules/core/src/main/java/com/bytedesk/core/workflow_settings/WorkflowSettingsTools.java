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

    @Tool(description = "Query workflow_settings by uid")
    public Object workflow_settingsQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query workflow_settings by org with request json")
    public Object workflow_settingsQueryByOrg(@ToolParam(description = "WorkflowSettingsRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query workflow_settings by user with request json")
    public Object workflow_settingsQueryByUser(@ToolParam(description = "WorkflowSettingsRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create workflow_settings with request json")
    public Object workflow_settingsCreate(@ToolParam(description = "WorkflowSettingsRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update workflow_settings with request json")
    public Object workflow_settingsUpdate(@ToolParam(description = "WorkflowSettingsRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete workflow_settings by uid")
    public Object workflow_settingsDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
