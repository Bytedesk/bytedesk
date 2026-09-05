package com.bytedesk.core.task;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TaskTools extends BaseTools<TaskRequest, TaskResponse> {

    public TaskTools(TaskRestService restService, ObjectMapper objectMapper) {
        super("task", TaskRequest.class, restService, objectMapper);
    }

    @Tool(name = "task_query_by_uid", description = "Query task by uid. This tool returns structured data for AI tool invocation.")
    public Object taskQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "task_query_by_org", description = "Query task by org with request json. This tool returns structured data for AI tool invocation.")
    public Object taskQueryByOrg(@ToolParam(description = "TaskRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "task_query_by_user", description = "Query task by user with request json. This tool returns structured data for AI tool invocation.")
    public Object taskQueryByUser(@ToolParam(description = "TaskRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "task_create", description = "Create task with request json. This tool returns structured data for AI tool invocation.")
    public Object taskCreate(@ToolParam(description = "TaskRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "task_update", description = "Update task with request json. This tool returns structured data for AI tool invocation.")
    public Object taskUpdate(@ToolParam(description = "TaskRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "task_delete_by_uid", description = "Delete task by uid. This tool returns structured data for AI tool invocation.")
    public Object taskDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
