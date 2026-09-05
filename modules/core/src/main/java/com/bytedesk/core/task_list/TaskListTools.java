package com.bytedesk.core.task_list;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TaskListTools extends BaseTools<TaskListRequest, TaskListResponse> {

    public TaskListTools(TaskListRestService restService, ObjectMapper objectMapper) {
        super("task_list", TaskListRequest.class, restService, objectMapper);
    }

    @Tool(name = "task_list_query_by_uid", description = "Query task_list by uid. This tool returns structured data for AI tool invocation.")
    public Object taskListQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(name = "task_list_query_by_org", description = "Query task_list by org with request json. This tool returns structured data for AI tool invocation.")
    public Object taskListQueryByOrg(@ToolParam(description = "TaskListRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(name = "task_list_query_by_user", description = "Query task_list by user with request json. This tool returns structured data for AI tool invocation.")
    public Object taskListQueryByUser(@ToolParam(description = "TaskListRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(name = "task_list_create", description = "Create task_list with request json. This tool returns structured data for AI tool invocation.")
    public Object taskListCreate(@ToolParam(description = "TaskListRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(name = "task_list_update", description = "Update task_list with request json. This tool returns structured data for AI tool invocation.")
    public Object taskListUpdate(@ToolParam(description = "TaskListRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(name = "task_list_delete_by_uid", description = "Delete task_list by uid. This tool returns structured data for AI tool invocation.")
    public Object taskListDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
