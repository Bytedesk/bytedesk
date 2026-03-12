package com.bytedesk.core.workflow_edge;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WorkflowEdgeTools extends BaseTools<WorkflowEdgeRequest, WorkflowEdgeResponse> {

    public WorkflowEdgeTools(WorkflowEdgeRestService restService, ObjectMapper objectMapper) {
        super("workflowEdge", WorkflowEdgeRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query workflowEdge by uid")
    public Object workflowEdgeQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query workflowEdge by org with request json")
    public Object workflowEdgeQueryByOrg(@ToolParam(description = "WorkflowEdgeRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query workflowEdge by user with request json")
    public Object workflowEdgeQueryByUser(@ToolParam(description = "WorkflowEdgeRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create workflowEdge with request json")
    public Object workflowEdgeCreate(@ToolParam(description = "WorkflowEdgeRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update workflowEdge with request json")
    public Object workflowEdgeUpdate(@ToolParam(description = "WorkflowEdgeRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete workflowEdge by uid")
    public Object workflowEdgeDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
