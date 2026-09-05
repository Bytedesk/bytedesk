package com.bytedesk.core.workflow_edge;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WorkflowEdgeTools extends BaseTools<WorkflowEdgeRequest, WorkflowEdgeResponse> {

    public WorkflowEdgeTools(WorkflowEdgeRestService restService, ObjectMapper objectMapper) {
        super("workflow_edge", WorkflowEdgeRequest.class, restService, objectMapper);
    }

    @Tool(name = "workflow_edge_query_by_uid", description = "Query workflow_edge by uid. This tool returns structured data for AI tool invocation.")
    public Object workflowEdgeQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "workflow_edge_query_by_org", description = "Query workflow_edge by org with request json. This tool returns structured data for AI tool invocation.")
    public Object workflowEdgeQueryByOrg(@ToolParam(description = "WorkflowEdgeRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "workflow_edge_query_by_user", description = "Query workflow_edge by user with request json. This tool returns structured data for AI tool invocation.")
    public Object workflowEdgeQueryByUser(@ToolParam(description = "WorkflowEdgeRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "workflow_edge_create", description = "Create workflow_edge with request json. This tool returns structured data for AI tool invocation.")
    public Object workflowEdgeCreate(@ToolParam(description = "WorkflowEdgeRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "workflow_edge_update", description = "Update workflow_edge with request json. This tool returns structured data for AI tool invocation.")
    public Object workflowEdgeUpdate(@ToolParam(description = "WorkflowEdgeRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "workflow_edge_delete_by_uid", description = "Delete workflow_edge by uid. This tool returns structured data for AI tool invocation.")
    public Object workflowEdgeDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
