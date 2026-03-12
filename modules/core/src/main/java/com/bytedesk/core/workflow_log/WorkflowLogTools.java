package com.bytedesk.core.workflow_log;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WorkflowLogTools extends BaseTools<WorkflowLogRequest, WorkflowLogResponse> {

    public WorkflowLogTools(WorkflowLogRestService restService, ObjectMapper objectMapper) {
        super("workflowLog", WorkflowLogRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query workflowLog by uid")
    public Object workflowLogQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query workflowLog by org with request json")
    public Object workflowLogQueryByOrg(@ToolParam(description = "WorkflowLogRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query workflowLog by user with request json")
    public Object workflowLogQueryByUser(@ToolParam(description = "WorkflowLogRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create workflowLog with request json")
    public Object workflowLogCreate(@ToolParam(description = "WorkflowLogRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update workflowLog with request json")
    public Object workflowLogUpdate(@ToolParam(description = "WorkflowLogRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete workflowLog by uid")
    public Object workflowLogDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
