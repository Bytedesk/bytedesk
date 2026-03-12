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

    @Tool(description = "Query task by uid")
    public Object taskQueryByUid(
            @ToolParam(description = "uid") String uid,
            @ToolParam(description = "orgUid", required = false) String orgUid) {
        return doQueryByUid(uid, orgUid);
    }

    @Tool(description = "Query task by org with request json")
    public Object taskQueryByOrg(@ToolParam(description = "TaskRequest json") String requestJson) {
        return doQueryByOrg(requestJson);
    }

    @Tool(description = "Query task by user with request json")
    public Object taskQueryByUser(@ToolParam(description = "TaskRequest json") String requestJson) {
        return doQueryByUser(requestJson);
    }

    @Tool(description = "Create task with request json")
    public Object taskCreate(@ToolParam(description = "TaskRequest json") String requestJson) {
        return doCreate(requestJson);
    }

    @Tool(description = "Update task with request json")
    public Object taskUpdate(@ToolParam(description = "TaskRequest json") String requestJson) {
        return doUpdate(requestJson);
    }

    @Tool(description = "Delete task by uid")
    public Object taskDeleteByUid(@ToolParam(description = "uid") String uid) {
        return doDeleteByUid(uid);
    }
}
