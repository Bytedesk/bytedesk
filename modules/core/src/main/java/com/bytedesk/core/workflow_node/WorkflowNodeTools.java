package com.bytedesk.core.workflow_node;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class WorkflowNodeTools extends BaseTools<WorkflowNodeRequest, WorkflowNodeResponse> {

    public WorkflowNodeTools(WorkflowNodeRestService restService, ObjectMapper objectMapper) {
        super("workflow_node", WorkflowNodeRequest.class, restService, objectMapper);
    }

    @Tool(name = "workflow_node_query_by_uid", description = "Query workflow_node by uid. This tool returns structured data for AI tool invocation.")
    public Object workflowNodeQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "workflow_node_query_by_org", description = "Query workflow_node by org with request json. This tool returns structured data for AI tool invocation.")
    public Object workflowNodeQueryByOrg(@ToolParam(description = "WorkflowNodeRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "workflow_node_query_by_user", description = "Query workflow_node by user with request json. This tool returns structured data for AI tool invocation.")
    public Object workflowNodeQueryByUser(@ToolParam(description = "WorkflowNodeRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "workflow_node_create", description = "Create workflow_node with request json. This tool returns structured data for AI tool invocation.")
    public Object workflowNodeCreate(@ToolParam(description = "WorkflowNodeRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "workflow_node_update", description = "Update workflow_node with request json. This tool returns structured data for AI tool invocation.")
    public Object workflowNodeUpdate(@ToolParam(description = "WorkflowNodeRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "workflow_node_delete_by_uid", description = "Delete workflow_node by uid. This tool returns structured data for AI tool invocation.")
    public Object workflowNodeDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
