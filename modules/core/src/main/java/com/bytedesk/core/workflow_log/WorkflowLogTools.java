package com.bytedesk.core.workflow_log;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WorkflowLogTools extends BaseTools<WorkflowLogRequest, WorkflowLogResponse> {

    public WorkflowLogTools(WorkflowLogRestService restService, ObjectMapper objectMapper) {
        super("workflow_log", WorkflowLogRequest.class, restService, objectMapper);
    }

    @Tool(name = "workflow_log_query_by_uid", description = "Query workflow_log by uid. This tool returns structured data for AI tool invocation.")
    public Object workflowLogQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "workflow_log_query_by_org", description = "Query workflow_log by org with request json. This tool returns structured data for AI tool invocation.")
    public Object workflowLogQueryByOrg(@ToolParam(description = "WorkflowLogRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "workflow_log_query_by_user", description = "Query workflow_log by user with request json. This tool returns structured data for AI tool invocation.")
    public Object workflowLogQueryByUser(@ToolParam(description = "WorkflowLogRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "workflow_log_create", description = "Create workflow_log with request json. This tool returns structured data for AI tool invocation.")
    public Object workflowLogCreate(@ToolParam(description = "WorkflowLogRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "workflow_log_update", description = "Update workflow_log with request json. This tool returns structured data for AI tool invocation.")
    public Object workflowLogUpdate(@ToolParam(description = "WorkflowLogRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "workflow_log_delete_by_uid", description = "Delete workflow_log by uid. This tool returns structured data for AI tool invocation.")
    public Object workflowLogDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
