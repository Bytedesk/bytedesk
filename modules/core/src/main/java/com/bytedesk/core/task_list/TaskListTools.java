package com.bytedesk.core.task_list;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.bytedesk.core.base.BaseTools;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class TaskListTools extends BaseTools<TaskListRequest, TaskListResponse> {

    public TaskListTools(TaskListRestService restService, ObjectMapper objectMapper) {
        super("taskList", TaskListRequest.class, restService, objectMapper);
    }

    @Tool(description = "Query taskList by uid")
    public Object taskListQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query taskList by org with request json")
    public Object taskListQueryByOrg(@ToolParam(description = "TaskListRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query taskList by user with request json")
    public Object taskListQueryByUser(@ToolParam(description = "TaskListRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create taskList with request json")
    public Object taskListCreate(@ToolParam(description = "TaskListRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update taskList with request json")
    public Object taskListUpdate(@ToolParam(description = "TaskListRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete taskList by uid")
    public Object taskListDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
