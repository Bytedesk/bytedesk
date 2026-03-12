package com.bytedesk.core.workflow_node;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WorkflowNodeTools extends BaseTools<WorkflowNodeRequest, WorkflowNodeResponse> {

    public WorkflowNodeTools(WorkflowNodeRestService restService, ObjectMapper objectMapper) {
        super("workflowNode", WorkflowNodeRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query workflowNode by uid")
    public Object workflowNodeQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query workflowNode by org with request json")
    public Object workflowNodeQueryByOrg(@ToolParam(description = "WorkflowNodeRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query workflowNode by user with request json")
    public Object workflowNodeQueryByUser(@ToolParam(description = "WorkflowNodeRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create workflowNode with request json")
    public Object workflowNodeCreate(@ToolParam(description = "WorkflowNodeRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update workflowNode with request json")
    public Object workflowNodeUpdate(@ToolParam(description = "WorkflowNodeRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete workflowNode by uid")
    public Object workflowNodeDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
